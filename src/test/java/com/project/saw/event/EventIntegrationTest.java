package com.project.saw.event;

import com.project.saw.dto.event.CreateEventRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.time.LocalDate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("Should successfully create an event")
    public void testCreateEventAfterUserLogin() throws Exception {
        // Given
        String username = "organizatortest3";
        String password = "251512251";

        // Create event request
        CreateEventRequest request = new CreateEventRequest("Test Eventx10", "Test Locationx10", 10.0, LocalDate.now(), LocalDate.now().plusDays(1), 5, "Test Description");

        // When - calling HTTP request to create an event
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<CreateEventRequest> httpEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Event> response = testRestTemplate.exchange("http://localhost:" + port + "/event", HttpMethod.POST, httpEntity, Event.class);

        // Then - verifying the result
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Event responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getTitle()).isEqualTo("Test Eventx10");
        assertThat(responseBody.getLocation()).isEqualTo("Test Locationx10");
        assertThat(responseBody.getPrice()).isEqualTo(10.0);
        assertThat(responseBody.getStartingDate()).isEqualTo(LocalDate.now());
        assertThat(responseBody.getEndingDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(responseBody.getSeatingCapacity()).isEqualTo(5);
        assertThat(responseBody.getDescription()).isEqualTo("Test Description");

        assertThat(eventRepository.findByTitleIgnoreCase("Test Event")).isPresent();

        eventRepository.delete(responseBody);
    }
}