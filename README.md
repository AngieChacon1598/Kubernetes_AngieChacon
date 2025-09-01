# WebFlux AI Integration

Este proyecto es una aplicación Spring WebFlux que consume dos APIs de IA diferentes (Language Identify y JSearch) y almacena los resultados en una base de datos MongoDB.

## Características

- **Language Identify API**: Detecta el idioma de un texto dado.
- **JSearch API**: Busca ofertas de trabajo basadas en criterios específicos.
- **Almacenamiento en MongoDB**: Todos los resultados se guardan para su posterior consulta.
- **API Reactiva**: Desarrollada con Spring WebFlux para un manejo eficiente de solicitudes concurrentes.

## Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring WebFlux**
- **Project Reactor**
- **MongoDB Reactive**
- **Lombok**
- **Maven**

## Requisitos previos

- Java 17 o superior
- Maven 3.6.3 o superior
- MongoDB 4.4 o superior
- Cuentas en las APIs de Language Identify y JSearch (RapidAPI)

## Configuración

1. Clona el repositorio:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd webflux-ai
   ```

2. Configura las variables de entorno en `src/main/resources/application.yml` o como variables de entorno del sistema:
   ```yaml
   MONGO_URI: tu_cadena_de_conexion_mongodb
   LANGUAGE_IDENTIFY_API_KEY: tu_api_key_de_language_identify
   JSEARCH_API_KEY: tu_api_key_de_jsearch
   ```

3. Construye la aplicación:
   ```bash
   mvn clean install
   ```

4. Ejecuta la aplicación:
   ```bash
   mvn spring-boot:run
   ```

## Uso

### Language Identify API

**Detectar idioma de un texto:**
```http
POST /api/v1/language/detect
Content-Type: application/json

{
  "text": "Hola, ¿cómo estás?"
}
```

**Obtener detección por ID:**
```http
GET /api/v1/language/detections/{id}
```

### JSearch API

**Buscar trabajos:**
```http
GET /api/v1/jobs/search?query=java&location=remote&page=1&resultsPerPage=10
```

**Obtener búsqueda por ID:**
```http
GET /api/v1/jobs/{id}
```

## Estructura del proyecto

```
src/main/java/com/vallegrande/webfluxai/
├── config/           # Configuraciones de la aplicación
├── controller/       # Controladores REST
├── dto/             # Objetos de transferencia de datos
├── exception/       # Manejo de excepciones
├── model/           # Entidades del dominio
├── repository/      # Repositorios de MongoDB
└── service/         # Lógica de negocio
```

## Variables de entorno

| Variable | Descripción |
|----------|-------------|
| `MONGO_URI` | URI de conexión a MongoDB |
| `LANGUAGE_IDENTIFY_API_KEY` | API Key para Language Identify |
| `JSEARCH_API_KEY` | API Key para JSearch |
| `JSEARCH_API_HOST` | Host de la API de JSearch |

## Ejecución de pruebas

Para ejecutar las pruebas unitarias:

```bash
mvn test
```

## Despliegue

La aplicación está lista para desplegarse en cualquier plataforma que soporte aplicaciones Java con Spring Boot. Asegúrate de configurar las variables de entorno necesarias en el entorno de producción.


Compilar
1. mvn clean compile
2. mvn spring-boot:run