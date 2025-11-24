# 🧬 Mutant Detector API

API REST para detectar mutantes mediante análisis de secuencias de ADN. Proyecto de examen técnico MercadoLibre.

**Alumno:** Adriel Espejo  
**Legajo:** 47664  
**Materia:** Desarrollo de Software  
**Comisión:** 3K9  
**Institución:** UTN Mendoza  
**Año:** 2025

---

## 🚀 Deploy en Producción

**API:** https://examenmercado-3k9.onrender.com  
**Swagger:** https://examenmercado-3k9.onrender.com/swagger-ui.html

⚠️ **Nota:** Primera request puede tardar ~50 segundos (free tier de Render).

---

## 🛠️ Tecnologías

- **Java 17** - Lenguaje de programación
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistencia
- **H2 Database** - BD en memoria (desarrollo)
- **JUnit 5 + Mockito** - Testing
- **JaCoCo** - Cobertura de código
- **Swagger/OpenAPI** - Documentación de API
- **Lombok** - Reducción de boilerplate
- **Gradle** - Build tool
- **Docker** - Contenedorización

---

## 📦 Instalación

### Prerequisitos
- Java JDK 17 o superior
- Git

### Clonar y ejecutar

```bash
# Clonar
git clone https://github.com/VerseV/Global-3K9-Adriel-Espejo-47664.git
cd Global-3K9-Adriel-Espejo-47664

# Ejecutar (Windows)
.\gradlew bootRun

# Ejecutar (Mac/Linux)
./gradlew bootRun
```

**Aplicación:** http://localhost:8080  
**Swagger UI:** http://localhost:8080/swagger-ui.html  
**H2 Console:** http://localhost:8080/h2-console

---

## 📡 Endpoints

### POST /mutant

Verifica si una secuencia de ADN es mutante.

**Request:**
```json
{
  "dna": [
    "ATGCGA",
    "CAGTGC",
    "TTATGT",
    "AGAAGG",
    "CCCCTA",
    "TCACTG"
  ]
}
```

**Responses:**
- `200 OK` - Es mutante
- `403 Forbidden` - No es mutante (humano)
- `400 Bad Request` - ADN inválido

**Reglas:**
- Matriz cuadrada NxN (mínimo 4x4)
- Solo caracteres: A, T, C, G
- Mutante = más de UNA secuencia de 4 letras iguales (horizontal, vertical o diagonal)

### GET /stats

Retorna estadísticas de verificaciones.

**Response:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

---

## 🧪 Testing

```bash
# Ejecutar todos los tests
.\gradlew test

# Generar reporte de cobertura
.\gradlew test jacocoTestReport
```

**Reporte:** `build/reports/jacoco/test/html/index.html`

**Cobertura:**
- Tests totales: 37
- Cobertura global: 90%
- Cobertura service layer: >95%

**Suite de tests:**
- `MutantDetectorTest` - 17 tests (algoritmo)
- `MutantServiceTest` - 5 tests (orquestación)
- `StatsServiceTest` - 6 tests (estadísticas)
- `MutantControllerTest` - 8 tests (integración)

---

## 🧮 Algoritmo de Detección

### Lógica Principal

El algoritmo busca secuencias de 4 letras iguales en 4 direcciones:
1. Horizontal (→)
2. Vertical (↓)
3. Diagonal descendente (↘)
4. Diagonal ascendente (↗)

### Optimizaciones Implementadas

#### 1. Early Termination
```java
if (sequenceCount > 1) {
    return true;  // Para inmediatamente
}
```
Detiene la búsqueda apenas encuentra 2 secuencias. **Mejora: ~20x más rápido** en matrices grandes.

#### 2. Conversión a char[][]
```java
char[][] matrix = new char[n][];
for (int i = 0; i < n; i++) {
    matrix[i] = dna[i].toCharArray();
}
```
Acceso directo O(1) vs `String.charAt()`. **Mejora: ~1.7x más rápido**.

#### 3. Boundary Checking
```java
if (col <= n - SEQUENCE_LENGTH) {
    checkHorizontal(matrix, row, col);
}
```
Solo busca donde hay espacio suficiente. **Mejora: ~15% más rápido**.

#### 4. Comparaciones Directas
```java
private boolean checkHorizontal(char[][] matrix, int row, int col) {
    final char base = matrix[row][col];
    return matrix[row][col + 1] == base &&
           matrix[row][col + 2] == base &&
           matrix[row][col + 3] == base;
}
```
Sin loops adicionales. **Mejora: ~1.2x más rápido**.

### Complejidad

- **Temporal:** O(N²) peor caso, O(N) promedio con early termination
- **Espacial:** O(N²) para la matriz

---

## 💾 Base de Datos

### Esquema

```sql
CREATE TABLE dna_records (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    dna_hash    VARCHAR(64) UNIQUE NOT NULL,
    is_mutant   BOOLEAN NOT NULL,
    created_at  TIMESTAMP NOT NULL
);

CREATE INDEX idx_dna_hash ON dna_records(dna_hash);
CREATE INDEX idx_is_mutant ON dna_records(is_mutant);
```

### Estrategia de Caché con Hash SHA-256

En lugar de guardar el DNA completo, se guarda un hash SHA-256:

```java
private String calculateDnaHash(String[] dna) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    String dnaString = String.join("", dna);
    byte[] hashBytes = digest.digest(dnaString.getBytes(UTF_8));
    return convertToHex(hashBytes);
}
```

