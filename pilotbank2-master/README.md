## Intro

Welcome to the PilotBank README. The author hopes the following will help others
who have come after to continue the great work being done here.

## Project Setup

This project makes heavy use of the [Project Lombok](https://projectlombok.org/)
library. As such, it is required that you install the Lombok plugin for your IDE
of choice. 

Baeldung has an [article](https://www.baeldung.com/lombok-ide) for setup and 
installation for IntelliJ and Eclipse. The IntelliJ instructions have been 
included here because of the author's familiarity with the process.

For IntelliJ Community Edition:
* Go to File / Settings
* Use the search bar at top for "Plugins"
* Check the installed plugins to see if Lombok by Michail Plushnikov is present
  * If it is not present, go to the Marketplace tab, and search for 'Lombok'.
* Return to settings:
  * Search for 'Build, Execution, Deployment'
  * Click 'Compiler'
  * Click 'Anotation Processors'
  * Ensure 'Enable anotation processing' is checked

## Using Swagger

Swagger is an API documentation tool that is part of the OpenAPI specification.
This project uses SpringDoc Swagger-UI which conforms to the OpenAPI 3.0 
specification. 

Upon starting the PilotBank Application, browse to 
[Swagger-UI](http://localhost:8082/swagger-ui/)

The `/users/customer/create/` endpoint can be used to create a new customer
in order to login to the system.

The `users/login/` endpoint will accept your newly created customer's username 
and password and return a JWT-Token. This can then be used with the `Authorize`
button to enable the use of the other endpoints.

## Using H2 Database

PilotBank uses an embedded file H2 database to provide persistent data for the 
application. In order to login, perform the following steps:

* With the back-end application running, navigate to [H2](http://localhost:8082/h2)
  * In the JDBC URL field, copy and paste the following 
  `jdbc:h2:file:./src/main/resources/db/pilotbank-db`
  * Consult the [application.properties](http://git.fdmgroup.com/Jay.Patel2/pilotbank2/blob/master/pilotbank2/src/main/resources/application.properties) 
  file for the username and password
