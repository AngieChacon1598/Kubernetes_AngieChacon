🌐 *WEBFLUX AI INTEGRATION*

Este proyecto es una aplicación Spring WebFlux que consume dos APIs de IA diferentes (Language Identify y JSearch) y almacena los resultados en una base de datos MongoDB.

**1ERA API: LANGUAGE IDENTIFY**

🔎 **¿Qué es Language Identify?**

La Language Identify API permite detectar automáticamente el idioma de un texto dado, devolviendo un listado de posibles lenguajes junto con un puntaje de confianza para cada uno. Es útil para aplicaciones multilingües, análisis de contenido y clasificación de textos en distintos idiomas.

📌 **Endpoints principales**

La API ofrece endpoints simples (con autenticación vía RapidAPI), siendo el más usado:

Detección de idioma → `POST /identify`

✅ **Casos de uso prácticos**

- Aplicaciones de chat o soporte → detectar el idioma del usuario automáticamente.
- Sistemas de recomendación → redirigir contenido en el idioma correcto.
- Análisis de textos → clasificación multilingüe en minería de datos o BI.

⚡**Uso**

*Detectar idioma de un texto:*
`POST /api/v1/language/detect`
Content-Type: application/json

*Obtener detección por ID:*
`GET /api/v1/language/detections/{id}`

*Obtener todas las detecciones:*
`GET /api/v1/language/detections`


**2DA API: JSEARCH** 

🔎 **¿Qué es JSearch?**

La API JSearch de OpenWeb Ninja permite buscar empleos en tiempo real desde Google for Jobs y portales como LinkedIn, Indeed o Glassdoor. Además de acceder a ofertas actualizadas, ofrece detalles de cada puesto e información salarial, siendo una solución completa para la exploración y análisis del mercado laboral.

📌 **Endpoints principales**

La API ofrece varios endpoints (todos con autenticación vía RapidAPI). Los más usados son:

1. Búsqueda de empleo → `GET /search`
2. Detalles del trabajo → `GET /job-details`
3. Salario del trabajo (estimado) → `GET /job-salary`
4. Salario por puesto en una empresa → `GET /company-job-salary`
   

✅ **Casos de uso prácticos**

- Aplicaciones de reclutamiento → mostrar empleos en tiempo real.
- Estudios de mercado laboral → analizar salarios por región/empresa.
- SEO de empleos → listar ofertas con filtros avanzados.

📌**Resumen**

- *Language Identify API*: Detecta el idioma de un texto dado.
- *JSearch API*: Busca ofertas de trabajo basadas en criterios específicos.
- *Almacenamiento en MongoDB*: Todos los resultados se guardan para su posterior consulta.
- *API Reactiva*: Desarrollada con Spring WebFlux para un manejo eficiente de solicitudes concurrentes.

**Buscar trabajos:**
`GET /api/v1/jobs/search?query=java&location=remote&page=1&resultsPerPage=10`

**Obtener detalles de un trabajo específico:**
`GET /api/v1/jobs/details/{jobId}`

**Obtener búsqueda por ID:**
`GET /api/v1/jobs/{id}`


🛠️**Tecnologías utilizadas**

- ☕Java 17
- 🌱Spring Boot 3.2.0
- 🔄Spring WebFlux
- ⚡Project Reactor
- 🍃MongoDB Reactive
- 📝Lombok
- 📦Maven

📋**Requisitos previos**

- Java 17 o superior
- Maven 3.6.3 o superior
- MongoDB 4.4 o superior
- Cuentas en las APIs de Language Identify y JSearch (RapidAPI)


🚀Ejecución

### Desarrollo Local
```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

### Docker

#### Con GitHub Actions (Recomendado)
El proyecto tiene configurado un pipeline de CI/CD que automáticamente:
- Construye la imagen Docker en cada commit
- Sube la imagen a Docker Hub

**Configuración:**
1. Agrega los secrets en GitHub:
   - `DOCKERHUB_USERNAME`: Tu usuario de Docker Hub
   - `DOCKERHUB_TOKEN`: Tu access token de Docker Hub
   
2. Ver la documentación completa: [CICD_SETUP.md](CICD_SETUP.md)

#### Ejecutar Imagen Docker Manualmente
```bash
# Construir la imagen
docker build -t webflux-ai-backend .

# Ejecutar el contenedor
docker run -d \
  -p 8088:8088 \
  --name webflux-ai \
  webflux-ai-backend:latest

# Ver logs
docker logs -f webflux-ai
```

#### Pull desde Docker Hub
```bash
# Descargar la imagen (reemplaza 'tu-usuario' con tu usuario de Docker Hub)
docker pull tu-usuario/webflux-ai-backend:latest

# Ejecutar
docker run -d \
  -p 8088:8088 \
  --name webflux-ai \
  tu-usuario/webflux-ai-backend:latest
```

🎯 CI/CD Pipeline
Este proyecto incluye un pipeline automatizado con GitHub Actions que:
- ✅ Construye automáticamente la imagen Docker
- ✅ Sube la imagen a Docker Hub en cada commit
- ✅ Genera tags automáticos por rama y versión
- ✅ Usa cache de Maven para builds más rápidas
- ✅ Valida el código antes de construir

Ver más detalles en [CICD_SETUP.md](CICD_SETUP.md)
Esto es una prueba

