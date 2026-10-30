# Product Catalog

Aplicación de catálogo con API REST en Java y Spring Boot, interfaz en React y base de datos PostgreSQL.

## Requisitos

- Docker Desktop con Docker Compose, recomendado para ejecutar todo el proyecto.
- Para ejecutar los proyectos por separado: Java 17, Maven 3.9 o posterior, Node.js 24 y PostgreSQL.

## Instalar y ejecutar con Docker

Desde la carpeta raíz del proyecto:

```powershell
Copy-Item .env.example .env
```

Edita `.env` y configura al menos `DB_PASSWORD`, `JWT_SECRET` y `ADMIN_PASSWORD`. Luego ejecuta:

```powershell
docker compose up --build -d
docker compose ps
```

Servicios disponibles:

- Frontend: <http://localhost:5173>
- Backend: <http://localhost:8080>
- PostgreSQL: `localhost:5433`, base de datos `product_catalog_db`

Para detener los servicios sin borrar los datos:

```powershell
docker compose down
```

Los datos de PostgreSQL se conservan en el volumen de Docker. No uses `docker compose down -v` salvo que quieras eliminar también la base de datos.

## Configuración de base de datos y variables de entorno

Docker crea la base `product_catalog_db` en el esquema `public`. En pgAdmin, registra un servidor con host `localhost`, puerto `5433`, y las credenciales definidas en `.env` (`DB_USERNAME` y `DB_PASSWORD`). El puerto `5432` puede corresponder a otra instalación local de PostgreSQL.

Variables disponibles en `.env`:

| Variable              | Uso                                                                             | Valor de ejemplo                                      |
| --------------------- | ------------------------------------------------------------------------------- | ----------------------------------------------------- |
| `DB_URL`              | URL JDBC para ejecutar el backend fuera de Docker                               | `jdbc:postgresql://localhost:5432/product_catalog_db` |
| `DB_USERNAME`         | Usuario PostgreSQL                                                              | `postgres`                                            |
| `DB_PASSWORD`         | Contraseña PostgreSQL                                                           | Define una contraseña local                           |
| `JWT_SECRET`          | Clave de firma de los tokens; usa una clave aleatoria de al menos 32 caracteres | Define una clave aleatoria                            |
| `ADMIN_EMAIL`         | Correo del administrador inicial                                                | `admin@example.com`                                   |
| `ADMIN_PASSWORD`      | Contraseña del administrador inicial                                            | Define una contraseña local                           |
| `CORS_ALLOWED_ORIGIN` | Origen permitido para solicitudes del frontend                                  | `http://localhost:5173`                               |
| `VITE_API_URL`        | URL de la API usada por React                                                   | `http://localhost:8080/api`                           |

El archivo `.env` es para Docker Compose; Spring Boot no lo carga automáticamente al ejecutar Maven desde la terminal. Para ejecución local, configura esas variables en el entorno de ejecución o en la configuración de tu IDE. En Docker, el backend se conecta a PostgreSQL mediante el nombre del servicio `db`; `DB_URL` se usa para la ejecución local.

La aplicación inicializa el esquema y actualiza las tablas existentes al arrancar. La base configurada debe existir y estar disponible antes de iniciar el backend. Los listados de productos, categorías y usuarios muestran primero los registros más recientes (`createdAt` descendente); también puedes especificar `sort=createdAt,desc` junto con `page` y `size`.

## Ejecución por separado

Inicia PostgreSQL y crea `product_catalog_db`. Configura las variables anteriores en el entorno y ejecuta el backend:

```powershell
Set-Location product_catalog_backend
mvn spring-boot:run
```

En otra terminal, inicia el frontend:

```powershell
Set-Location product_catalog_frontend
npm ci
npm run dev
```

El frontend de desarrollo estará disponible en <http://localhost:5173>.

## Usuario de prueba

Con los valores predeterminados de Docker Compose:

- Correo: `admin@example.com`
- Contraseña: `Admin123!`
- Rol: `ADMIN`

Si configuraste `ADMIN_EMAIL` o `ADMIN_PASSWORD` en `.env`, usa esos valores. El administrador inicial se crea solo si ese correo aún no existe; cambiar la contraseña de configuración no modifica una cuenta ya creada.

## Decisiones técnicas

- Spring Boot organiza la API por controladores, servicios, repositorios, DTOs y entidades. React separa páginas, componentes reutilizables, autenticación y llamadas HTTP; Tailwind CSS 4 aplica estilos adaptables a móvil, tableta y escritorio.
- PostgreSQL almacena los datos. Spring Data JPA implementa el acceso paginado.
- JWT protege las rutas autenticadas; Spring Security aplica los roles y BCrypt cifra las contraseñas.
- `POST /api/auth/logout` revoca el JWT actual y elimina el token del navegador. La revocación se guarda en PostgreSQL hasta que el token expire; los registros vencidos se limpian al cerrar otra sesión.
- La relación muchos a muchos se modela con `product_categories`, una entidad de asociación con ID propio, columnas de auditoría y una restricción única sobre `(product_id, category_id)`.
- `created_by` guarda el correo y determina la propiedad de productos y categorías. Cuando ADMIN cambia el correo de un usuario, esos recursos se transfieren al correo nuevo.
- Las categorías con productos asociados no se pueden eliminar; primero hay que quitar las asociaciones.

## Pruebas

Para aplicar el formato estándar de Java (Google Java Format):

```powershell
Set-Location product_catalog_backend
mvn fmt:format
mvn fmt:check
```

Para verificar el formato de los archivos React/CSS y configuración compartida:

```powershell
Set-Location product_catalog_frontend
npm run format:check
```

Backend, JUnit 5, Mockito y Spring Boot Test:

```powershell
Set-Location product_catalog_backend
mvn test
```

Frontend, Vitest y React Testing Library:

```powershell
Set-Location product_catalog_frontend
npm ci
npm test
npm run build
```

La colección [postman_collection.json](./postman_collection.json) permite probar los endpoints principales.

## Mejoras posibles

- Incorporar migraciones versionadas con Flyway o Liquibase en lugar de depender de la inicialización de esquema de la aplicación.
- Ampliar las pruebas con escenarios de integración usando PostgreSQL y pruebas de componentes para los flujos principales.
- Añadir observabilidad, documentación OpenAPI y configuración de despliegue para producción, con secretos administrados fuera del repositorio.
