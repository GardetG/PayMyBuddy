# Pay My Buddy
![alt text](https://user.oc-static.com/upload/2019/10/21/15716565363662_image1.png)
Pay My Buddy is a web application allowing our clients to easily transfer moneys for managing their finances or paying their friends.
This project include a front-end built with Angular and a back-end built with Java, Spring Boot, Spring Security, Spring JPA and Maven.

## Diagrams and Documentation

### Physical Data Model

![alt text](https://gardetg.github.io/PayMyBuddy/PhysicalDataModel.png)

### UML Class Diagram

![alt text](https://gardetg.github.io/PayMyBuddy/UMLClassDiagram.png)

### JavaDoc

The JavaDoc is available [here](https://gardetg.github.io/PayMyBuddy/).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisities

This project is built with:

- IntellJ
- Java 11
- Maven 3.8.3
- Spring Boot 2.5.6
- MySQL 8.0.26
- Angular 13.1.2
- BootStrap 5

### Installing

#### Setting Up Database
Execute the SQL script locate in: `.\src\main\resources`  
- [schema.sql](https://github.com/GardetG/PayMyBuddy/blob/release/1.0/src/main/resources/data.sql) : To generate the schema and tables of the database  
- [data.sql](https://github.com/GardetG/PayMyBuddy/blob/release/1.0/src/main/resources/schema.sql) : To insert an Admin (email: admin@mail.com, password: password) and a user (email: johndoe@mail.com, password: password)

#### Install npm to use angular
From the root folder, open /Front-End:  
`cd .\Front-End\`  
Install npm with the command:
`npm install`

### Running the Application

#### Running the back-end

To run the back-end API we need some arguments:
- `db.username` : username of the MySQL database
- `db.password` : password of the MySQL database
- `app.keyword` : keyword for the encryption of bank details

/!\ Run the application with another keyword while data are encrypted in the database will prevent the application to fetch the data correctly /!\

From the root folder containing the pom.xml, run the application with maven command:  
`mvn spring-boot:run "-Dspring-boot.run.arguments='--db.username=root' '--db.password=rootroot' '--app.keyword=paymybuddy_crypt'"`

#### Running the front-end

Go in the front-End folder:  
`cd .\Front-End\`

Run the front-end server with angular command:  
`ng serve --open`

The web browser open on the login page. From this page you can log as admin or user with the testing account add by `date.sql` or register a new user.