FROM openjdk:11-jdk-slim
COPY build/libs/learn-kafka.jar learn-kafka.jar
ENTRYPOINT ["java","-jar","/learn-kafka.jar"]