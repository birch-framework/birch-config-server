#####################################################################
# Copyright (c) 2021 Birch Framework
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# any later version.
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#####################################################################
ARG JARFILE
FROM openjdk:11.0.11-jdk-slim
WORKDIR /jars
COPY /${JARFILE} /jars
EXPOSE 8888
ENTRYPOINT ["java", "-Xmx64m", "-jar ${JARFILE}"]