# API de E-commerce con Microservicios (Spring Framework)
[![Docker Pulls](https://img.shields.io/docker/pulls/sophia1981/ecommerceapi)](https://hub.docker.com/repository/docker/sophia1981/ecommerceapi/general)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Este proyecto consiste en una API de microservicios desarrollada con Spring Framework para la gestión de un e-commerce, abarcando productos, inventario (stock) y órdenes. Se ha puesto un énfasis especial en la escalabilidad, la implementación de pruebas y la integración con servicios de AWS.

## Tecnologías Utilizadas

* **Lenguaje:** Java
* **Framework:** Spring Framework (Spring Boot)
* **Persistencia:** Hibernate (JPA)
* **Base de Datos (AWS):** RDS (PostgreSQL)
* **Autenticación/Autorización:** JWT
* **Docker:** Para la containerización de la aplicación
* **AWS:** ECS para el despliegue, RDS para la base de datos, CodePipeline y CodeBuild para CI/CD, CloudWatch para monitoreo y registro.
* **Pruebas:** JUnit, Mockito, MockMvc
* **Documentación API:** Swagger/OpenAPI 3
* **IDE:** Visual Studio Code

## Requisitos Funcionales Implementados

* **Gestión de Productos:**
    * CRUD completo para productos.
    * Búsqueda de productos por nombre, categoría y rango de precios.
* **Gestión de Usuarios:**
    * Registro de usuarios.
    * Login de usuarios.
* **Gestión de Órdenes:**
    * Creación de órdenes (uno o varios productos).
    * Visualización del historial de órdenes de un usuario.
* **Autenticación y Autorización:** Implementado con JWT.

## Modelo de Datos (Entidades)

Se han definido las siguientes entidades utilizando Hibernate, implementando herencia uno-a-muchos y muchos-a-muchos:

* `Brand`
* `Category`
* `Product` (relaciones con Brand y Category)
* `User`
* `Role`
* `Stock` (relación con Product)
* `Order` (relación con User)
* `OrderDetails` (relación con Product)

*(Nota: Los detalles específicos de los atributos de cada entidad se encuentran en el código fuente o se puede consultar con swagger al compilar la API).*

## Cómo Ejecutar la Aplicación Localmente

Sigue estos pasos para ejecutar la API localmente:

1.  **Requisitos Previos:**
    * Java Development Kit (JDK) instalado.
    * Maven instalado.
    * Docker Desktop instalado (si deseas probar la imagen Docker localmente).
    * Una instancia local de una base de datos (tiene configurado bases de datos H2).

2.  **Clonar el Repositorio:**
    ```bash
    git clone https://github.com/fernanvergara/ecommerce-api
    cd ecommerce-api
    ```

3.  **Construir la Aplicación:**
    ```bash
    mvn clean install
    ```

4.  **Ejecutar la Aplicación:**
    ```bash
    mvn spring-boot:run
    ```

    O, si prefieres ejecutarla con Docker:

    ```bash
    docker build -t ecommerce-api .
    docker run -p 8080:8080 ecommerce-api
    ```

    La API estará disponible en `http://localhost:8080`.

## Documentación de la API

La documentación de la API se genera utilizando **Swagger** (o **OpenAPI 3**). Una vez que la aplicación esté en ejecución (localmente o en AWS), puedes acceder a la documentación en la siguiente URL:

```bash
http://localhost:8080/swagger-ui.html
```


*(Nota: La URL puede variar ligeramente dependiendo de la configuración de Swagger/OpenAPI).*

## Pruebas

El proyecto incluye un conjunto de pruebas unitarias y de integración para demostrar la funcionalidad y robustez de la aplicación.

* **Pruebas Unitarias:** Se encuentran en el directorio `src/test/java`. Alcanza un 83% aprox. de cobertura del código.
* **Pruebas de Integración:** Utilizan `MockMvc` para probar los endpoints de la API sin necesidad de un servidor en ejecución.

Para ejecutar las pruebas:

```bash
mvn test
```

## Despliegue en AWS

El despliegue de esta aplicación en AWS se realiza utilizando los siguientes servicios:

1.  **Amazon ECS (Elastic Container Service):** Para orquestar los contenedores Docker de la aplicación.
2.  **Amazon RDS (Relational Database Service):** Para la base de datos.

### Configuración de AWS

Antes de desplegar, asegúrate de tener los siguientes recursos configurados en AWS:

* Una instancia de RDS con el motor de base de datos de tu elección.
* Un registro de contenedor de ECS (ECR) para almacenar la imagen Docker de la aplicación.
* Un clúster de ECS.
* Configuración de seguridad (Security Groups) para permitir la comunicación entre ECS y RDS.
* Roles de IAM con los permisos necesarios para ECS y la aplicación.

### Proceso de Despliegue en AWS

1.  **Construir y Subir la Imagen Docker:**
    ```bash
    docker build -t ecommerce-api .
    aws ecr get-login-password --region <REGION_AWS> | docker login --username AWS --password-stdin <ID_CUENTA_AWS>.dkr.ecr.<REGION_AWS>.amazonaws.com
    docker tag ecommerce-api:latest <ID_CUENTA_AWS>.dkr.ecr.<REGION_AWS>[.amazonaws.com/ecommerce-api:latest](https://.amazonaws.com/ecommerce-api:latest)
    docker push <ID_CUENTA_AWS>.dkr.ecr.<REGION_AWS>[.amazonaws.com/ecommerce-api:latest](https://.amazonaws.com/ecommerce-api:latest)
    ```

2.  **Configurar la Definición de Tarea de ECS:**
    Crea una definición de tarea en ECS que utilice la imagen Docker subida a ECR y configure las variables de entorno necesarias (por ejemplo, la URL de la base de datos RDS, credenciales).

3.  **Configurar el Servicio de ECS:**
    Crea o actualiza un servicio de ECS para ejecutar la definición de tarea. Asegúrate de que el servicio esté configurado para utilizar el clúster y la subredes correctas, y que esté asociado a un Load Balancer si es necesario.

## CI/CD (Integración y Despliegue Continuos)

Se ha configurado una canalización de CI/CD utilizando **AWS CodePipeline** y **AWS CodeBuild**.

1.  **Source:** La canalización se activa con cambios en el repositorio Git (GitHub).
2.  **Build:** AWS CodeBuild toma el código fuente, ejecuta las pruebas unitarias. Si las pruebas fallan, la canalización se detiene. Si las pruebas son exitosas, CodeBuild construye la imagen Docker y la sube a Amazon ECR.
3.  **Deploy:** AWS CodePipeline actualiza el servicio de ECS con la nueva imagen de Docker desde ECR.

## Monitoreo y Registro

Para el monitoreo y registro de la aplicación en AWS, se ha implementado **Amazon CloudWatch**. Esto permite la visualización de métricas, logs y el rastreo de solicitudes para identificar y solucionar problemas.

