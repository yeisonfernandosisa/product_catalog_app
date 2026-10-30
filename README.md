# Product Catalog

## Instalar y ejecutar el backend y el frontend

### Opción recomendada: Docker

Necesitas tener Docker Desktop instalado. Desde la carpeta principal del proyecto, copia el archivo de ejemplo:

```powershell
Copy-Item .env.example .env
```

Puedes dejar los valores de ejemplo para probar en tu computadora. Luego inicia la aplicación:

```powershell
docker compose up --build
```

Abre el frontend en <http://localhost:5173>. El backend estará en <http://localhost:8080>.
Para detener todo, presiona `Ctrl+C` y ejecuta `docker compose down`.

### Ejecutar backend y frontend por separado

Necesitas Java 17, Maven, Node.js y PostgreSQL. Crea la base de datos como se explica en la siguiente sección y configura las variables necesarias.

En una terminal, inicia el backend:

```powershell
Set-Location product_catalog_backend
mvn spring-boot:run
```

En otra terminal, instala e inicia el frontend:

```powershell
Set-Location product_catalog_frontend
npm ci
npm run dev
```

El frontend estará en <http://localhost:5173>.

## Base de datos y variables de entorno

La aplicación usa PostgreSQL y necesita una base de datos llamada `product_catalog_db`. Si ejecutas el proyecto con Docker, Docker Compose crea la base automáticamente y la expone en el puerto `5433`.

Si ejecutas el backend por separado, crea esa base de datos en PostgreSQL. Por defecto, Spring Boot busca PostgreSQL en `localhost:5432`.

El archivo `.env.example` muestra las variables disponibles. Cópialo como `.env` y configura estos valores:

| Variable              | Para qué sirve                                                                                                 |
| --------------------- | -------------------------------------------------------------------------------------------------------------- |
| `DB_URL`              | Dirección de la base de datos. Para ejecución local usa `jdbc:postgresql://localhost:5432/product_catalog_db`. |
| `DB_USERNAME`         | Usuario de PostgreSQL.                                                                                         |
| `DB_PASSWORD`         | Contraseña de PostgreSQL.                                                                                      |
| `JWT_SECRET`          | Clave usada para las sesiones. Configura una clave aleatoria de al menos 32 caracteres.                        |
| `ADMIN_EMAIL`         | Correo del administrador inicial.                                                                              |
| `ADMIN_PASSWORD`      | Contraseña del administrador inicial.                                                                          |
| `CORS_ALLOWED_ORIGIN` | Dirección permitida para el frontend; normalmente `http://localhost:5173`.                                     |
| `VITE_API_URL`        | Dirección de la API; normalmente `http://localhost:8080/api`.                                                  |

Docker Compose lee las variables de `.env`. Si ejecutas los programas por separado, ese archivo no se carga automáticamente. En PowerShell, configura las variables en la terminal donde iniciarás el programa. Por ejemplo:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/product_catalog_db"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "tu-contraseña-de-postgres"
$env:JWT_SECRET = "escribe-aqui-una-clave-aleatoria-de-32-caracteres-o-mas"
$env:ADMIN_EMAIL = "admin@example.com"
$env:ADMIN_PASSWORD = "change-me-now"
$env:CORS_ALLOWED_ORIGIN = "http://localhost:5173"
$env:VITE_API_URL = "http://localhost:8080/api"
```

No subas tu archivo `.env` ni compartas contraseñas reales.

## Usuario de prueba

Si copiaste `.env.example` sin cambiar los datos del administrador, puedes ingresar con:

- Correo: `admin@example.com`
- Contraseña: `change-me-now`
- Rol: administrador

Si cambias `ADMIN_EMAIL` o `ADMIN_PASSWORD`, usa los datos que configuraste. Sin un archivo `.env`, Docker Compose usa `admin@example.com` y `Admin123!`. El usuario administrador se crea al iniciar la aplicación si todavía no existe.

## Decisiones técnicas

- Usé Spring Boot y Java para crear una API, y React para la interfaz.
- Usé PostgreSQL para guardar los datos y Spring Data JPA para consultarlos.
- Usé JWT para mantener la sesión y Spring Security para proteger las rutas.
- Separé el código por responsabilidades: páginas y componentes en el frontend; controladores, servicios y repositorios en el backend.
- Usé JUnit y Mockito para probar el backend, y React Testing Library para probar acciones visibles para el usuario.

## Qué mejoraría con más tiempo

- Agregaría más pruebas para los flujos principales, incluyendo pruebas que usen una base de datos real.
- Usaría Flyway para llevar un historial claro de los cambios en la base de datos.
- Agregaría documentación de la API y mejores herramientas para revisar errores en producción.
