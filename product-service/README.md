[Booking-Service-Api] 
- Rest Call 

[Booking-Service] 
- persist BookingCreatedEvent event store 
- publish topic (booking.created) 

[ProductService] 
- listen topic (booking.created) 
- find By ProductId Order By Aggregate Version Asc 
- aggregate rehydrate 
- check product availability 
- persist ProductReservedEvent event store 
- publish topic (product.reserved) 

[PaymentService] 
- listen topic (product.reserved) 
- persist PaymentInitiatedEvent event store 
- process payment 
- persist PaymentCompletedEvent event store 
- publish topic (payment.completed) 

[BookingService] 
- listen topic (payment.completed) 
- persist BookingPaymentConfirmedEvent event store 
- publish topic (booking.completed)



## TODO:
- unit and integration test product-service
- add SAGA
- add generic utilities into custom starter
- update documentation
- add product-service readme
- implement read_model / projections