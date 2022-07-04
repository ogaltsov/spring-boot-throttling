FROM openjdk:17
ADD /build/libs/amzscout-test-task.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]