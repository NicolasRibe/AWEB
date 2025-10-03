# Etapa 1: Build da aplicação com Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia pom.xml e baixa dependências (melhor cache)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o restante do código
COPY . .
RUN mvn -B clean package -DskipTests

# Etapa 2: Imagem final para rodar o Spring Boot
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o JAR gerado no build
COPY --from=build /app/target/*.jar app.jar

# Expõe porta (Render substitui pela variável PORT)
EXPOSE 8080

# Usa a variável PORT do Render (ou 8080 localmente)
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]