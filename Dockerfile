FROM openjdk:22-jdk
ADD target/practice-application.jar practice-application.jar
ENTRYPOINT ["java", "-jar", "/practice-application.jar"]