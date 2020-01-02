package test.com.h2rd.refactoring.integration;

import com.h2rd.refactoring.RefactorApplication;
import com.h2rd.refactoring.usermanagement.User;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RefactorApplication.class)
public final class UserIntegrationTest {
    private static final String BASE_PATH = "/users";

    @LocalServerPort
    int randomServerPort;

    @Before
    public void setUp() {
        addUsers();
    }

    @After
    public void cleanUp() {
        getUsers().forEach(user -> deleteUser(user.getEmail()));
    }


    @Test
    public void testGetUsers() {
        final RestTemplate testRestTemplate = new RestTemplate();

        final ResponseEntity<Collection<User>> response =
                testRestTemplate.exchange("http://localhost:" + randomServerPort + "/rest/" + BASE_PATH + "/find", HttpMethod.GET, null, new ParameterizedTypeReference<Collection<User>>() {
                });
        assertEquals(HttpStatus.OK_200.getStatusCode(), response.getStatusCode().value());

        final Collection<User> actual = response.getBody();
        final Collection<User> expected = getUsers();

        assertEquals(expected, actual);
    }

    @Test
    public void testSearchUser() {
        final RestTemplate testRestTemplate = new RestTemplate();
        final ResponseEntity<User> response =
                testRestTemplate.exchange("http://localhost:" + randomServerPort + "/rest/" + BASE_PATH + "/search?name=integration", HttpMethod.GET, null, new ParameterizedTypeReference<User>() {
                });

        assertEquals(HttpStatus.OK_200.getStatusCode(), response.getStatusCode().value());

        final User actual = response.getBody();

        assertEquals("initial@integration.com", actual.getEmail());
        assertEquals("integration", actual.getName());
    }

    @Test
    public void testFindUser404() {
        final RestTemplate testRestTemplate = new RestTemplateBuilder().errorHandler(new NoOpResponseErrorHandler()).build();

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + randomServerPort + "/rest" + BASE_PATH + "/search")
                .queryParam("name", "integration1");
        final ResponseEntity<String> response =

                testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, String.class);


        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteUser() {
        final RestTemplate testRestTemplate = new RestTemplateBuilder().errorHandler(new NoOpResponseErrorHandler()).build();

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + randomServerPort + "/rest" + BASE_PATH + "/delete")
                .queryParam("email", "initial@integration.com");
        final ResponseEntity<User> response =

                testRestTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, null, User.class);


        assertEquals(200, response.getStatusCodeValue());

        assertEquals(0, getUsers().size());
    }


    @Test
    public void createUserTest() {
        final User updated = new User();
        updated.setName("integration");
        updated.setEmail("updated@integration.com");
        updated.getRoles().add("testrole");

        final RestTemplate testRestTemplate = new RestTemplateBuilder().errorHandler(new NoOpResponseErrorHandler()).build();

        final ResponseEntity<User> result = testRestTemplate.exchange("http://localhost:" + randomServerPort + "/rest" + BASE_PATH + "/add", HttpMethod.POST, new HttpEntity<>(updated), User.class);
        assertEquals(200, result.getStatusCodeValue());

        assertEquals(2, getUsers().size());
    }

    @Test
    public void updateUserTest() {

        final User updated = new User();
        updated.setName("integration");
        updated.setEmail("initial@integration.com");
        updated.getRoles().add("testrole");

        final RestTemplate testRestTemplate = new RestTemplateBuilder().errorHandler(new NoOpResponseErrorHandler()).build();

        final ResponseEntity<User> result = testRestTemplate.exchange("http://localhost:" + randomServerPort + "/rest" + BASE_PATH + "/update", HttpMethod.PUT, new HttpEntity<>(updated), User.class);
        assertEquals(200, result.getStatusCodeValue());

        assertEquals(1, getUsers().size());
    }

    @Test
    public void updateUser404Test() {

        final User updated = new User();
        updated.setName("integration");
        updated.setEmail("updated1@integration.com");
        updated.getRoles().add("testrole");

        final RestTemplate testRestTemplate = new RestTemplateBuilder().errorHandler(new NoOpResponseErrorHandler()).build();

        final ResponseEntity<String> result = testRestTemplate.exchange("http://localhost:" + randomServerPort + "/rest" + BASE_PATH + "/update", HttpMethod.PUT, new HttpEntity<>(updated), String.class);
        assertEquals(404, result.getStatusCodeValue());

        assertEquals(1, getUsers().size());
    }


    public void addUsers() {

        final User integration = new User();
        integration.setName("integration");
        integration.setEmail("initial@integration.com");
        integration.getRoles().add("testrole");

        addUser(integration);
    }


    private static class NoOpResponseErrorHandler extends
            DefaultResponseErrorHandler {

        @Override
        public void handleError(final ClientHttpResponse response) {
        }

    }

    private Collection<User> getUsers() {
        final RestTemplate testRestTemplate = new RestTemplate();

        final ResponseEntity<Collection<User>> response =
                testRestTemplate.exchange("http://localhost:" + randomServerPort + "/rest/" + BASE_PATH + "/find", HttpMethod.GET, null, new ParameterizedTypeReference<Collection<User>>() {
                });
        assertEquals(HttpStatus.OK_200.getStatusCode(), response.getStatusCode().value());

        final Collection<User> result = response.getBody();
        return result;
    }

    public void deleteUser(final String email) {
        final RestTemplate testRestTemplate = new RestTemplateBuilder().errorHandler(new NoOpResponseErrorHandler()).build();

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + randomServerPort + "/rest" + BASE_PATH + "/delete")
                .queryParam("email", email);
        final ResponseEntity<User> response =

                testRestTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, null, User.class);
    }

    public void addUser(final User user) {
        final RestTemplate testRestTemplate = new RestTemplateBuilder().errorHandler(new NoOpResponseErrorHandler()).build();

        final ResponseEntity<User> result = testRestTemplate.exchange("http://localhost:" + randomServerPort + "/rest" + BASE_PATH + "/add", HttpMethod.POST, new HttpEntity<>(user), User.class);
    }
}
