package com.fabricio.gerenciadorlivros;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LivroAdapter.OnItemClickListener {

    private static final String COLECAO_LIVROS = "livros";

    private EditText etTitulo, etAutor, etAno, etBusca;
    private Spinner spGenero, spStatus, spFiltroGenero;
    private CheckBox cbFavorito, cbSoFavoritos;
    private TextView tvTotal;
    private RecyclerView rvLivros;

    private FirebaseFirestore db;
    private CollectionReference livrosRef;

    private LivroAdapter adapter;
    private final List<Livro> todosLivros = new ArrayList<>();

    private String livroEmEdicaoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        livrosRef = db.collection(COLECAO_LIVROS);

        bindViews();
        configurarSpinners();
        configurarLista();
        configurarListeners();

        carregarLivros();
    }

    private void bindViews() {
        etTitulo = findViewById(R.id.etTitulo);
        etAutor = findViewById(R.id.etAutor);
        etAno = findViewById(R.id.etAno);
        etBusca = findViewById(R.id.etBusca);

        spGenero = findViewById(R.id.spGenero);
        spStatus = findViewById(R.id.spStatus);
        spFiltroGenero = findViewById(R.id.spFiltroGenero);

        cbFavorito = findViewById(R.id.cbFavorito);
        cbSoFavoritos = findViewById(R.id.cbSoFavoritos);

        tvTotal = findViewById(R.id.tvTotal);
        rvLivros = findViewById(R.id.rvLivros);
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> generoAdapter = ArrayAdapter.createFromResource(
                this, R.array.generos, android.R.layout.simple_spinner_item);
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenero.setAdapter(generoAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.status_leitura, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        ArrayAdapter<CharSequence> filtroAdapter = ArrayAdapter.createFromResource(
                this, R.array.generos_filtro, android.R.layout.simple_spinner_item);
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltroGenero.setAdapter(filtroAdapter);
    }

    private void configurarLista() {
        adapter = new LivroAdapter(this);
        rvLivros.setLayoutManager(new LinearLayoutManager(this));
        rvLivros.setAdapter(adapter);
        rvLivros.setNestedScrollingEnabled(false);
    }

    private void configurarListeners() {
        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvarLivro());
        findViewById(R.id.btnLimpar).setOnClickListener(v -> limparFormulario());

        AdapterView.OnItemSelectedListener filtroListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spFiltroGenero.setOnItemSelectedListener(filtroListener);

        cbSoFavoritos.setOnCheckedChangeListener((buttonView, isChecked) -> aplicarFiltros());

        etBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // ===================== CRUD (Firebase Firestore) =====================

    private void carregarLivros() {
        livrosRef.get()
                .addOnSuccessListener(this::onLivrosCarregados)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar livros: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void onLivrosCarregados(QuerySnapshot snapshot) {
        todosLivros.clear();
        for (QueryDocumentSnapshot doc : snapshot) {
            Livro livro = doc.toObject(Livro.class);
            livro.setId(doc.getId());
            todosLivros.add(livro);
        }
        aplicarFiltros();
    }

    private void salvarLivro() {
        String titulo = etTitulo.getText().toString().trim();
        String autor = etAutor.getText().toString().trim();
        String anoTexto = etAno.getText().toString().trim();
        String genero = spGenero.getSelectedItem().toString();
        String status = spStatus.getSelectedItem().toString();
        boolean favorito = cbFavorito.isChecked();

        if (!validarCampos(titulo, autor, anoTexto, genero, status)) {
            return;
        }

        int ano = Integer.parseInt(anoTexto);
        Livro livro = new Livro(titulo, autor, genero, ano, status, favorito);

        if (livroEmEdicaoId == null) {
            livrosRef.add(livro)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Livro cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                        limparFormulario();
                        carregarLivros();
                    })
                    .addOnFailureListener(mostrarErro("cadastrar"));
        } else {
            livrosRef.document(livroEmEdicaoId).set(livro)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        limparFormulario();
                        carregarLivros();
                    })
                    .addOnFailureListener(mostrarErro("atualizar"));
        }
    }

    private void excluirLivro(Livro livro) {
        livrosRef.document(livro.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Livro excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarLivros();
                })
                .addOnFailureListener(mostrarErro("excluir"));
    }

    private OnFailureListener mostrarErro(String acao) {
        return e -> Toast.makeText(this,
                "Erro ao " + acao + " livro: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    // ===================== Validações =====================

    private boolean validarCampos(String titulo, String autor, String anoTexto,
                                   String genero, String status) {
        if (titulo.isEmpty()) {
            etTitulo.setError("Informe o título do livro");
            etTitulo.requestFocus();
            return false;
        }
        if (autor.isEmpty()) {
            etAutor.setError("Informe o autor do livro");
            etAutor.requestFocus();
            return false;
        }
        if ("Selecione o gênero".equals(genero)) {
            Toast.makeText(this, "Selecione um gênero válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (anoTexto.isEmpty()) {
            etAno.setError("Informe o ano de publicação");
            etAno.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(anoTexto);
        } catch (NumberFormatException e) {
            etAno.setError("Ano inválido");
            etAno.requestFocus();
            return false;
        }
        if ("Selecione o status".equals(status)) {
            Toast.makeText(this, "Selecione um status de leitura válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // ===================== Formulário =====================

    private void limparFormulario() {
        etTitulo.setText("");
        etAutor.setText("");
        etAno.setText("");
        spGenero.setSelection(0);
        spStatus.setSelection(0);
        cbFavorito.setChecked(false);
        livroEmEdicaoId = null;
    }

    private void preencherFormularioParaEdicao(Livro livro) {
        livroEmEdicaoId = livro.getId();
        etTitulo.setText(livro.getTitulo());
        etAutor.setText(livro.getAutor());
        etAno.setText(String.format(Locale.getDefault(), "%d", livro.getAnoPublicacao()));
        cbFavorito.setChecked(livro.isFavorito());

        ArrayAdapter generoAdapter = (ArrayAdapter) spGenero.getAdapter();
        int posGenero = generoAdapter.getPosition(livro.getGenero());
        spGenero.setSelection(Math.max(posGenero, 0));

        ArrayAdapter statusAdapter = (ArrayAdapter) spStatus.getAdapter();
        int posStatus = statusAdapter.getPosition(livro.getStatusLeitura());
        spStatus.setSelection(Math.max(posStatus, 0));

        Toast.makeText(this, "Editando: " + livro.getTitulo(), Toast.LENGTH_SHORT).show();
    }

    // ===================== Filtros =====================

    private void aplicarFiltros() {
        String filtroGenero = spFiltroGenero.getSelectedItem() != null
                ? spFiltroGenero.getSelectedItem().toString() : "Todos";
        String busca = etBusca.getText().toString().trim().toLowerCase(Locale.getDefault());
        boolean apenasFavoritos = cbSoFavoritos.isChecked();

        List<Livro> filtrados = new ArrayList<>();
        for (Livro livro : todosLivros) {
            boolean passaGenero = "Todos".equals(filtroGenero) || livro.getGenero().equals(filtroGenero);
            boolean passaBusca = busca.isEmpty()
                    || livro.getTitulo().toLowerCase(Locale.getDefault()).contains(busca);
            boolean passaFavorito = !apenasFavoritos || livro.isFavorito();

            if (passaGenero && passaBusca && passaFavorito) {
                filtrados.add(livro);
            }
        }

        adapter.setLivros(filtrados);
        tvTotal.setText("Total de livros: " + filtrados.size() + " (de " + todosLivros.size() + " cadastrados)");
    }

    // ===================== Ações da lista =====================

    @Override
    public void onItemClick(Livro livro) {
        preencherFormularioParaEdicao(livro);
    }

    @Override
    public void onItemLongClick(Livro livro) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir livro")
                .setMessage("Tem certeza que deseja excluir \"" + livro.getTitulo() + "\"?")
                .setPositiveButton("Excluir", (dialog, which) -> excluirLivro(livro))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
