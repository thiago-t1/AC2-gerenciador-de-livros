# Gerenciador de Livros - AC2 Mobile

Atividade da disciplina de Mobile (AC2). O app serve para cadastrar os livros que eu li,
estou lendo ou quero ler, com tudo salvo no Firebase (Firestore), não em memória.

## O que o app faz

- Cadastra livro com título, autor, gênero, ano de publicação, status de leitura e se é
  favorito.
- Lista todos os livros cadastrados.
- Clicando num livro da lista, carrega ele pra editar.
- Segurando o dedo (clique longo) no livro, pergunta se quer excluir.
- Tem filtro por gênero, filtro de favoritos e busca por título.
- Valida os campos obrigatórios (não deixa salvar título/autor vazio, sem gênero ou status
  selecionado, etc).
- Mostra quantos livros estão cadastrados no total.

## Como rodar

1. Abre o Android Studio e usa `File > Open`, escolhe a pasta do projeto.
2. Deixa o Gradle sincronizar sozinho (baixa as dependências na primeira vez).
3. Precisa de um projeto no Firebase pra funcionar:
   - cria um projeto em https://console.firebase.google.com
   - adiciona um app Android com o pacote `com.fabricio.gerenciadorlivros`
   - baixa o `google-services.json` e coloca dentro da pasta `app/`
   - no console, ativa o Firestore Database (modo de teste)
   - já deixei meu `google-services.json` no repositório pra facilitar, mas caso não
     funcione é só gerar o seu
4. Sincroniza o projeto de novo e roda num emulador ou celular.

## Estrutura

- `Livro.java` - classe do livro (os campos que ele tem).
- `LivroAdapter.java` - adapter que monta a lista de livros na tela.
- `MainActivity.java` - onde está tudo: formulário, lista, filtros, busca e o CRUD
  com o Firestore.
- `res/layout/activity_main.xml` - tela principal (formulário + lista).
- `res/layout/item_livro.xml` - como cada livro aparece na lista.
- `res/values/arrays.xml` - as opções dos spinners de gênero e status.

## Extras que escolhi fazer

- Filtro de favoritos
- Busca por título

Além desses dois, também fiz o diálogo de confirmação antes de excluir e o contador do
total de livros, que ajudam bastante na apresentação.
