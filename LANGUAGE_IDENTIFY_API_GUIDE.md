# Language Identify API Guide

## Configuración

La API de Language Identify está configurada para usar RapidAPI con las siguientes configuraciones:

- **Base URL**: `https://language-identify-detector.p.rapidapi.com`
- **API Key**: `7eed17039dmsh63431ac43f3bab8p1c600cjsn1eee5acffea8`
- **API Host**: `language-identify-detector.p.rapidapi.com`

## Endpoints Disponibles

### 1. Detectar Idioma

**Endpoint**: `POST /api/v1/language/detect`

**Body** (JSON):
```json
{
  "text": "Hello, how are you today?"
}
```

**Ejemplo de uso**:
```bash
curl -X POST "http://localhost:8088/api/v1/language/detect" \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello, how are you today?"}'
```

**Respuesta**:
```json
{
  "code": "en",
  "confidence": 0.95,
  "name": "English"
}
```

### 2. Obtener Detección Específica

**Endpoint**: `GET /api/v1/language/detections/{id}`

**Parámetros**:
- `id` (requerido): ID de la detección guardada en MongoDB

**Ejemplo de uso**:
```bash
curl -X GET "http://localhost:8088/api/v1/language/detections/64f8a1b2c3d4e5f6a7b8c9d0"
```

### 3. Obtener Todas las Detecciones

**Endpoint**: `GET /api/v1/language/detections`

**Descripción**: Retorna todas las detecciones de idioma guardadas en MongoDB

**Ejemplo de uso**:
```bash
curl -X GET "http://localhost:8088/api/v1/language/detections"
```

## Configuración Actual

La aplicación está configurada con:
- Timeout: 30 segundos
- Buffer máximo: 16 MB (para manejar respuestas grandes)
- Headers automáticos para RapidAPI
- Almacenamiento automático en MongoDB

## Manejo de Errores

La API maneja los siguientes errores:
- `400 Bad Request`: Texto vacío o inválido
- `404 Not Found`: Detección no encontrada
- `500 Internal Server Error`: Error del servidor
- `503 Service Unavailable`: API externa no disponible

## Logs

Los logs incluyen:
- Información de detección
- Errores de la API externa
- Tiempo de respuesta
- Detalles del idioma detectado

## Pruebas en Postman

Para probar en Postman:

### Endpoints de Prueba:

1. **Verificar configuración**:
   ```
   GET http://localhost:8088/api/v1/test/jsearch-config
   ```

2. **Ver información de MongoDB**:
   ```
   GET http://localhost:8088/api/v1/test/mongodb-info
   ```

### Endpoints Principales:

3. **Detectar idioma**:
   ```
   POST http://localhost:8088/api/v1/language/detect
   Content-Type: application/json
   
   {
     "text": "Hola, ¿cómo estás?"
   }
   ```

4. **Ver todas las detecciones**:
   ```
   GET http://localhost:8088/api/v1/language/detections
   ```

5. **Ver detección específica**:
   ```
   GET http://localhost:8088/api/v1/language/detections/{id}
   ```

## Idiomas Soportados

La API puede detectar múltiples idiomas incluyendo:
- English (en)
- Spanish (es)
- French (fr)
- German (de)
- Italian (it)
- Portuguese (pt)
- Russian (ru)
- Chinese (zh)
- Japanese (ja)
- Korean (ko)
- Y muchos más...

## Notas Importantes

- Los resultados se guardan automáticamente en MongoDB
- La API usa WebClient reactivo para mejor rendimiento
- Los timeouts están configurados para APIs externas
- Los headers de RapidAPI se configuran automáticamente
- Se guarda el texto original, idioma detectado y nivel de confianza
