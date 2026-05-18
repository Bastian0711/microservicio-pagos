# Microservicio Pagos

## Descripción

Microservicio encargado de gestionar pagos dentro del sistema de eventos gastronómicos.

## Funcionalidades

* Crear pagos
* Listar pagos
* Buscar pagos por ID
* Actualizar pagos
* Eliminar pagos
* Cancelar pagos
* Comunicación con microservicio de pedidos
* Comunicación con microservicio de subpedidos

## Tecnologías utilizadas

* Java 21
* Spring Boot
* MySQL
* Maven
* Docker
* Docker Compose

## Ejecución del proyecto

```bash
docker compose up -d
```

## Endpoints principales

### Obtener pagos

GET /api/v2/pagos

### Obtener pago por ID

GET /api/v2/pagos/{id}

### Crear pago

POST /api/v2/pagos

### Actualizar pago

PUT /api/v2/pagos/{id}

### Eliminar pago

DELETE /api/v2/pagos/{id}

## Validaciones

* Campos obligatorios
* Validación de montos positivos
* Manejo de errores controlados con Bean Validation
