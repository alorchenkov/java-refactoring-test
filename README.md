Spring Boot Jersey Application:
1. Persistence Layer: Java Collection.

How to run:
Required pre-installed software: maven and Java 8+.
1. Download/clone project;
2. Go to the folder with the pom.xml file: java-refactoring-test/
3. Run mvn clean package && java -jar target/java-refactoring-test-0.0.1-SNAPSHOT.war
4. Open in browser http://localhost:8080/rest/users/find
5. Use any REST client and http://localhost:8080/rest/users/add operation to populate the embedded store.
