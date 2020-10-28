# Wave Software Development Challenge

This repository contains a Java 11 Spring Boot app managed with the Maven Build tool. It connects with a Postgres database. Together they act as a prototype payroll system API.
## Part I: Instructions on how to build/run my application

These commands have been tested on a Mac. Other systems may require slightly different set up.

### Initialize the Database Locally

You will need to install Postgres locally and initialize a database locally to run this app. This can be done by following these steps:

1. Create a user with admin privileges.
    ```
    $ createuser --interactive --password postgresUser
    Shall the new role be a superuser? (y/n) y
    Password: 

    ```

1. Create a new database owned by that user.
    ```
    $ createdb payrolldb -O postgresUser
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



### Building and Running the Spring Boot App

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

##Part II: Answers to the challenge questions:

1. How did you test that your implementation was correct?
I tested the app using an application called insomnia to check each end point.

1. If this application was destined for a production environment, what would you add or change?
There are a few things I would like to add to this application if it were destined for a production environment:
    - Automated tests to the app to make sure the current functionality of the app continues to act as expected as more functionality is added to it in the future.
    - A Docker process to enable running the app on different platforms.
     
1. What compromises did you have to make as a result of the time constraints of this challenge?
In addition to not having time to implement the components listed in the previous question, given more time I would have liked to add:
    - More detailed error messages.
    - Functionality to retrieve custom Payroll Reports for specific employees, or time periods.
    - Options to sort the Payroll Reports by either date or employee number.