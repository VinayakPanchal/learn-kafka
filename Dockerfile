FROM openjdk:8-jdk-alpine
COPY build/libs/learn-kafka.jar learn-kafka.jar
ENTRYPOINT ["java","-jar","/learn-kafka.jar"]