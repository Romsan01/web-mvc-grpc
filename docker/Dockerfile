FROM  maven:3.8.3-openjdk-17
COPY target/web-0.0.1-SNAPSHOT.jar /usr/app/web.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/web.jar"]