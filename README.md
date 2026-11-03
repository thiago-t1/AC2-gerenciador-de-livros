# Gerenciador de Livros

App Android (Java) para cadastrar, listar, editar e excluir livros, com persistência no
Firebase Firestore.

## Passo a passo para rodar no Android Studio

1. **Abra o projeto**
   - Abra o Android Studio (o que já está instalado em `D:\Android`).
   - `File > Open...` e selecione a pasta `D:\facc`.
   - Deixe o Gradle sincronizar (ele vai baixar o wrapper e as dependências na primeira vez).

2. **Crie um projeto no Firebase**
   - Acesse https://console.firebase.google.com
   - Clique em "Adicionar projeto" e siga os passos (pode desativar o Google Analytics).

3. **Registre o app Android no Firebase**
   - Dentro do projeto Firebase, clique no ícone do Android para adicionar um app.
   - Nome do pacote (applicationId): `com.fabricio.gerenciadorlivros`
   - Baixe o arquivo `google-services.json` gerado.
   - Copie esse arquivo para `D:\facc\app\google-services.json` (dentro da pasta `app`,
     no mesmo nível de `build.gradle.kts`).

4. **Ative o Firestore**
   - No console do Firebase, vá em "Firestore Database" > "Criar banco de dados".
   - Escolha o modo de teste (test mode) para simplificar as regras durante o desenvolvimento.

5. **Sincronize novamente**
   - Volte ao Android Studio e clique em "Sync Project with Gradle Files" (ícone do elefante
     com a setinha, ou `File > Sync Project with Gradle Files`).

6. **Rode no emulador**
   - Selecione o emulador já configurado (pasta `D:\Android\avd`) no seletor de dispositivos.
   - Clique em "Run" (▶) para instalar e executar o app.

## Estrutura do projeto

- `Livro.java` — modelo de dados do livro.
- `LivroAdapter.java` — adapter do RecyclerView que exibe a lista de livros.
- `MainActivity.java` — tela única com formulário de cadastro/edição, lista, filtros,
  busca e integração com o Firestore (CRUD completo).
- `res/layout/activity_main.xml` — layout do formulário, filtros e lista.
- `res/layout/item_livro.xml` — layout de cada item da lista.
- `res/values/arrays.xml` — opções dos Spinners (gênero e status de leitura).

## Funcionalidades implementadas

- Cadastro de livro (título, autor, gênero, ano, status de leitura, favorito).
- Listagem dos livros com título, autor, gênero, ano e status.
- Filtro por gênero (Spinner).
- Clique curto no item: carrega os dados para edição.
- Clique longo no item: pede confirmação e exclui.
- Validações de campos obrigatórios com mensagens ao usuário.
- Extras escolhidos:
  - Filtro para mostrar apenas livros favoritos.
  - Busca por título.
  - (bônus) Diálogo de confirmação antes de excluir.
  - (bônus) Exibição da quantidade total de livros cadastrados.

## Observação sobre o `google-services.json`

Esse arquivo está no `.gitignore` por conter configurações específicas do projeto Firebase.
Se for necessário entregar o repositório com ele incluso para o professor rodar sem precisar
criar o próprio projeto Firebase, remova a linha `google-services.json` do `.gitignore` antes
de comitar.
