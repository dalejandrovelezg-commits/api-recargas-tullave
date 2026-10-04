# API de Recargas Digitales Tullave

API REST en **Spring Boot 3** y **Java 21** para el registro, consulta paginada y eliminación de recargas digitales de la tarjeta Tullave, integrada con **PostgreSQL**.

---

## Instrucciones para ejecutar el proyecto

### Requisitos Previos

- **Docker** y **Docker Desktop** instalados y activos.
- **Git** instalado.

### 1. Clonar el proyecto a local

Clonar el proyecto a una carpeta local. Para ello se puede usar en una terminal el comando:

```bash
git clone https://github.com/dalejandrovelezg-commits/api-recargas-tullave.git
```

### 2. Despliegue con Docker Compose

Desde la carpeta raíz del proyecto ejecutar docker compose. Para ello se puede usar en una terminal el comando:

```bash
docker compose up --build -d
```

### 3. Comprobar la API (Opcional)

Una vez se complete el despliegue con docker, se puede comprobar que el servicio se ha levantado correctamente accediendo a la documentación de la API con Swagger en:

<http://localhost:8080/swagger-ui/index.html>

## Instrucciones para ejecutar pruebas unitarias

### Requisitos Previos
- **Java 21** instalado y configurado en el PATH del sistema.
- **Maven** instalado y configurado en el PATH del sistema.

### 1. Ejecutar pruebas unitarias con Maven

Una vez clonado el proyecto se pueden correr las pruebas unitarias. Para ello, se puede ejecutar desde la carpeta raíz del proyecto el comando:

```bash
./mvnw clean test
```

### 2. Comprobar resultados de las pruebas unitarias
Una vez finalizada la ejecución de las pruebas unitarias, se puede comprobar el resultado de las mismas en la terminal. En caso de que todas las pruebas unitarias hayan pasado correctamente, se mostrará un mensaje indicando que todas las pruebas han sido exitosas.

## Arquitectura y Decisiones Técnicas
- El proyecto sigue una arquitectura en capas limpia (Controller - Service - Repository - DTO - Model) priorizando el bajo acoplamiento y la mantenibilidad

- Ninguna entidad JPA es expuesta directamente. Las solicitudes se procesan mediante `RechargeRequestDto` y se responden con `RechargeResponseDto`.

- Uso de jakarta.validation para asegurar la validéz de la entrada (Numero de tarjeta de 16 dígitos exactos, montos entre $2,000 y $200,000, métodos de pago válidos).

- Uso de `@RestControllerAdvice` para unificar las respuestas de error en un formato JSON consistente definido en el DTO `ErrorResponseDto`

- Spring Data JPA con dialecto PostgreSQLDialect configurado para autogenerar tablas en el primer inicio (`ddl-auto = update`).

- Inyección de logs con `SLF4J` en cada flujo de negocio y en el manejo de excepciones.

- Uso de `@SpringBootTest` y `@WebMvcTest` para pruebas unitarias de servicios y controladores respectivamente, con `MockMvc` para simular peticiones HTTP.

## Explicación de Dockerfile y Docker Compose
### Dockerfile

- **Etapa 1 (Build):** Se utiliza la imagen `maven:3.9.6-eclipse-temurin-21-alpine` para descargar dependencias y compilar la aplicación generando el archivo `.jar`

- **Etapa 2 (Runtime):** Se copia el `.jar` en una imagen ligera del JRE `eclipse-temurin:21-jre-alpine` para el despliegue de la aplicación (`app.jar`).

- **Puerto y Despliegue**: Se define el puerto 8080 y se define el comando de ejecución para desplegar la aplicación (`java -jar app.jar`)

### docker-compose.yml

- **Contenedor BD (postgres-db)** 
    1. Define el contenedor `tullave-postgres` para la base de datos de PostgreSQL utilizando la imagen `postgres:16-alpine`.
    2. Define las variables de entorno para las credenciales de acceso a la base de datos
    3. Expone la base de datos por el puerto `5432`
    4. Utiliza un volumen para persistir los datos creados

- **Contenedor aplicación (recargas-api)**
    1. Define el contenedor `api-recargas-tullave` para la aplicación utilizando el Dockerfile en la misma capreta raíz del proyecto
    2. Define las variables de entorno que utilizará la aplicación (en este caso las credenciales de acceso y conexión a base de datos)
    3. Expone la aplicación por el puerto `8080`
    4. Condiciona a que la base de datos está lista antes de iniciar la aplicación

## Flujo de Git Utilizado
- `main`: Rama de producción.

- `feature/model-dto`: Implementación del modelo de datos, repositorio y DTOs con validaciones.

- `feature/service-controller`: Implementación del servicio, controlador REST y el manejo de excepciones.

- `feature/test-doc`: Implementación de pruebas unitarias y documentación de la API con Swagger.

*Se realiza un pull request a la rama main por cada feature completado*