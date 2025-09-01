# JSearch API Guide

## Configuración

La API de JSearch está configurada para usar RapidAPI con las siguientes configuraciones:

- **Base URL**: `https://jsearch.p.rapidapi.com`
- **API Key**: `7eed17039dmsh63431ac43f3bab8p1c600cjsn1eee5acffea8`
- **API Host**: `jsearch.p.rapidapi.com`

## Endpoints Disponibles

### 1. Búsqueda de Trabajos

**Endpoint**: `GET /api/v1/jobs/search`

**Parámetros**:
- `query` (requerido): Término de búsqueda (ej: "developer jobs in chicago")
- `location` (opcional): Ubicación específica
- `page` (opcional, default: 1): Número de página
- `resultsPerPage` (opcional, default: 10, máximo: 5): Resultados por página (limitado para optimizar rendimiento)

**Ejemplo de uso**:
```bash
curl -X GET "http://localhost:8088/api/v1/jobs/search?query=developer%20jobs%20in%20chicago&page=1&resultsPerPage=10"
```

**Respuesta**:
```json
{
  "id": "...",
  "query": "developer jobs in chicago",
  "location": null,
  "page": 1,
  "resultsPerPage": 10,
  "searchedAt": "2024-01-15T10:30:00",
  "jobs": [
    {
      "jobId": "...",
      "title": "Software Developer",
      "companyName": "Tech Company",
      "location": "Chicago, IL",
      "jobType": "Full-time",
      "description": "Job description...",
      "applyLink": "https://...",
      "postedAt": "2024-01-15T09:00:00",
      "requiredSkills": ["Java", "Spring", "React"]
    }
  ],
  "metadata": {
    "totalResults": 150,
    "currentPage": 1,
    "resultsPerPage": 10
  }
}
```

### 2. Detalles de un Trabajo Específico

**Endpoint**: `GET /api/v1/jobs/details/{jobId}`

**Parámetros**:
- `jobId` (requerido): ID del trabajo específico

**Ejemplo de uso**:
```bash
curl -X GET "http://localhost:8088/api/v1/jobs/details/n20AgUu1KG0BGjzoAAAAAA=="
```

### 3. Obtener Resultado de Búsqueda Guardado

**Endpoint**: `GET /api/v1/jobs/{id}`

**Parámetros**:
- `id` (requerido): ID del resultado de búsqueda guardado en la base de datos

## Parámetros de la API JSearch

Según la documentación de JSearch, los parámetros soportados incluyen:

- `query`: Término de búsqueda
- `page`: Número de página (default: 1)
- `num_pages`: Número de páginas a obtener (default: 1)
- `country`: Código de país (default: "us")
- `date_posted`: Filtro de fecha (default: "all")
- `job_id`: ID específico del trabajo (para detalles)
- `language`: Código de idioma (opcional)
- `fields`: Campos específicos a incluir (opcional)

## Configuración Actual

La aplicación está configurada con:
- País por defecto: `us`
- Fecha de publicación: `all` (todas las fechas)
- Timeout: 30 segundos
- Buffer máximo: 16 MB (para manejar respuestas grandes)
- Límite de páginas: máximo 5 páginas por búsqueda (para optimizar rendimiento)
- Headers automáticos para RapidAPI

## Manejo de Errores

La API maneja los siguientes errores:
- `400 Bad Request`: Parámetros inválidos
- `404 Not Found`: No se encontraron trabajos
- `500 Internal Server Error`: Error del servidor
- `503 Service Unavailable`: API externa no disponible

## Logs

Los logs incluyen:
- Información de búsqueda
- Errores de la API externa
- Tiempo de respuesta
- Detalles de los trabajos encontrados

## Pruebas en Postman

Para probar en Postman:

### Endpoints de Prueba (para debugging):

1. **Verificar configuración**:
   ```
   GET http://localhost:8088/api/v1/test/jsearch-config
   ```

2. **Probar conexión con API externa**:
   ```
   GET http://localhost:8088/api/v1/test/jsearch-connection
   ```

### Endpoints Principales:

3. **Búsqueda básica**:
   ```
   GET http://localhost:8088/api/v1/jobs/search?query=developer%20jobs%20in%20chicago
   ```

4. **Búsqueda con parámetros**:
   ```
   GET http://localhost:8088/api/v1/jobs/search?query=java%20developer&page=1&resultsPerPage=5
   ```

5. **Detalles de trabajo**:
   ```
   GET http://localhost:8088/api/v1/jobs/details/{jobId}
   ```

## Notas Importantes

- Los resultados se guardan automáticamente en MongoDB
- La API usa WebClient reactivo para mejor rendimiento
- Los timeouts están configurados para APIs externas
- Los headers de RapidAPI se configuran automáticamente
