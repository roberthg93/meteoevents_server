# Utilitzar una imatge base Java, ha d'estar amb la versió 21
FROM eclipse-temurin:21-jdk-alpine
LABEL authors="rhospital"
WORKDIR /app

# Definim el fitxer JAR generat per Spring Boot a copiar en el contenidor
COPY target/*.jar app.jar

# Exposar el port en el que l'aplicació estarà disponible
EXPOSE 8080

# Comanda per executar l'aplicació
ENTRYPOINT ["java", "-jar", "app.jar"]
