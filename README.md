[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/summary/new_code?id=org.birchframework.config%3Abirch-config-server)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=org.birchframework.config%3Abirch-config-server&metric=bugs)](https://sonarcloud.io/summary/new_code?id=org.birchframework.config%3Abirch-config-server)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=org.birchframework.config%3Abirch-config-server&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=org.birchframework.config%3Abirch-config-server)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.birchframework.config%3Abirch-config-server&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=org.birchframework.config%3Abirch-config-server)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.birchframework.config%3Abirch-config-server&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=org.birchframework.config%3Abirch-config-server)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.birchframework.config%3Abirch-config-server&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=org.birchframework.config%3Abirch-config-server)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=org.birchframework.config%3Abirch-config-server&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=org.birchframework.config%3Abirch-config-server)


[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
# Birch Config Server

An out-of-the-box Spring Cloud Config Server with simplified customizations.  This implementation is essentially
a plain Spring Config Server with some default configurations and simpler config parameters that
adhere to Spring Boot relaxed binding rules.

# Docker Image
The released docker images can be accessed via the following command:
```shell
docker pull ghcr.io/birch-framework/birch-config-server:latest
```

# Build
To build your own version of the Birch Config Server, run:
```shell
mvn clean install
```

# Running
This config server can be run via Docker or building the JAR and running directly via Java.

## Docker
```shell
docker run --restart unless-stopped -d --name birch-config-server --pull always \
         -e GIT_URL=https://some-server.somegit.com/my-config.git \
         -e GIT_USERNAME=mygitid \
         -e GIT_PASSWORD=my_git_personal_access_token \
         ghcr.io/birch-framework/birch-config-server:<version>
```
Replace `<version>` with the desired image version.

## Java
Once the config server is built, run:
```shell
java -Dgit.url=https://some-server.somegit.com/my-config.git -Dgit.username=mygitid -Dgit.password=my_git_personal_access_token -jar target/birch-config-server-<version>.jar
```
Replace `<version>` with the version that is built locally by Maven.

# Configuration
For any of the following configurations, Spring Boot relaxed binding is in play.  Therefore any environment vairable,
for example, the `GIT_URL` environment variable can alternatively be specified as the Java system property `-Dgit.url`
when running the config server using Java instead of Docker.

## Filesystem Backend
This mode uses the local filesystem as the config source.  To enable this mode, use the following environment variables:
```shell
SPRING_PROFILES_ACTIVE=native                             # required
CONFIG_REPO=C:/path/to/config-dir                         # required
```

## Git Backend
This mode uses a Git repository as the config source.  To enable this mode, use the following environment variables:
```shell
GIT_URL=https://some-server.somegit.com/my-config.git     # required
GIT_USERNAME=mygitid                                      # required
GIT_PASSWORD=my_git_personal_access_token                 # required
GIT_CLONE_ON_START=true                                   # optional
GIT_DELETE_UNTRACKED_CHANGES=true                         # optional
GIT_FORCE_PULL=true                                       # optional
GIT_SKIP_SSL_VALIDATION=false                             # optional
GIT_TIMEOUT=5                                             # optional; the unit is seconds
GIT_REFRESH_RATE=0                                        # optional
GIT_BASEDIR=/tmp                                          # optional; defaults to java.io.tmpdir Java system property
GIT_DEFAULT_BRANCH=master                                 # optional
```
For the above variables that are `optional`, the sample value specified is the default unless specified.

## Kubernetes Use Case
The `GIT_BASEDIR` configuration is a useful mechanism to bind the container to a Kubernetes Persistent Volume 
when configuring a Stateful Set for the application.  This way, multiple replica PODs can utilize the same 
Persistent Volume, thus ensuring high availability of the config server, even when the Git repository 
backend is unavailable at config server boot time.
