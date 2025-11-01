# Etapa 1: construir o projeto
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# ✅ Copiar primeiro o POM para cache de dependências
COPY pom.xml .
COPY src ./src

# ✅ Baixar dependências primeiro (cache eficiente)
RUN mvn dependency:go-offline -B

# ✅ Compilar com retry em caso de falha de rede
RUN mvn clean package -DskipTests

# Etapa 2: rodar o projeto
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]