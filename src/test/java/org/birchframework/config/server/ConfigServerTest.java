/*===============================================================
 = Copyright (c) 2021 Birch Framework
 = This program is free software: you can redistribute it and/or modify
 = it under the terms of the GNU General Public License as published by
 = the Free Software Foundation, either version 3 of the License, or
 = any later version.
 = This program is distributed in the hope that it will be useful,
 = but WITHOUT ANY WARRANTY; without even the implied warranty of
 = MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 = GNU General Public License for more details.
 = You should have received a copy of the GNU General Public License
 = along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ==============================================================*/
package org.birchframework.config.server;

import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.cxf.jaxrs.client.WebClient;
import org.birchframework.framework.jaxrs.Responses;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

/**
 * Tests for {@link ConfigServer}.
 * @author Keivan Khalichi
 */
@SpringBootTest(properties = {"spring.cloud.config.enabled: true",
                              "spring.cloud.config.uri: http://localhost:" + ConfigServerTest.SERVER_PORT,
                              "management.security.enabled: false",
                              "management.endpoints.web.exposure.include: *",
                              "spring.config.name: config"})
@ActiveProfiles("it")
public class ConfigServerTest {

   static final int SERVER_PORT = 18888;

   private static ConfigurableApplicationContext configServer;

   /**
    * Setup before all tests.
    */
   @BeforeAll
   public static void beforeAll() {
      configServer = new SpringApplicationBuilder(ConfigServer.class).properties("server.port=" + SERVER_PORT,
                                                                                 "spring.profiles.active=native",
                                                                                 "spring.config.name=config",
                                                                                 "spring.application.name=${spring.config.name}",
                                                                                 "config.repo=classpath:src/test/resources/config/")
                                                                     .run();
   }

   @AfterAll
   public static void afterAll() {
      configServer.close();
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testMain() {
      final var aClient = WebClient.create(String.format("http://localhost:%d/native/config", SERVER_PORT));
      Responses.of(aClient.get()).ifOKOrElse(
         new TypeReference<SerializableEnvironment>() {},
         environment -> {
            assertThat(environment).isNotNull();
            final var aPropertySources = environment.getPropertySources();
            assertThat(aPropertySources).isNotNull();
            assertThat(aPropertySources).isNotEmpty();
            aPropertySources.stream()
                            .filter(ps -> ps.getName().contains("config/application.yml"))
                            .findFirst()
                            .ifPresentOrElse(ps -> {
                               final var aSource = (Map<String, Object>) ps.getSource();
                               assertThat(aSource).isNotEmpty();
                               assertThat(aSource).containsKey("birch.test.value");
                               final var aValue = aSource.get("birch.test.value");
                               assertThat(aValue).isInstanceOf(String.class);
                               assertThat((String) aValue).isNotBlank();
                               assertThat((String) aValue).isEqualTo("Hello!");
                            },
                            () -> fail("Expected property source 'config/application.yml', but it was not found"));
         },
         () -> fail("Unable to obtain property sources"));
   }
}