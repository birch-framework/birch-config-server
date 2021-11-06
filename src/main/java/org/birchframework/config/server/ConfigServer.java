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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Config server Spring Boot main application.
 * @author Keivan Khalichi
 */
@SpringBootApplication
@EnableConfigServer
@EnableConfigurationProperties(MultipleJGitEnvironmentProperties.class)
@Import(JacksonAutoConfiguration.class)
@Profile("!it")  // if active profile isn't 'it' (integration tests)
@Slf4j
public class ConfigServer {

   public ConfigServer(final MultipleJGitEnvironmentProperties theProperties) {
      if (theProperties != null) {
         log.debug("Git properties: {}", ToStringBuilder.reflectionToString(theProperties));
      }
   }

   /**
    * Main method.
    * @param theArgs command line arguments
    */
   @SuppressWarnings("VariableArgumentMethod")
   public static void main(String... theArgs) {
      SpringApplication.run(ConfigServer.class, theArgs);
   }
}