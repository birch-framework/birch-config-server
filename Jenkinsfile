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
            version = projectVersion
            if (theBranchName == 'master') {
               branchType = BranchType.MASTER
            }
         }
      }
   }
}

node('ubuntu-node') {
   // Configure JDK 11
   env.JAVA_HOME = tool name: 'GraalVM-JDK11' // Tool name from Jenkins configuration

   stage ('Clone') {
      checkout scm
   }

   stage ('Configuration') {
      def projectVersion = sh(returnStdout: true, script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout').trim()
      Globals.setVersionInfo(env.BRANCH_NAME as String, projectVersion as String)
   }

   stage ('Test and Install') {
      withMaven(mavenSettingsConfig: 'Birch-Maven-Settings') {
         sh "mvn clean install"
      }
   }

   stage ('Quality Analysis') {
      withSonarQubeEnv(installationName: 'SonarCloud (Birch Framework)', envOnly: true) {
         withMaven (mavenSettingsConfig: 'Birch-Maven-Settings') {
            sh "mvn sonar:sonar -Dsonar.branch.name=${env.BRANCH_NAME}"
         }
      }
   }

   stage ('Docker Image') {
      if (Globals.branchType == BranchType.MASTER || Globals.branchType == BranchType.RELEASE) {
         def tag = Globals.branchType == BranchType.MASTER ? "latest" : Globals.version
         docker.withRegistry("https://${Globals.DOCKER_REG}", Globals.DOCKER_REG_CREDENTIALS) {
            def image = docker.build("${Globals.DOCKER_REG}/${Globals.DOCKER_IMAGE_NAME}:${tag}", "--build-arg VERSION=${Globals.version} -f ${env.WORKSPACE}/${Globals.DOCKERFILE_BASE_PATH}/Dockerfile .")
            image.push(tag)
         }
      }
      else {
         echo "${env.BRANCH_NAME} branch does not publish/release Docker container"
      }
   }
}