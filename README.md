# MARKET APPLICATION

## Preview Of Project's Work Principle 

![Untitled-2023-09-09-1701](https://github.com/berkaykopuz/market/assets/103936811/0fb0b162-b177-49af-be24-12f7872a16f6)

This repository contains a demo project showcasing a microservices-based application. The project consists of services: "Gateway, Discovery Server, Registration, User-Management, Product, Report, Selling".

## Getting Started

Follow the instructions below for set up the project and begin to use on your local host machine.

### Pre Requirements
- at least JDK 17
- Maven
- Docker (optional to use containers)

### Installation
  1. Clone the repository.
  2. Open the project directory with Intellij IDEA compiler.
  3. Build with maven.

## Project Components (Microservices)

### Discovery Server
Discovery Server is enabling service-to-service communication within the microservices ecosystem.

### Gateway
The API Gateway serves as the single entry point for all client requests, managing and routing them to the microservices.

### User-Management
Creating users, roles, update and delete operations for any user. 

### Registration 
It is responsible for the provide a token for the session of users.

### Product
PUBLIC Users access to view products available on database.

### Selling
It can make and return sales for the given products. CRUD operations for campaigns. CASHIER tag users can access.

### Report
It creates bills for given sales. Could be used for search operations. Sorting, filtering and pagination are available. MANAGER tag users can access.

## How To Use It
You can access an api link to communicate project with using postman. Examples are given below:
### API LINKS
- localhost:8088/sales/createbill?billId=
- localhost:8088/sales?pageNo=0&pageSize=2&field=saleDate&cashierName=ozgur
- localhost:8088/selling/delete/e9d4ea67-2de3-4205-860b-6f2f1ac78201
- localhost:8088/selling/makesale
- localhost:8088/campaign
- localhost:8088/campaign/create
- localhost:8088/campaign/delete
- localhost:8088/product-modify/create
- localhost:8088/product-modify/update
- localhost:8088/product-modify/delete
- localhost:8088/product
- localhost:8088/users
- localhost:8088/users/register?rolename=
- localhost:8088/users/update
- localhost:8088/users/delete
- localhost:8088/auth/token
![image](https://github.com/berkaykopuz/market/assets/103936811/703d60ba-fc1f-4c42-8a1c-bbba74f841b8)
![image](https://github.com/berkaykopuz/market/assets/103936811/168d6626-86ff-44fc-8dc8-68327263ce82)
![image](https://github.com/berkaykopuz/market/assets/103936811/dd1ec2b4-fd61-4813-91b8-3b90976a755c)










