package com.biopix.telemedicine_backend;

import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
@WithPostgresContainer
class TelemedicineBackendApplicationTests {

	@Test
	void contextLoads() {
	}

	//Spring modulith structural validation
	@Test
	void verifyModuleArchitecture(){
		//Scan project's package structure
		ApplicationModules modules = ApplicationModules.of(TelemedicineBackendApplication.class);

		modules.verify();
	}

}
