## Spring Boot Jersey Application.
### Persistence Layer: 
1. Java Collection - ConcurrentHashMap.
2. SQL server - embedded H2 SQL engine.

### Code Quality Report
Code Quality report link, it is available for collaborates Github users:</br>
https://sonarcloud.io/dashboard?id=alorchenkov_java-refactoring-test
</br> There is Code Coverage section there as well.

### Cloud Deployment (MS Azure)
The application is deployed and running in the Cloud (MS Azure) with SQL database
as Persistence layer:</br>
https://java-refactoring-test-1578179374175.azurewebsites.net/swagger/index.html
</br> Please, select HTTPS scheme when try out the API with Swagger UI.

### How to run:
Required pre-installed software: maven and Java 8+.
1. Download/clone project;
2. Go to the folder with the pom.xml file: .../java-refactoring-test/
3. The application has two Spring profiles, default one for the Persistence Layer based on Java ConcurrentHashMap
and 'db-dao' for SQL embedded engine (H2)
- SQL Persistence Layer profile command:
```
mvn clean package && java -Dspring.profiles.active=db-dao -Dlogging.level.com.h2rd.refactoring=DEBUG -jar target/java-refactoring-test-0.0.1-SNAPSHOT.war
```
- Java Collection Persistence Layer profile command:
```
mvn clean package && java -Dlogging.level.com.h2rd.refactoring=DEBUG -jar target/java-refactoring-test-0.0.1-SNAPSHOT.war
 ```
4. Open in browser http://localhost:8080/swagger/index.html
5. Use Swagger or any REST client (POSTMAN, soapUI, etc) and <b>POST</b> http://localhost:8080/rest/users/ operation to populate the embedded store.
6. POSTMAN example of User request:
```xml
                                    <user>
                                        <name>Test Name</name>
                                        <email>test@test.name</email>
                                        <roles>testrole</roles>
                                    </user>
```
### Local Code Coverage Report
How to see Code Coverage report locally:
1. Go to the folder with the pom.xml file: .../java-refactoring-test/
2. Run mvn clean package
3. Open in browser html report: java-refactoring-test/target/site/jacoco/index.html
