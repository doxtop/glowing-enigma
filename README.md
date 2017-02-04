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
    curl -i -X PUT  -d '{id:"a1"}' localhost:9000/adverts/a1
    curl -i -X POST -d '{id:"a2"}' localhost:9000/adverts
    curl -i -X DELETE localhost:9000/adverts/a1
    
Service layer
-------------

Since there is no such thing as sorted hash-table the service level provides sorting capabily for retrieving the list of adverts.
Sort field name and order should be provided as query parameters.

  > curl -i -X GET localhost:9000/adverts?sort=title&ord=asc


Storage layer
-------------

Support various storage providers.
Specifies the model each storage provider should implement:

  * Container - collection of entries
  * Entry     - collection of attributes
  * Attribute - basic (key,value)

Storage engine
--------------

Simple wrapper of AWS-SDK dynamodb client. Implements Dynamodb storage model required interface and provides mapping:
      
    Table     <-> Container
    Entry     <-> Item
    Attribute <-> Attribute

Since its based on Amazon WS libraries the ~/.aws/credentials should be configured.


Prerequisites
-------------
DynamoDb database instance and `endpoint` configured at `application.conf` `aws` section.

[tar.gz](http://dynamodb-local.s3-website-us-west-2.amazonaws.com/dynamodb_local_latest.tar.gz)

