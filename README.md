# Music Vault

Music Vault é um aplicativo Android nativo desenvolvido em Java como projeto final da disciplina de Desenvolvimento para Dispositivos Móveis. A proposta do app é funcionar como um diário musical, permitindo que o usuário pesquise álbuns, salve seus favoritos, registre status de escuta, escreva resenhas e compartilhe suas reviews como imagem.

## Sobre o projeto

O Music Vault foi criado para organizar experiências musicais de forma simples e pessoal. O usuário pode buscar álbuns por meio da iTunes Search API, visualizar informações como capa, artista, gênero, data de lançamento e lista de músicas, e adicionar os álbuns ao seu acervo local.

Além disso, o aplicativo permite classificar os álbuns em diferentes status, atribuir notas, escrever reviews e compartilhar uma imagem personalizada da avaliação em outros aplicativos, como WhatsApp e redes sociais.

## Funcionalidades

* Pesquisa de álbuns utilizando a iTunes Search API;
* Exibição de detalhes do álbum;
* Exibição da lista de músicas do álbum;
* Salvamento de álbuns no acervo pessoal;
* Organização por status:

  * Quero ouvir;
  * Ouvindo;
  * Já ouvi;
* Cadastro de nota e resenha para álbuns;
* Edição e exclusão de álbuns salvos;
* Tela “Meu Vault” com os álbuns cadastrados;
* Filtro por status;
* Compartilhamento de review em formato de imagem;
* Persistência local utilizando SQLite.

## Tecnologias utilizadas

* Java;
* Android SDK;
* XML para construção das interfaces;
* SQLite para persistência local;
* Retrofit para consumo de API;
* Gson para conversão de JSON;
* Picasso para carregamento de imagens;
* RecyclerView para listagem de dados;
* iTunes Search API como API externa.

## API utilizada

O aplicativo utiliza a iTunes Search API para buscar informações de álbuns musicais.

A API é usada para obter dados como:

* Nome do álbum;
* Nome do artista;
* Capa do álbum;
* Gênero musical;
* Data de lançamento;
* Lista de músicas do álbum.

## Banco de dados local

O Music Vault utiliza SQLite para armazenar os álbuns salvos pelo usuário diretamente no dispositivo.

Os principais dados armazenados são:

* ID do álbum na iTunes;
* Nome do álbum;
* Artista;
* URL da capa;
* Gênero;
* Data de lançamento;
* Status;
* Nota;
* Resenha.

## Estrutura geral do app

O aplicativo é organizado em telas principais:

* **Tela inicial:** apresenta um resumo do diário musical do usuário;
* **Tela de busca:** permite pesquisar álbuns na iTunes API;
* **Tela de detalhes:** mostra as informações completas do álbum e permite salvar, avaliar e compartilhar a review;
* **Meu Vault:** lista os álbuns salvos e permite filtrar por status.

## Como executar o projeto

1. Clone este repositório:

```bash
git clone https://github.com/seu-usuario/music-vault.git
```

2. Abra o projeto no Android Studio.

3. Aguarde a sincronização do Gradle.

4. Execute o aplicativo em um emulador Android ou dispositivo físico.

## Permissões

O app utiliza permissão de internet para acessar a iTunes Search API:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Objetivo acadêmico

Este projeto foi desenvolvido com foco nos principais conceitos de desenvolvimento Android nativo, incluindo:

* Ciclo de vida de Activity;
* Construção de interfaces com XML;
* Consumo de API externa;
* Persistência local com SQLite;
* Manipulação de listas com RecyclerView;
* Organização de código em camadas;
* Interação do usuário com dados dinâmicos.

## Autores

Desenvolvido por Gustavo Alves Borges, Arthur Henrique Peres da Silva, Henrique Oliveira Silva.
