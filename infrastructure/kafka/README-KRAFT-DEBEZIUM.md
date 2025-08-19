# 🚀 Kafka con KRaft + Debezium + PostgreSQL

Este proyecto implementa un cluster de **Kafka con KRaft** (sin Zookeeper) integrado con **Debezium** para Change Data Capture (CDC) usando **PostgreSQL**.

## 🏗️ Arquitectura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Kafka Broker  │    │   Kafka Broker  │    │   Kafka Broker  │
│   (KRaft Mode)  │    │   (KRaft Mode)  │    │   (KRaft Mode)  │
│   Node ID: 1    │    │   Node ID: 2    │    │   Node ID: 3    │
│                 │    │                 │    │                 │
│ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ │  Controller │ │    │ │  Controller │ │    │ │  Controller │ │
│ │   Broker    │ │    │ │   Broker    │ │    │ │   Broker    │ │
│ └─────────────┘ │    │ └─────────────┘ │    │ └─────────────┘ │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │  Schema Registry│
                    │   (Confluent)   │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │ Debezium Connect│
                    │  (Kafka Connect)│
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   PostgreSQL    │
                    │   (Database)    │
                    └─────────────────┘
```

## 🎯 Características Principales

### ✅ **KRaft (Kafka Raft)**
- **Sin Zookeeper**: Eliminamos la dependencia de Zookeeper
- **Mejor rendimiento**: Menor latencia y mayor throughput
- **Configuración simplificada**: Menos componentes que gestionar
- **Escalabilidad mejorada**: Mejor manejo de clusters grandes

### ✅ **Debezium CDC**
- **Change Data Capture**: Captura cambios en tiempo real
- **PostgreSQL**: Soporte nativo para PostgreSQL
- **Avro**: Serialización con Schema Registry
- **Transformaciones**: Unwrapping de eventos CDC

### ✅ **PostgreSQL**
- **Logical Replication**: Configurado para CDC
- **Triggers automáticos**: Actualización de timestamps
- **Datos de ejemplo**: Base de datos completa para testing

## 📁 Estructura del Proyecto

```
kafka/
├── docker-compose.yml                    # Configuración básica KRaft
├── docker-compose-dev.yml               # Configuración desarrollo KRaft
├── docker-compose-debezium-postgres.yml # Configuración completa con Debezium
├── postgres-init/
│   ├── 01-create-tables.sql            # Esquema de base de datos
│   └── 02-sample-data.sql              # Datos de ejemplo
├── test-debezium-postgres-cdc.sql      # Script de pruebas CDC
└── README-KRAFT-DEBEZIUM.md            # Este archivo
```

## 🚀 Inicio Rápido

### 1. Configuración Básica (Solo Kafka)
```bash
# Levantar cluster Kafka básico
docker-compose up -d

# Verificar servicios
docker-compose ps

# Acceder a Kafka UI
# http://localhost:8080
```

### 2. Configuración Completa (Kafka + Debezium + PostgreSQL)
```bash
# Levantar toda la infraestructura
docker-compose -f docker-compose-debezium-postgres.yml up -d

# Verificar todos los servicios
docker-compose -f docker-compose-debezium-postgres.yml ps

# Acceder a las interfaces web:
# - Kafka UI: http://localhost:8080
# - Schema Registry: http://localhost:8081
# - Debezium Connect: http://localhost:8083
```

## 🔧 Configuración KRaft

### Variables de Entorno Clave

```yaml
# KRaft Configuration
KAFKA_NODE_ID: 1
KAFKA_PROCESS_ROLES: 'broker,controller'
KAFKA_CONTROLLER_QUORUM_VOTERS: '1@kafka-broker-1:9093,2@kafka-broker-2:9093,3@kafka-broker-3:9093'

# Listeners
KAFKA_LISTENERS: 'PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093,PLAINTEXT_HOST://0.0.0.0:9094'
KAFKA_CONTROLLER_LISTENER_NAMES: 'CONTROLLER'
KAFKA_INTER_BROKER_LISTENER_NAME: 'PLAINTEXT'
```

### Puertos Externos

| Servicio | Puerto Interno | Puerto Externo | Descripción |
|----------|----------------|----------------|-------------|
| Kafka Broker 1 | 9092 | 19092 | Cliente Kafka |
| Kafka Broker 1 | 9093 | 19093 | Controller KRaft |
| Kafka Broker 2 | 9092 | 29092 | Cliente Kafka |
| Kafka Broker 2 | 9093 | 29093 | Controller KRaft |
| Kafka Broker 3 | 9092 | 39092 | Cliente Kafka |
| Kafka Broker 3 | 9093 | 39093 | Controller KRaft |
| Kafka UI | 8080 | 8080 | Interfaz web |
| Schema Registry | 8081 | 8081 | API Schema Registry |
| Debezium Connect | 8083 | 8083 | API Kafka Connect |
| PostgreSQL | 5432 | 5432 | Base de datos |

## 🗄️ Base de Datos PostgreSQL

### Esquema de Tablas

```sql
-- Tablas principales
customers          # Clientes del sistema
products           # Productos disponibles
orders             # Órdenes de pedidos
order_items        # Items de cada orden
payments           # Pagos realizados
restaurants        # Restaurantes
restaurant_products # Relación restaurante-producto
```

### Configuración CDC

```sql
-- Configuración para Debezium
wal_level=logical
max_wal_senders=1
max_replication_slots=1
wal_keep_size=64
```

## 🔄 Debezium CDC

### Configuración del Connector

```json
{
  "name": "postgres-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "debezium",
    "database.password": "dbz",
    "database.dbname": "inventory",
    "database.server.name": "postgres-server",
    "plugin.name": "pgoutput",
    "transforms": "unwrap",
    "transforms.unwrap.type": "io.debezium.transforms.ExtractNewDocumentState"
  }
}
```

### Topics Generados

Para cada tabla, Debezium crea un topic con el formato:
```
postgres-server.inventory.{table_name}
```

Ejemplos:
- `postgres-server.inventory.customers`
- `postgres-server.inventory.products`
- `postgres-server.inventory.orders`
- `postgres-server.inventory.payments`

## 🧪 Testing CDC

### 1. Verificar que Debezium esté funcionando

```bash
# Verificar el estado del connector
curl -X GET http://localhost:8083/connectors/postgres-connector/status

