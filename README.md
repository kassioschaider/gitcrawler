## Avaliação de Codificação Java com Spring

### Descrição

API REST para extração de dados de repositório do GitHub.

O projeto foi desenvolvido usando Spring Boot 2.5.0 e Java 1.11 para uma API Rest.

Foram utilizados do Spring Boot os módulos DevTools, Starter Web e Starter Test, além do Lombok e JUnit.

### Endpoint de dados da API

- Endereço: http://localhost:8080

- A API REST possui o seguinte endpoint que recebe e responde no formato JSON, sendo que é utilizado um
  ResponseEntity para gerar a resposta do endpoint.

- `POST /counter`: O endpoint processa uma requisição de dados do GitHub,
  no corpo da requisição deve conter o campo "linkRepository", contendo uma URL válida.

### Arquitetura

O projeto a seguinte estrutura:

- Packs:
    - controller: o RestController da API.
    - model: as classes de domínio da API, sendo que são utilizados as anotações do Lombok para gerar Getters, Setters, Constructors, Equals e HashCode.
    - service: classes Services da API. Contém o pacote:
        - impl: implementação dos métodos definidos nas interfaces Service e implementação da task da Thread de extração dos dados (DataGitFileTask.java).
    - util: pacotes com as classes e interfaces de suporte a API.

### Próximas Implementações

- Implementar Kafka para Mensageria/Streams.
- Implementar Threads para percorer o repositório GitHub.
- Melhorar os filtros e regex.
- Adicionar camada de persistência e cache.
