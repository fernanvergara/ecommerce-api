# Usa una imagen base de Java (por ejemplo, una imagen de Eclipse Temurin con JDK 17)
FROM eclipse-temurin:17-jdk-focal
# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app
# Copia el archivo JAR de Spring Boot al contenedor
COPY target/ecommerceapi-0.0.1-SNAPSHOT.jar app.jar
# Expon el puerto en el que la aplicación escucha (por defecto, 8080)
EXPOSE 8080
# Define el comando para ejecutar la aplicación cuando el contenedor se inicie
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]