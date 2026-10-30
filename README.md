# Catálogo de productos

Aplicación full-stack para gestionar productos y categorías con Spring Boot, React y PostgreSQL. Los usuarios estándar solo pueden consultar y modificar sus propios productos y categorías. Los administradores pueden consultar y editar usuarios y roles.

## Requisitos

- Docker Desktop con Docker Compose, o bien Java 17+, Maven 3.9+, Node.js 20+ y PostgreSQL 16.
- La base de datos se llama `product_catalog_db`.

## Ejecutar con Docker Compose

1. Copia `.env.example` a `.env` y define `DB_PASSWORD` con la contraseña de PostgreSQL que vayas a utilizar. `JWT_SECRET` debe ser una clave aleatoria de al menos 32 caracteres. Las contraseñas de ejemplo son solo para desarrollo.
2. Desde la raíz del proyecto:

   ```powershell
   docker compose up --build
   ```

3. Abre <http://localhost:5173>. La API queda disponible en <http://localhost:8080/api>. PostgreSQL de Docker se publica en el puerto `5433` para evitar conflictos con una instalación local de PostgreSQL que use `5432`.
4. Para detener los servicios: `docker compose down`. Para borrar también los datos persistidos de desarrollo: `docker compose down -v`.

Compose levanta PostgreSQL 16, el backend y el frontend. El volumen `postgres_data` conserva la base cuando se apagan los contenedores.

## Ejecución local sin Docker

### PostgreSQL y configuración

Crea la base `product_catalog_db` en PostgreSQL. La configuración predeterminada usa `localhost:5432` y el usuario `postgres`. Configura variables de entorno para adaptarla a tu instalación:

| Variable | Predeterminado de desarrollo | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/product_catalog_db` | URL JDBC |
| `DB_USERNAME` | `postgres` | Usuario de PostgreSQL |
| `DB_PASSWORD` | `postgres` | Contraseña de PostgreSQL; usa la asignada en tu servidor |
| `JWT_SECRET` | Clave de desarrollo incluida en `application.yml` | Clave aleatoria para firmar tokens; cámbiala fuera de desarrollo |
| `ADMIN_EMAIL` | `admin@example.com` | Correo para la cuenta ADMIN inicial |
| `ADMIN_PASSWORD` | `Admin123!` | Contraseña para la cuenta ADMIN inicial |
| `CORS_ALLOWED_ORIGIN` | `http://localhost:5173` | Origen permitido para la interfaz web |
| `SERVER_PORT` | `8080` | Puerto del backend |
| `VITE_API_URL` | `http://localhost:8080/api` | URL base usada por el frontend |

En `.env`, escribe entre comillas simples los valores que contengan `$` (por ejemplo, `DB_PASSWORD='mi$clave'`), para que Docker Compose no interprete la parte posterior al signo como otra variable. El ejemplo incluido lo muestra así. No subas el archivo `.env` real al control de versiones.

### Backend

```powershell
cd product_catalog_backend
mvn spring-boot:run
```

La aplicación guarda sus tablas directamente en el esquema `public` de `product_catalog_db`. El SQL de inicialización agrega los campos de auditoría a las tablas existentes; Hibernate crea/actualiza las entidades y el backend crea el administrador inicial si todavía no existe. En pgAdmin, para la base de Docker, conéctate a `localhost:5433` (PostgreSQL local continúa en `5432`) y revisa **Databases → product_catalog_db → Schemas → public → Tables**; actualiza `Tables` con **Refresh**.

### Frontend

```powershell
cd product_catalog_frontend
npm.cmd install
npm.cmd run dev
```

Abre <http://localhost:5173>. Para cambiar la API, define `VITE_API_URL` antes de iniciar Vite.

## Usuarios de prueba

Con la configuración predeterminada de desarrollo:

- **Administrador:** `admin@example.com` / `Admin123!`
- **Usuario estándar:** regístralo desde la pantalla de registro o usa la solicitud **Registro USER** de Postman; ejemplo `usuario@example.com` / `User1234!`.

El usuario ADMIN se crea al iniciar el backend. Cambia las credenciales de ejemplo antes de cualquier despliegue. El registro público siempre crea cuentas `USER`; el rol ADMIN solo se asigna al usuario inicial o desde una sesión ADMIN.

## API principal

Todas las rutas, excepto registro e inicio de sesión, requieren `Authorization: Bearer <token>`.

