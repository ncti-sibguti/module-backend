FROM openjdk:17

ARG JAR_FILE=core/target/*.jar
COPY ${JAR_FILE} app.jar

ENV DB_URL=jdbc:postgresql://mypostgres:5432/ncti
ENV DB_USER=postgres
ENV DB_PASSWORD=root
ENV EMAIL_PASSWORD=qehvszevaoocrlma
ENV EMAIL_USER=iv21an45@gmail.com
ENV SECRET=dSgVkYp3s6v9y$B&
ENV RABBITMQ_HOST=172.18.0.3

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
