## Java Coding Test with Spring

### Description

REST API to extract data from GitHub repository.

The project was developed using Spring Boot 2.5.0 and Java 1.11 for a Rest API.

Spring Boot's DevTools, Starter Web, and Starter Test modules were used, in addition to Lombok and JUnit.

### API Data Endpoint

- Address: http://localhost:8080

- The REST API has the following endpoint that receives and responds in JSON format, using a ResponseEntity to generate the endpoint's response:

- `POST /counter`: The endpoint processes the data request from GitHub,
  The body of the request must contain the field "linkRepository" with a valid URL.

### Architecture

The project has the following structure:

Link to Swagger Hub: https://app.swaggerhub.com/apis-docs/kassioschaider/Trustly/1.0.0

- Packs:
    - controller: the RestController of the API.
    - model: the API's domain classes. Lombok annotations were used to generate Getters, Setters, Constructors, Equals and HashCode.
    - service: Refers to the Services classes of the API. It contains the packages:
        - impl: the implementation of the methods defined in the Service interfaces and implementation of the data extraction Thread (DataGitFileTask.java).
    - util: API's support classes and interfaces.

### Upcoming Implementations

- Implement Kafka for Messaging/Streams.
- Implement Threads to traverse the GitHub repository.
- Improve filters and regex.
- Add persistence layer and cache.
