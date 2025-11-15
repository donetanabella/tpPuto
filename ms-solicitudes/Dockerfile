# Etapa de build
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests clean package

# Etapa de runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
