FROM maven:3.6.3-jdk-11-openj9

WORKDIR /project
COPY simple-consumer/pom.xml /project/pom.xml
COPY simple-consumer/src /project/src

RUN mvn -f pom.xml compile assembly:single

WORKDIR /project

EXPOSE 8090
