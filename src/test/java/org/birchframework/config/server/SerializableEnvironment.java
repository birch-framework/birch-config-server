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

import java.io.Serializable;
import org.springframework.cloud.config.environment.Environment;

/**
 * A {@link Serializable} version of the {@link Environment} class, used only for testing purposes.
 * @author Keivan Khalichi
 */
public class SerializableEnvironment extends Environment implements Serializable {

   public SerializableEnvironment() {
      this("", "");
   }

   @SuppressWarnings("VariableArgumentMethod")
   public SerializableEnvironment(final String name, final String... profiles) {
      super(name, profiles);
   }

   public SerializableEnvironment(final Environment env) {
      super(env);
   }

   public SerializableEnvironment(final String name, final String[] profiles, final String label, final String version, final String state) {
      super(name, profiles, label, version, state);
   }
}
