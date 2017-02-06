 Adverts
=========

Card adrverts publishing REST web service. 

Has an interface to store, delete, update individual advert and retrive the sorted list of adverts in specified order.

Web service consist of following layers:
  
Web Server layer
----------------

The web server handlers and HTTP request routing/parsing/validatiion.
Provided by Play framework router and controllers. 
Here the user input converts to service model data vice versa.

Basic usage: 

-H "Content-Type: application/json"

    curl -i -X GET localhost:9000/adverts
    curl -i -X GET localhost:9000/adverts/101
    curl -i -X PUT  -d '{,...}' localhost:9000/adverts/a1
    curl -i -X POST -d '{id:"a2"}' localhost:9000/adverts
    curl -i -X DELETE localhost:9000/adverts/a1

Adverts requests should have `application/json` content type and have one of the format listed below:

New car advert:

```json
{"id":"e8a2dbd206b94b92b7f2ec385d7448cf","title":"Audi A4 Avant","fuel":"Gas","price":20456,"new":true} 
```

Used car advert:
```json
{"id":"97a9268746e149a7a47d0d88e68f0e07","title":"VW Touareg","fuel":"Diesel","price":60000,"new":false,"mileage":62560,"reg":"2015-11-11"}
```

Service layer
-------------

Since there is no such thing as sorted hash-table the service level provides sorting capabily for retrieving the list of adverts.
Sort field name and order should be provided as query parameters.

  > curl -i -X GET localhost:9000/adverts?sort=title&ord=asc

Also provide the storage handlers for specific model data types. Handlers are responsible for converting service level type into the format DBA applicaiton will understand. 
Cars, adverts, books, ToDos everything is possible to store as soon as you implement the handler.


Storage layer
-------------

Provides the API for storage providers that can interchange with the service.
Specifies the model each storage provider should implement:

  * Container - collection of entries
  * Entry     - collection of attributes
  * Attribute - basic (key,value)

Configuring the specific storage option, primary keys, table/index names, distribution and sharding strategies will be possible here some day :)

Storage engine
--------------
The only one storage engine exist for this moment.

Simple wrapper of AWS-SDK dynamodb client. Implements Dynamodb storage model required interface and provides mapping:
      
    Table     <-> Container
    Entry     <-> Item
    Attribute <-> Attribute

Since its based on Amazon WS libraries the ~/.aws/credentials should be configured.
DynamoDb database instance and `endpoint` configured at `application.conf` `aws` section.


Prerequisites
-------------
[tar.gz](http://dynamodb-local.s3-website-us-west-2.amazonaws.com/dynamodb_local_latest.tar.gz)

