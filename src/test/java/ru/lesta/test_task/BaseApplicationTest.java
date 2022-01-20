package ru.lesta.test_task;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseApplicationTest {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @LocalServerPort
    protected int port;

    public static String matchMakerUrl;

    @BeforeEach
    public void setupUrl() {
        matchMakerUrl = "http://localhost:" + port + "/matchmaker/users";
    }

}
