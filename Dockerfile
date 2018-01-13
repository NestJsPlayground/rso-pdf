FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./api/target/web-1.0.0.jar /app

EXPOSE 8081

CMD ["java", "-jar", "web-1.0.0.jar"]