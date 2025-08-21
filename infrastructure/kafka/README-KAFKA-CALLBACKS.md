# Sistema de Callbacks de Kafka

## Descripción General

El sistema implementa un patrón de callback genérico para manejar las respuestas de éxito y fallo cuando se envían mensajes a Kafka. Esto permite un manejo asíncrono y robusto de las operaciones de mensajería.

## Arquitectura

### 1. KafkaMessageHelper Class

```java
@Slf4j
@Component
public class KafkaMessageHelper {

    public <K, V> ListenableFutureCallback<SendResult<K, V>>
    getKafkaCallback(String responseTopicName, V avroModel, String orderId, String avroModelName) {
        return new ListenableFutureCallback<SendResult<K, V>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending " + avroModelName +
                        " message {} to topic {}", avroModel.toString(), responseTopicName, ex);
            }

            @Override
            public void onSuccess(SendResult<K, V> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for order id: {}" +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
            }
        };
    }
}
```

### 2. KafkaProducer Implementation

El `KafkaProducerImpl` utiliza el callback genérico del `KafkaMessageHelper`:

```java
ListenableFuture<SendResult<K, V>> future = kafkaTemplate.send(topicName, key, message);

// Usar el callback genérico del KafkaMessageHelper
future.addCallback(kafkaMessageHelper.getKafkaCallback(topicName, message, key.toString(), message.getClass().getSimpleName()));
```

### 3. Ventajas de la Nueva Implementación

- **Genérico**: Un solo helper para todos los tipos de mensajes
- **Reutilizable**: Se puede usar en todos los servicios
- **Simple**: Menos código y más fácil de mantener
- **Consistente**: Mismo comportamiento en toda la aplicación

## Flujo de Ejecución

1. **Envío del Mensaje**: El publisher llama a `kafkaProducer.send()`
2. **Creación del Future**: KafkaTemplate crea un `ListenableFuture`
3. **Registro del Callback**: Se registra el callback genérico para manejar la respuesta
4. **Ejecución Asíncrona**: Kafka procesa el mensaje en background
5. **Callback Execution**: 
   - **Success**: Se ejecuta `onSuccess()` con metadata detallada
   - **Failure**: Se ejecuta `onFailure()` con logging de error

## Ventajas del Sistema

### 1. **Asincronía**
- No bloquea el hilo principal
- Permite procesamiento paralelo
- Mejora el rendimiento

### 2. **Manejo de Errores**
- Captura automática de excepciones
- Logging detallado de errores
- Información completa de metadata

### 3. **Simplicidad**
- Un solo helper para todos los servicios
- No necesita implementaciones específicas
- Fácil de mantener y extender

### 4. **Observabilidad**
- Logging estructurado con metadata completa
- Información de topic, partition, offset y timestamp
- Trazabilidad completa

## Ejemplo de Uso

```java
@Component
public class CreateOrderKafkaMessagePublisher {
    
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        
        PaymentRequestModel paymentRequestModel = orderMessagingDataMapper
                .orderCreatedEventToPaymentRequestModel(domainEvent);

        // El callback se maneja automáticamente con el helper genérico
        kafkaProducer.send(
            orderServiceConfigData.getPaymentRequestTopicName(),
            orderId,
            paymentRequestModel,
            kafkaMessageHelper  // Helper genérico reutilizable
        );
    }
}
```

## Configuración

### 1. Inyectar el Helper Genérico
```java
@Autowired
private KafkaMessageHelper kafkaMessageHelper;
```

### 2. Usar en el Envío
```java
kafkaProducer.send(topicName, key, message, kafkaMessageHelper);
```

### 3. El Helper se Aplica Automáticamente
- Logging automático de éxito/fallo
- Metadata completa del mensaje
- Manejo consistente en toda la aplicación

## Consideraciones

1. **Thread Safety**: Los callbacks se ejecutan en hilos diferentes
2. **Exception Handling**: Siempre manejar excepciones en los callbacks
3. **Logging**: Usar logging apropiado para debugging
4. **Métricas**: Considerar agregar métricas de performance
5. **Reintentos**: Implementar estrategias de reintento si es necesario
