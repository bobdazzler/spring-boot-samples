# Spring Boot Reactive Product Microservice

Spring Boot sample for Reactive Webflux and Reactive Mongo

### Server Port 8081

#### Create Product
> curl --header "Content-Type: application/json" --request POST --data '{"id": "iphone", "company": "apple"}' http://localhost:8081/products

#### Read Product
> curl http://localhost:8081/products/iphone
