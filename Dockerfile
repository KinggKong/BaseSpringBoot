FROM openjdk:17
WORKDIR Project
COPY target/Project-0.0.1-SNAPSHOT.jar /Project/Project-0.0.1-SNAPSHOT.jar
#COPY /keystore.p12 /education_cy/uploads/keystore.p12
COPY /src/main/resources/application-prd.yml /Project/application.yml
WORKDIR uploads

EXPOSE 8085
CMD ["java", "-jar", "/Project/Project-0.0.1-SNAPSHOT.jar", "--spring.config.location=/Project/application.yml"]