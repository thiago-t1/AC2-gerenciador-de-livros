package com.fabricio.gerenciadorlivros;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LivroAdapter extends RecyclerView.Adapter<LivroAdapter.LivroViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Livro livro);
        void onItemLongClick(Livro livro);
    }

    private final List<Livro> livros = new ArrayList<>();
    private final OnItemClickListener listener;

    public LivroAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLivros(List<Livro> novaLista) {
        livros.clear();
        livros.addAll(novaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LivroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_livro, parent, false);
        return new LivroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LivroViewHolder holder, int position) {
        Livro livro = livros.get(position);

        holder.tvTitulo.setText(livro.getTitulo());
        holder.tvAutor.setText(livro.getAutor());
        holder.tvGeneroAno.setText(livro.getGenero() + " • " + livro.getAnoPublicacao());
        holder.tvStatus.setText(livro.getStatusLeitura());
        holder.tvFavorito.setVisibility(livro.isFavorito() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(livro));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(livro);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return livros.size();
    }

    static class LivroViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvAutor, tvGeneroAno, tvStatus, tvFavorito;
        CardView cardView;

        LivroViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvAutor = itemView.findViewById(R.id.tvAutor);
            tvGeneroAno = itemView.findViewById(R.id.tvGeneroAno);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvFavorito = itemView.findViewById(R.id.tvFavorito);
        }
    }
}
