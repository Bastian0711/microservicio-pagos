# Microservicio Pagos

## Descripción

Microservicio encargado de gestionar los pagos dentro del sistema de eventos gastronómicos. Un pago se genera automáticamente a partir de un pedido ya confirmado: el servicio consulta el estado y el total del pedido en el microservicio de **Pedidos**, y si todo es válido, registra el pago y solicita al microservicio de **SubPedidos** la generación de los subpedidos correspondientes.

## Funcionalidades

* Crear (procesar) pagos a partir de un pedido confirmado
* Listar todos los pagos
* Buscar pago por ID
* Actualizar el método de pago
* Cancelar un pago
* Devolver un pago
* Eliminar un pago
* Comunicación con el microservicio de **Pedidos** (vía Feign) para validar el pedido asociado
* Comunicación con el microservicio de **SubPedidos** (vía Feign) para generar subpedidos tras un pago exitoso
* Registro en Eureka como cliente de descubrimiento de servicios
* Documentación interactiva con Swagger / OpenAPI

## Tecnologías utilizadas

* Java 21
* Spring Boot 3.5.14
* Spring Data JPA
* Spring Cloud OpenFeign
* Spring Cloud Netflix Eureka Client
* MySQL 8.0
* springdoc-openapi (Swagger UI)
* Lombok
* Maven
* Docker / Docker Compose
* H2 (para pruebas)

## Arquitectura y flujo de un pago

1. El cliente envía el `idPedido` y el `metodoPago` para crear un pago.
2. El servicio consulta el pedido en el microservicio de Pedidos (`pedido.service.url`).
3. Si el pedido no existe, responde `404 NOT_FOUND`. Si el servicio de Pedidos no está disponible, responde `503 SERVICE_UNAVAILABLE`.
4. Si el pedido existe pero su estado no es `CONFIRMADO`, se rechaza con `409 CONFLICT`.
5. Si el pedido es válido, se crea el pago con estado `APROBADO`, usando el total y el usuario del pedido.
6. Se solicita al microservicio de SubPedidos la generación de los subpedidos asociados al pedido.

## Ejecución del proyecto

El repositorio incluye dos formas de trabajar:

### Opción 1: entorno de desarrollo (contenedor Maven + MySQL)

```bash
docker compose up -d
```

Esto levanta:
* `mysql_servidor`: base de datos MySQL en el puerto `3307` (host) → `3306` (contenedor).
* `entorno_pagos`: contenedor con Maven y JDK 21 montando el código fuente (`./codigo-fuente`) en `/app`, listo para compilar y ejecutar la aplicación manualmente (por ejemplo, con `mvn spring-boot:run` dentro del contenedor).

### Opción 2: imagen de la aplicación

```bash
mvn clean package -DskipTests
docker build -t microservicio-pagos .
docker run -p 8087:8080 microservicio-pagos
```

> El servicio corre por defecto en el puerto **8087** (definido en `application.properties`), aunque el `Dockerfile` expone el `8080`; ajusta el mapeo de puertos según cómo lo despliegues.

### Configuración relevante

En `application.properties`:

* `spring.datasource.url`: conexión a MySQL (`pagos_backend`).
* `pedido.service.url` / `subpedido.service.url`: URLs de los microservicios de Pedidos y SubPedidos (consumidos internamente en `/api/v3/orden/{id}` y `/api/v3/subpedidos/generar`, respectivamente).
* `eureka.client.service-url.defaultZone`: URL del servidor Eureka.
* `springdoc.swagger-ui.path=/doc/swagger-ui.html`: ruta de la documentación Swagger.

## Endpoints principales

Base path: `/api/v3/pagos`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v3/pagos` | Lista todos los pagos |
| GET | `/api/v3/pagos/{id}` | Busca un pago por ID |
| POST | `/api/v3/pagos` | Procesa/crea un pago a partir de un pedido confirmado |
| PUT | `/api/v3/pagos/{id}` | Actualiza el método de pago |
| PUT | `/api/v3/pagos/{id}/cancelar` | Cancela un pago (requiere motivo) |
| PUT | `/api/v3/pagos/{id}/devolver` | Devuelve un pago aprobado (requiere motivo) |
| DELETE | `/api/v3/pagos/{id}` | Elimina un pago |

### Crear pago

`POST /api/v3/pagos`

```json
{
  "idPedido": 10,
  "metodoPago": "TARJETA_CREDITO"
}
```

### Actualizar pago

`PUT /api/v3/pagos/{id}`

```json
{
  "metodoPago": "TRANSFERENCIA"
}
```

### Cancelar / devolver pago

`PUT /api/v3/pagos/{id}/cancelar` y `PUT /api/v3/pagos/{id}/devolver`

```json
{
  "motivo": "Solicitud del cliente"
}
```

## Modelo de datos (respuesta `PagoDTO`)

```json
{
  "id": 1,
  "idPedido": 10,
  "nombreCliente": "Cliente 5",
  "metodoPago": "TARJETA_CREDITO",
  "monto": 25990.0,
  "estado": "APROBADO",
  "motivoCancelacion": null,
  "motivoDevolucion": null
}
```

Estados posibles: `APROBADO`, `CANCELADO`, `DEVUELTO`.

## Validaciones y manejo de errores

* Campos obligatorios en la creación (`idPedido`, `metodoPago`) mediante Bean Validation.
* El `idPedido` debe ser un valor positivo.
* Motivo obligatorio para cancelar o devolver un pago.
* No se puede cancelar un pago que ya está cancelado.
* Solo se pueden devolver pagos en estado `APROBADO`.
* Solo se pueden procesar pagos de pedidos en estado `CONFIRMADO`.

Los errores se devuelven en un formato consistente mediante un manejador global de excepciones:

```json
{
  "error": "NOT_FOUND",
  "message": "Pago no encontrado"
}
```

| Código de error | HTTP Status | Causa |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Falla de validación de campos (Bean Validation) |
| `BAD_REQUEST` | 400 | Error de negocio genérico |
| `NOT_FOUND` | 404 | Pago o pedido no encontrado |
| `BUSINESS_ERROR` | 409 | Regla de negocio incumplida (ej. estado inválido) |
| `SERVICE_UNAVAILABLE` | 503 | Microservicio de Pedidos o SubPedidos no disponible |
| `INTERNAL_ERROR` | 500 | Error inesperado del servidor |

## Documentación de la API

Con la aplicación en ejecución, la documentación Swagger UI está disponible en:

```
http://localhost:8087/doc/swagger-ui.html
```

## Pruebas

El proyecto incluye pruebas unitarias e de integración para el modelo, repositorio, servicio y controlador (`src/test/java`), ejecutables con:

```bash
mvn test
```