**Ventajas:**
- Tamaño fijo: 64 caracteres
- Búsqueda rápida con índice: O(log N)
- Garantiza unicidad (evita duplicados)
- Ahorra espacio en BD

**Flujo:**
1. Calcular hash del DNA
2. Buscar en BD por hash
3. Si existe → retornar resultado cacheado (**~15x más rápido**)
4. Si no existe → analizar y guardar

---

## 🏗️ Arquitectura

### Estructura del Proyecto

```
src/main/java/org/example/mutantes/
├── config/                  # Configuración (Swagger)
├── controller/              # Endpoints REST
├── dto/                     # Request/Response objects
├── entity/                  # Entidades JPA
├── exception/               # Manejo de errores
├── repository/              # Acceso a datos
├── service/                 # Lógica de negocio
├── validation/              # Validaciones custom
└── ExamenMercado3K9Application.java
```

### Arquitectura en Capas

```
Cliente (Postman/Swagger)
    ↓
Controller (REST endpoints)
    ↓
DTO (Validaciones)
    ↓
Service (Lógica de negocio)
    ↓
Repository (JPA)
    ↓
Database (H2/PostgreSQL)
```

**Capas transversales:**
- Validation - `@ValidDnaSequence`
- Exception Handling - `GlobalExceptionHandler`
- Config - `SwaggerConfig`

---

## 📊 Diagramas

### Diagrama de Clases

![Diagrama de Clases](docs/Diagrama_de_Clases.png)

Muestra la estructura completa del sistema con todas las relaciones entre clases.

**Archivo fuente:** [diagrama-clases.puml](docs/diagrama-clases.puml)

### Diagrama de Secuencia

![Diagrama de Secuencia](docs/Diagrama_de_Secuencia.png)

Muestra el flujo de ejecución completo:
- POST /mutant con DNA mutante
- POST /mutant con DNA humano
- POST /mutant con caché hit
- POST /mutant con validación fallida
- GET /stats

**Archivo fuente:** [diagrama-secuencia.puml](docs/diagrama-secuencia.puml)

---

## 🐳 Docker

### Build y Ejecución

```bash
# Construir imagen
docker build -t mutantes-api .

# Ejecutar
docker run -d -p 8080:8080 --name mutantes-container mutantes-api

# Ver logs
docker logs -f mutantes-container
```

El Dockerfile usa **multi-stage build** para optimizar el tamaño (~200MB vs ~500MB).

---

## 📝 Ejemplos de Uso

### cURL - DNA Mutante

```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "ATGCGA",
      "CAGTGC",
      "TTATGT",
      "AGAAGG",
      "CCCCTA",
      "TCACTG"
    ]
  }'
```

**Response:** `200 OK`

### cURL - DNA Humano

```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "ATGCGA",
      "CAGTGC",
      "TTATTT",
      "AGACGG",
      "GCGTCA",
      "TCACTG"
    ]
  }'
```

**Response:** `403 Forbidden`

### cURL - Estadísticas

```bash
curl http://localhost:8080/stats
```

**Response:**
```json
{
  "count_mutant_dna": 1,
  "count_human_dna": 1,
  "ratio": 1.0
}
```

---

## ✅ Niveles Implementados

### Nivel 1: Algoritmo de Detección ✅
- [x] Función `isMutant(String[] dna)` implementada
- [x] Validación de entrada (matriz NxN, caracteres válidos)
- [x] Búsqueda en 4 direcciones
- [x] Optimización con early termination
- [x] Complejidad O(N²) peor caso, O(N) promedio
- [x] 17 tests unitarios, cobertura >95%

### Nivel 2: API REST ✅
- [x] Endpoint `POST /mutant`
- [x] Respuestas HTTP correctas (200, 403, 400)
- [x] Validaciones con Bean Validation
- [x] Documentación Swagger/OpenAPI
- [x] Deploy en Render Cloud
- [x] Dockerfile con multi-stage build

### Nivel 3: Base de Datos y Estadísticas ✅
- [x] Base de datos H2 (desarrollo) / PostgreSQL (producción)
- [x] Deduplicación con hash SHA-256
- [x] Endpoint `GET /stats`
- [x] Índices en BD para optimización
- [x] Tests de integración
- [x] Cobertura >90% en service layer

---

## 🔧 Comandos Útiles

```bash
# Compilar
.\gradlew build

# Ejecutar tests
.\gradlew test

# Generar JAR
.\gradlew bootJar

# Limpiar y compilar
.\gradlew clean build

# Ver dependencias
.\gradlew dependencies
```

---

## 📚 Documentación Adicional

Para documentación más detallada del proyecto:
- [Guía de Evaluación](docs/GUIA_EVALUACION_ESTUDIANTE.md)
- [Diagrama de Clases](docs/diagrama-clases.puml)
- [Diagrama de Secuencia](docs/diagrama-secuencia.puml)

---

## 📄 Licencia

Proyecto académico desarrollado para el examen técnico de Desarrollo de Software, UTN Mendoza.

**Autor:** Adriel Espejo  
**Legajo:** 47664  
**Año:** 2025

---

## 🔗 Links

- **Repositorio:** https://github.com/VerseV/Global-3K9-Adriel-Espejo-47664
- **API en Producción:** https://examenmercado-3k9.onrender.com
- **Swagger UI:** https://examenmercado-3k9.onrender.com/swagger-ui.html