### Requirements

Create a git repository (either local or public one on GitHub) that
contains a RESTful web-service written in Scala. The service should
allow users to place new car adverts and view, modify and delete
existing car adverts.

Car adverts should have the following fields:
 * **id** (_required_): **int** or **guid**, choose whatever is more convenient for you;
 * **title** (_required_): **string**, e.g. _"Audi A4 Avant"_;
 * **fuel** (_required_): gasoline or diesel, use some type which could be extended in the future by adding additional fuel types;
 * **price** (_required_): **integer**;
 * **new** (_required_): **boolean**, indicates if car is new or used;
 * **mileage** (_only for used cars_): **integer**;
 * **first registration** (_only for used cars_): **date** without time.

Service should:

 * have functionality to return list of all car adverts;
 * optional sorting by any field specified by query parameter, default sorting - by **id**;
 * have functionality to return data for single car advert by id;
 * have functionality to add car advert;
 * have functionality to modify car advert by id;
 * have functionality to delete car advert by id;
 * have validation (see required fields and fields only for used cars);
 * accept and return data in JSON format, use standard JSON date 
 format for the **first registration** field. 

### Additional requirements

 * Service should be able to handle CORS requests from any domain.
 * Think about test pyramid and write unit-, integration- and acceptance-tests if needed.
 * It's not necessary to document your code, but having a readme.md 
 for developers who will potentially use your service would be great.

### Tips, hints & insights
 * If you decide to use Play!, try to use version 2.4 and do not use **GlobalSettings** 
 or any globals like **Play.configuration** or **Play.application**, use dependency injection instead.
