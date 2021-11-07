/****************************************************************
 * Copyright (c) 2021 Birch Framework
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ***************************************************************/

enum BranchType {
   RELEASE,
   MASTER,
   OTHER;
}

class Globals {

   // Docker parameters
   static final String DOCKER_REG = 'ghcr.io'
   static final String DOCKER_REG_CREDENTIALS = 'GitHub-CICD-Automation'
   static final String DOCKER_IMAGE_NAME = 'birch-framework/birch-config-server'
   static final String DOCKERFILE_BASE_PATH = 'src/main/docker'

   static String version
   static BranchType branchType = BranchType.OTHER

   static setVersionInfo (String theBranchName, String projectVersion) {
      if (theBranchName != null && !theBranchName.isEmpty ()) {
         if (theBranchName =~ 'release/*') {
            if (projectVersion == null || projectVersion.isEmpty ()) {
               def splitVersion = theBranchName.split ("/")
               if (splitVersion != null && splitVersion.size () > 0) {
                  version = splitVersion [1]
               }
               else {
                  throw new RuntimeException ("Could not infer version number from branch name")
               }
            }
            else {
               version = projectVersion
            }
            branchType = BranchType.RELEASE
         }
         else {
            version = (projectVersion == null || projectVersion.isEmpty ()) ? "latest" : projectVersion
            if (theBranchName == 'master') {
               branchType = BranchType.MASTER
            }
         }
      }
   }
}

node('ubuntu-node') {
   // Configure JDK 11
   jdk = tool name: 'GraalVM-JDK11' // Tool name from Jenkins configuration
   env.JAVA_HOME = "${jdk}"

   stage ('Clone') {
      checkout scm
   }

   stage ('Configuration') {
      def projectVersion = sh(returnStdout: true, script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout').trim()
      Globals.setVersionInfo(env.BRANCH_NAME as String, projectVersion as String)
      withMaven(mavenSettingsConfig: 'Birch-Maven-Settings') {
         bat "mvn clean -version"
      }
   }

   stage ('Test and Install') {
      withMaven(mavenSettingsConfig: 'Birch-Maven-Settings') {
         bat "mvn clean install"
      }
   }

   stage ('Quality Analysis') {
      withCredentials([string(credentialsId: 'Sonar-Token', variable: 'TOKEN')]) {
         env.SONAR_TOKEN = "${TOKEN}"
         withMaven(mavenSettingsConfig: 'Birch-Maven-Settings') {
            bat "mvn sonar:sonar"
         }
      }
   }

   stage ('Docker Build') {
      if (Globals.branchType == BranchType.MASTER || Globals.branchType == BranchType.RELEASE) {
         withCredentials([usernamePassword (credentialsId: "${Globals.DOCKER_REG_CREDENTIALS}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            docker.withRegistry("https://${Globals.DOCKER_REG}", "${Globals.DOCKER_REG_CREDENTIALS}") {
               def image = docker.build("${Globals.DOCKER_REG}/${Globals.DOCKER_IMAGE_NAME}:${Globals.version}", "--build-arg VERSION=${Globals.version} -f ${env.WORKSPACE}/${Globals.DOCKERFILE_BASE_PATH}/Dockerfile .")
               image.push(Globals.version)
            }
         }
      }
      else {
         echo "${env.BRANCH_NAME} branch does not publish/release Docker container"
      }
   }
}