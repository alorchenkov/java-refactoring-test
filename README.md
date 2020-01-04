Spring Boot Jersey Application:
1. Persistence Layer: Java Collection.

Code Quality report link, it is available for collaborates Github users:
https://sonarcloud.io/dashboard?id=alorchenkov_java-refactoring-test

How to run:
Required pre-installed software: maven and Java 8+.
1. Download/clone project;
2. Go to the folder with the pom.xml file: .../java-refactoring-test/
3. The application has two Spring profiles, default one for the Persistence Layer based on Java ConcurrentHashMap
and 'db-dao' for SQL embedded engine (H2)
3.1. SQL Persistence Layer profile command:
```
mvn clean package && java -Dspring.profiles.active=db-dao -jar target/java-refactoring-test-0.0.1-SNAPSHOT.war
```
3.2. Java Collection Persistence Layer profile command:
```
mvn clean package && java -jar target/java-refactoring-test-0.0.1-SNAPSHOT.war
 ```
4. Open in browser http://localhost:8080/swagger/index.html
5. Use Swagger or any REST client (POSTMAN, soapUI, etc) and http://localhost:8080/rest/users/add operation to populate the embedded store.
6. POSTMAN example of User request:
```xml
                                    <user>
                                        <name>Test Name</name>
                                        <email>test@test.name</email>
                                        <roles>testrole</roles>
                                    </user>
```
How to see Code Coverage report:
1. Go to the folder with the pom.xml file: .../java-refactoring-test/
2. Run mvn clean package
3. Open in browser html report: java-refactoring-test/target/site/jacoco/index.html
