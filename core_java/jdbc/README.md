# Introduction
The Stock Quote application is a command-line utility that enables users to manage a simulated stock
portfolio. IT retrieves up-to-date stock information through the Alpha Vantage API and utilizes JDBC to store this
data in a PostgreSQL database. The project is built using Maven for managing dependencies and Docker
for containerization, facilitating easy deployment and portability. Built with Java 11 and utilized JUnit for unit testing
and Mockito for mocking and verifying interactions, ensuring reliability and robustness in the application's
functionality.

# Implementaiton
The application is developed with Java 11. It employs the Jackson Databind library for efficient serialization and deserialization 
of stock data retrieved from the Alpha Vantage API. Maven is used for dependency management, incorporating 
libraries such as JUnit for testing, Mockito for mocking, SLF4J for basic logging, and LOG4j for advanced logging functionalities. 
Following standard JDBC practices, the application's structure is organized into Data Access Objects (DAOs), Services, and Controllers, 
ensuring a clear separation of concerns.

## ER Diagram
![Stock Quote ERD](jdbc/src/main/resources/Stock_quote_ERD.png)

## Design Patterns
The application utilizes both the Data Access Object (DAO) and Repository design patterns. 
The DAO pattern is employed to separate data access logic from business logic, encapsulating all database interactions 
within specific DAO classes. This approach enhances modularity and maintainability by keeping data access concerns distinct 
from core business functionality. Meanwhile, the Repository pattern adds an abstraction layer that simplifies data source interactions, 
allowing the application to work with business domain objects instead of direct database operations. 
This design choice improves testability and flexibility by enabling the use of mock repositories in unit tests and facilitating easier 
modifications to the data access layer.

# Test
Testing is performed with JUnit and Mockito to validate the application's functionality with the PostgreSQL database. 
Test data is inserted and manipulated in the database to simulate different scenarios, and assertions are used to verify 
the application's operations. CRUD operations are rigorously tested to ensure accurate database interactions. Mockito is 
employed to mock external dependencies, enabling isolated unit testing. Integration tests check end-to-end functionality, 
focusing on the interaction between various components and layers of the application, ensuring thorough coverage and reliability.