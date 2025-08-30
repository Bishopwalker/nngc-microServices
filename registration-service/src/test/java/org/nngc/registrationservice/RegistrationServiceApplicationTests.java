package org.nngc.registrationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = org.nngc.TestRegistrationServiceApplication.class
)
@ActiveProfiles("test")
class RegistrationServiceApplicationTests {

	@Test
	void contextLoads() {
		// Simple test to verify Spring context loads successfully
	}

}
