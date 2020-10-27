# Wave Software Development Challenge

This repository contains a Java 11 Spring Boot app managed with the Maven Build tool. It connects with a Postgres database. Together they act as a prototype payroll system API.

## Initialize the Database Locally

You will need to install Postgres locally and initialize a database locally to run this app. This can be done by following these steps:

1. Create a user with admin privileges.
    ```
    $ createuser --interactive --password postgresUser
    Shall the new role be a superuser? (y/n) y
    Password: 

    ```

1. Create a new database owned by that user.
    ```
    $ postgres createdb payrolldb -O postgresUser
    ```
1. Create new tables.

    open the psql interactive terminal using the following command:
   ```
    $ psql payrolldb  -U postgresUser
   ```
    Run the following instructions in the psql interactive terminal
    ```
    DROP TABLE IF EXISTS PayrollReport;
    CREATE TABLE PayrollReport( id serial PRIMARY KEY, EmployeeID Integer, StartDate date, EndDate date, Amount NUMERIC(20, 2));
    DROP TABLE IF EXISTS LoggedTimeReport;
    CREATE TABLE LoggedTimeReport (id Integer PRIMARY Key);
    ```



## Building and Running the App

In order to connect the Spring Boot App to your Local Database Instance, you will to set the following environment variables. Example values shown below.

```
spring.datasource.url=jdbc:postgresql://localhost:5432/payrolldb
spring.datasource.username=postgresUser
spring.datasource.password=postgresPassword
```

In order to run and build this App you will need Maven and Java 11 installed.

Build the Spring Boot App. 

```
$ mvn package
```

Run the App.

```
$ java -jar target/payroll-0.0.1-SNAPSHOT.jar com.wave.payroll.PayrollApplication
```




### Using the API

A request with the following format can be used to upload time report data to the API
```
POST http://localhost:8081/upload
Content-Type: multipart/form-data
Properties: 
    file:
        type: File
        description: the time-report-x.csv formatted file
```   

A request with the following format can be used to retrieve a payroll report
```
GET http://localhost:8081/report
```     

