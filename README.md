## Sast Restful

Sast Restful endpoint is a solution to the coding test sent by Sast

## Technologies
##### Spring Boot:
Spring Boot was chosen because of the ease of use and the ease of
bootstrapping an application with minimal effort.

##### Postgres:
Postgresql was chosen because it is the most advanced, SQL-compliant
and open-source objective-RDBMS since it tries to adopt the ANSI/ISO SQL
standards together with the revisions.

Also it's support for concurrency is achieved without read locks thanks
to the implementation of Multiversion Concurrency Control (MVCC),
which also ensures the ACID compliance.

It is highly programmable, and therefore extendable.

## Development

To start your application in the dev profile, simply run:

    ./gradlew


> Don't forget to set the following env vars:
> * SPRING_DATASOURCE_URL
> * SPRING_DATASOURCE_USERNAME (Optional based on postgres setup)
> * SPRING_DATASOURCE_PASSWORD (Optional based on postgres setup)

## Building for production

To optimize the restful application for production, run:

    ./gradlew -Pprod clean bootWar

To ensure everything worked, run:

    java -jar build/libs/*.war

## Using Docker for publishing the code
You can also fully dockerize the application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./gradlew publish

Then run:
    docker-compose -f docker/app.yml up -d

or simply run
    ./publish.sh

## Accessing the app
You can access the app using any of the JSON client using the url `/v1`

### CRUD for employees
Both employees and candidates has the same properties:
* first name
* last name
* email

All CRUD operations for the employees managed under `employees resource`

|   Method      |            URI          |       Description     | 
| ------------- |:----------------------- |:---------------------:|
|GET            | /v1/employees           | list all employees    |
|POST           | /v1/employees/{id}      | create employee       |
|PUT            | /v1/employees/{id}      | update employee       |
|DELETE         | /v1/employees/{id}      | delete employee       |
|GET            | /v1/employees/{id}      | show employee         |

> The same goes for Candidates under the resource `candidates`

### Submit availability
Expected availability model is as follows:

* day: Day of the next week to submit the availability Available Values are `SATURDAY,SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY`
* person_id: either a candidate or an employee based on the URI provided
* times: Array of available hours in that day with two propertied:
    * from: integer value representation of hours and minutes e.g 1500 means 15:00
    * to : integer value representation of hours and minutes
    
Submitting availability would be done under `/v1/<resource>/submit-availability` with a `POST` request for the following resources:
* employees
* candidates


### Interview Matching
Getting available times for interview is done by sending a `GET` request to the URI `/v1/candidates/{id}/possible-interview-times?interviewers={id1}&&interviewer={id2}..`
it will return employees submitted time slots that matches the available time slots for this candidate.

## Final Notes on development:
In this section I'll share some of the questions and points that I had in mind but due to
the time limit I had to make a lot of assumptions and cut back the test to the barebones
of the requirements:

* The Interview matching can be done using the Knapsack strategy trying to fit the interviewer time slots to employees free time slots
* The model needs a some of alteration to work properly since it assumes that employees and 
  candidates only submit availability of the next week.
* `/v1/<resource>/submit-availability` URI can be altered to be `/v1/<resource>/{id}/submit-availability` and in that 
  case we wont need the `person_id` in the request body. 
* Validation should be done in the availability submission so users can't submit overlapping availability time slots
* Upon the creation of the interview the time slot should be split in two different time slots with one marked as 
  occupied and the remaining time slot should be marked as free.   
* Do we need to have authentication and authorization over the api calls and over the data?
* Do we want to use Oauth Mechanism or JWT if we decided to implement authentication?
* What is the API versioning strategy? using sub-domains or include the api version in the URI?
* what are the content types that are accepted to be served by the app
* Can we use ETag for caching?
* what kind of protocols our app supports? 