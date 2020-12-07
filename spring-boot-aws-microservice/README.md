# spring-boot-aws-microservice
AWS ready Spring Boot Microservice

### IAM Credentials
Spring boot has the capability to load credentials from the underlying EC2.

But EC2 roles based microservice has the following disadvantages
1. No clear separation of permissions between different microservices running in same EC2 instance
2. Microservice specific audit trail is not possible

So for each microservice create an IAM user with the service name and narrow down the permission.
Enable programmatic access to get access key and secret key for configuring Spring boot.

Best practice is to automate the microservice stack using cloudformation even if it is just IAM that the service needs.
Access Key can be created manually from the console to simplify things instead of using encryption or secrets.

Pass the below properties to start this microservice locally.

```properties
cloud.aws.credentials.accessKey=ABCD0123456789
cloud.aws.credentials.secretKey=127812HAHJS2727272
cloud.aws.region.static=ap-south-1
```
For each environment run the stack, create the access key and configure the above properties in the docker environment.

### Multi Region setup

When running the microservice in multiple regions we can simply set the `cloud.aws.region.static` property.

Including EC2 based lookup for region means we have to declare some beans to make the application work for 
local environment. Which in my humble opinion is unnecessary.