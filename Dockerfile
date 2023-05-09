# Загрузка образа для сборки приложения
FROM maven:3.8.3 AS build

# Копирование всех файлов из текущего каталога на хост-системе в Docker-образ
COPY . /home/app

# Установка рабочей директории
WORKDIR /home/app

# Запуск сборки приложения
RUN mvn clean package -DskipTests

# Загрузка образа для запуска приложения
FROM openjdk:17-slim AS run

# Копирование собранного jar-файла из предыдущего образа в текущий
ARG JAR_FILE=/home/app/core/target/*.jar
COPY --from=build ${JAR_FILE} /home/app/app.jar

# Установка рабочей директории
WORKDIR /home/app

ENV DB_URL=jdbc:postgresql://mypostgres:5432/ncti
ENV DB_USER=postgres
ENV DB_PASSWORD=root
ENV EMAIL_USER=mail
ENV EMAIL_PASSWORD=mail
ENV SECRET=dSgVkYp3s6v9y$B&
ENV RABBITMQ_HOST=172.18.0.2
ENV RABBITMQ_USER=aa
ENV RABBITMQ_PASSWORD=aa

EXPOSE 8080

# Запуск приложения
CMD ["java", "-jar", "app.jar"]