| Método | Endpoint | Acceso |
|---|---|---|
| `POST` | `/api/auth/register` | Público; registra un USER |
| `POST` | `/api/auth/login` | Público |
| `GET`, `PUT` | `/api/auth/me` | Autenticado; consultar/editar nombre y contraseña propia |
| `GET`, `POST` | `/api/products` | Autenticado; productos propios |
| `GET` | `/api/products/{id}` | Autenticado; solo propietario |
| `PUT`, `DELETE` | `/api/products/{id}` | Autenticado; solo propietario |
| `GET` | `/api/products?categoryId={id}&page=0&size=10` | Filtrar y paginar productos propios |
| `GET`, `POST` | `/api/categories` | Autenticado; categorías propias |
| `GET` | `/api/categories/{id}` | Autenticado; solo propietario |
| `PUT`, `DELETE` | `/api/categories/{id}` | Autenticado; solo propietario |
| `GET` | `/api/users?page=0&size=10` | Solo ADMIN |
| `PUT` | `/api/users/{id}` | Solo ADMIN; edita perfil, rol y opcionalmente contraseña |

Los listados devuelven el formato paginado de Spring (`content`, `totalElements`, `totalPages`, entre otros). Un producto acepta `name`, `description`, `price` y `categoryIds`, por ejemplo:

```json
{
  "name": "Audífonos",
  "description": "Bluetooth",
  "price": 79.99,
  "categoryIds": [1, 2]
}
```

Los errores de validación devuelven `400`, una sesión ausente o inválida `401`, permisos insuficientes `403` y recursos inexistentes/no propios `404`, con un mensaje legible. Importa [postman_collection.json](./postman_collection.json) en Postman para probar autenticación y operaciones. La colección incluye credenciales ADMIN locales de ejemplo; actualízalas si configuras otras variables.

## Roles, privacidad y relación muchos a muchos

- `USER` solo recibe sus propios registros. Productos y categorías usan `created_by` como propietario; las consultas, edición y eliminación verifican ese valor en el backend. No existe una columna `owner_id` ni se confía en un identificador de propietario enviado por el cliente.
- Cada usuario puede consultar y editar su propio nombre y contraseña desde **Mi perfil**; el rol y correo no se pueden cambiar desde esa vista.
- `ADMIN` puede listar y editar perfiles de usuario, incluido el rol y la contraseña opcional; las contraseñas continúan guardándose con BCrypt. Las rutas administrativas exigen `ROLE_ADMIN`.
- `Product` y `Category` conservan la relación muchos a muchos mediante `product_categories`, modelada como entidad de asociación para auditar también sus fechas y usuarios. La API recibe `categoryIds`; el servicio valida que todas pertenezcan al usuario autenticado antes de guardar. La respuesta usa DTOs con las categorías asociadas, sin exponer entidades.
- Todas las tablas (`app_users`, `products`, `categories` y `product_categories`) guardan `created_by` y `updated_by` como `String`, y `created_at` y `updated_at` como `LocalDateTime`. Los campos de propietario de productos y categorías almacenan el ID de usuario como texto, sin una relación JPA con `AppUser`.
- Eliminar una categoría devuelve `409 Conflict` si todavía tiene productos asociados; primero se deben quitar las asociaciones editando los productos.

## Estructura

```text
product_catalog_app/
├── product_catalog_backend/
│   └── src/main/java/com/catalog/
│       ├── config/       # Seguridad, CORS y administrador inicial
│       ├── controller/  # Endpoints REST y errores
│       ├── dto/         # Contratos de entrada y salida
│       ├── model/       # Entidades JPA y roles
│       ├── repository/  # Repositorios Spring Data
│       ├── security/    # JWT y filtro de autenticación
│       └── service/     # Reglas de negocio y aislamiento
├── product_catalog_frontend/
│   └── src/
│       ├── api/          # Axios y funciones de API
│       ├── auth/         # Contexto de sesión
│       ├── components/   # Layout, rutas protegidas y paginación
│       └── pages/        # Login/registro y pantallas de catálogo/admin
├── docker-compose.yml
└── postman_collection.json
```

## Pruebas

Backend, JUnit 5, Mockito y Spring Boot Test:

```powershell
cd product_catalog_backend
mvn test
```

Frontend, Vitest y React Testing Library:

```powershell
cd product_catalog_frontend
npm.cmd install
npm.cmd test
npm.cmd run build
```

Las pruebas actuales cubren la protección de asociaciones entre propietarios y la paginación de la interfaz. Es posible ampliar con pruebas de integración usando PostgreSQL de prueba y cobertura de flujos E2E.

## Decisiones técnicas y mejoras futuras

- JWT stateless para autenticar llamadas, BCrypt para credenciales y DTOs validados en los límites REST.
- Paginación server-side para productos, categorías y usuarios, evitando enviar colecciones completas en los listados.
- Aislamiento por propietario aplicado en servicios y repositorios, además de reglas de rol en Spring Security.
- Para producción mejoraría la gestión de migraciones con Flyway, usaría refresh tokens rotatorios y almacenamiento de sesión más seguro que `localStorage`, añadiría rate limiting/auditoría, observabilidad, pruebas de integración contra PostgreSQL y una pipeline CI.
