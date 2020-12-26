# Arquitecturas orientadas a eventos: de las notificaciones al Event Sourcing

## reservas-teatro

Ejemplo de aplicación orientada a eventos en el dominio de las reservas de teatro.
 
Contiene:

* Un caso de uso de notificación cuando se concede un descuento.  
  Clase `com.autentia.tutoriales.reservas.teatro.DescuentoService` en el módulo `reservas-teatro-notificacion`.  
  Ver el [test](https://github.com/dav-garcia/reservas-teatro/blob/main/reservas-teatro-notification/src/test/java/com/autentia/tutoriales/reservas/teatro/DescuentoServiceTest.java).

* Un caso de uso de ECST cuando se seleccionan butacas para una reserva.  
  Clase `com.autentia.tutoriales.reservas.teatro.command.RepresentacionCommandService` en el módulo `reservas-teatro-ecst`.  
  Ver el [test](https://github.com/dav-garcia/reservas-teatro/blob/main/reservas-teatro-ecst/src/test/java/com/autentia/tutoriales/reservas/teatro/command/RepresentacionCommandServiceTest.java).

* Una implementación completa de toda la lógica de negocio con Event Sourcing y CQRS.  
  Todo esto está en el módulo `reservas-teatro-es`.  
  Hay un [test](https://github.com/dav-garcia/reservas-teatro/blob/main/reservas-teatro-es/src/test/java/com/autentia/tutoriales/reservas/teatro/ReservasTeatroTest.java) de varias partes del proceso.

No incluye adaptador HTTP REST para comandos y consultas.

Los adaptadores de event journal, persistencia de entidades y pasarela de pagos son "fakes" en memoria.  
Están en el módulo `reservas-teatro-infra`.

En ese mismo módulo hay un task scheduler (también en memoria) y dos implementaciones de command dispatcher:
un single-writer usando `synchronized` de Java y otro que implementa OCC (Optimistic Concurrency Control).

También en ese módulo están las interfaces básicas de DDD, CQRS y ES: `AggregateRoot`, `Entity`, `Command` y `Event`.

### Paquetes en Event Sourcing

![Paquetes](/Paquetes.png)

1. Comandos y modelo de escritura, agrupados por agregada.  
   Cada agregada es un paquete.
2. Interior de la agregada *Cliente*: clases de entidad, clases de comando,
   clase con el consumidor propio de la agregada y una clase de contexto de apoyo.
3. Como los eventos son elementos de primer nivel en Event Sourcing,
   se han extraído a su propio paquete `event` y están agrupados por agregada.  
   Aquí se ven los de *Cliente*.
4. Modelo de lectura de ejemplo: un histórico de las reservas hechas por cada cliente.
5. Saga del proceso de reserva.  
   Contiene consumidores de eventos y el `EstadoProceso` persistente.

## Enlaces

* [Charla](https://youtu.be/gX0DUO171jc)
* [Diapositivas](https://www.youtube.com/redirect?q=https%3A%2F%2Fspeakerdeck.com%2Fdav_garcia%2Farquitecturas-orientadas-a-eventos-de-las-notificaciones-al-event-sourcing&v=gX0DUO171jc&event=video_description&redir_token=QUFFLUhqbTBCRDU2eHo4X2E5Sm92OWFIVVZFd2tpZmNsZ3xBQ3Jtc0tuUms0TDR3VHdTdm0zb1hnYmhYbXlQZGZrTHgydmRheU1kSFViVzFRaFQtRnJXUmVXXzZmdW9DbUpSeDhtcmk0TGd6dVRxSUVYN0Q3eGtOV05GSE4wcUphQjk2Y1FZX0N3SDJ4WUk1cnlKYkowR1Nkcw%3D%3D)
* [Comparativa de sistemas de mensajería](https://softwaremill.com/mqperf/)