# Verificar los topics creados
docker exec -it kafka-broker-1 kafka-topics --list --bootstrap-server localhost:9092
```

### 2. Ejecutar cambios en la base de datos

```bash
# Conectar a PostgreSQL
docker exec -it postgres psql -U debezium -d inventory

# Ejecutar el script de pruebas
\i /docker-entrypoint-initdb.d/test-debezium-postgres-cdc.sql
```

### 3. Verificar mensajes en Kafka

```bash
# Consumir mensajes de un topic específico
docker exec -it kafka-broker-1 kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic postgres-server.inventory.customers \
  --from-beginning
```

## 📊 Monitoreo

### Kafka UI
- **URL**: http://localhost:8080
- **Funcionalidades**:
  - Ver topics y particiones
  - Consumir mensajes en tiempo real
  - Monitorear consumer groups
  - Ver schemas de Avro

### Schema Registry
- **URL**: http://localhost:8081
- **API**: http://localhost:8081/subjects
- **Funcionalidades**:
  - Gestionar schemas Avro
  - Versionado de schemas
  - Compatibilidad de schemas

### Debezium Connect
- **URL**: http://localhost:8083
- **API**: http://localhost:8083/connectors
- **Funcionalidades**:
  - Gestionar connectors
  - Ver estado de tareas
  - Configurar transformaciones

## 🔧 Comandos Útiles

### Gestión de Contenedores

```bash
# Levantar servicios
docker-compose -f docker-compose-debezium-postgres.yml up -d

# Ver logs
docker-compose -f docker-compose-debezium-postgres.yml logs -f

# Parar servicios
docker-compose -f docker-compose-debezium-postgres.yml down

# Limpiar volúmenes
docker-compose -f docker-compose-debezium-postgres.yml down -v
```

### Gestión de Topics

```bash
# Listar topics
docker exec -it kafka-broker-1 kafka-topics --list --bootstrap-server localhost:9092

# Ver detalles de un topic
docker exec -it kafka-broker-1 kafka-topics --describe --topic payment-request --bootstrap-server localhost:9092

# Crear topic manual
docker exec -it kafka-broker-1 kafka-topics --create --topic test-topic --partitions 3 --replication-factor 3 --bootstrap-server localhost:9092
```

### Gestión de Connectors

```bash
# Listar connectors
curl -X GET http://localhost:8083/connectors

# Ver estado de un connector
curl -X GET http://localhost:8083/connectors/postgres-connector/status

# Eliminar connector
curl -X DELETE http://localhost:8083/connectors/postgres-connector

# Crear connector
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @connector-config.json
```

## 🚨 Troubleshooting

### Problemas Comunes

1. **Kafka no inicia**
   ```bash
   # Verificar logs
   docker-compose logs kafka-broker-1
   
   # Limpiar volúmenes
   docker-compose down -v
   docker-compose up -d
   ```

2. **Debezium no conecta a PostgreSQL**
   ```bash
   # Verificar que PostgreSQL esté listo
   docker exec -it postgres pg_isready -U debezium
   
   # Verificar logs de Debezium
   docker-compose logs debezium-connect
   ```

3. **No se ven mensajes CDC**
   ```bash
   # Verificar que el connector esté activo
   curl -X GET http://localhost:8083/connectors/postgres-connector/status
   
   # Verificar que PostgreSQL tenga WAL habilitado
   docker exec -it postgres psql -U debezium -d inventory -c "SHOW wal_level;"
   ```

## 📚 Recursos Adicionales

- [Kafka KRaft Documentation](https://kafka.apache.org/documentation/#kraft)
- [Debezium PostgreSQL Connector](https://debezium.io/documentation/reference/connectors/postgresql.html)
- [Confluent Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html)
- [Kafka UI Documentation](https://github.com/provectus/kafka-ui)

## 🤝 Contribución

Para contribuir al proyecto:

1. Fork el repositorio
2. Crea una rama para tu feature
3. Realiza los cambios
4. Ejecuta las pruebas
5. Envía un Pull Request

---

**¡Disfruta usando Kafka con KRaft y Debezium! 🎉**
