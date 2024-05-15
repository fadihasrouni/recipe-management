# Recipe Management Application

This is a recipe management application built with Spring Boot 3 and Java 17. It allows users to create, update, delete, and retrieve recipes based on certain. The application provides a RESTful API for managing recipes and utilizes a MySql database for storage (inside a docker container).

The Spring Boot project follows a layered architecture in which there are 3 main layers: Presentation, Service(Business), Persistence Layer. This is followed by a database that stores and performs database operations. Below are some of what was implemented in the project

- All exceptions are handled in a custom exception handler.
- Swagger is used for API resource documentation. Open API specifications are used to describe and generate the needed REST operations.
- MySQL is used to store all the data. A Docker container is used to run the MySQL database instance.
- The project includes unit tests and integration tests
- Java 17 is being used.

## Features

- **Create Recipe**: Users can create new recipes with details such as name, description, ingredients, and instructions.
- **Update Recipe**: Existing recipes can be updated with new information and recipe ingredients.
- **Delete Recipe**: Recipes can be deleted.
- **Retrieve Recipe**: Users can retrieve recipes by Id.
- **Retrieve a list of Recipes**: Users can retrieve recipes by a list of criteria
  - **numberOfServings**: How many persons it serves
  - **dietaryType**: Different dietary type like VEGAN, VEGETARIAN, FLEXITARIAN...
  - **instructionsInclude**: The instruction include a certain keyword
  - **ingredientIncludes**: Includes a specific ingredient in the list of ingredients
  - **ingredientExcludes**: Exclude a specific ingredient in the list of ingredients

## Prerequisites

Before running the application, ensure you have the following installed:

- Java 17
- Maven
- IDE (IntelliJ IDEA, Eclipse, etc.)

## Getting Started

The project consists of the API and a MYSQL database. To be able to run them both locally please follow the steps below.

1. Clone the repository:

    ```bash
    git clone https://github.com/fadihasrouni/recipe-management.git
    ```

2. Startup docker for the database:

    ```bash
    docker-compose up
    ```
   Once the docker container is up we can proceed with the application.

3. Build the project using Maven:

    ```bash
    mvn clean install
    ```

4. Startup the application:

    ```bash
    mvn spring-boot:run
    ```
   You can also run tests:
    ```bash
    mvn test
    ```   

5. Access the API documentation:

   Open your web browser and go to: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
   
## API Documentation

The API is documented using the OpenAPI Specification. You can access the API documentation in Swagger UI by visiting  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) after starting the application, it has all the details needed to get started with a sample request.


### Creating a recioe example payload:
You can also use this payload to update the recipe

```json
{
  "name": "Mushroom chicken",
  "description": "This homemade mushroom chicken that is so tasty and deserves a try!",
  "dietaryType": "NON_VEGETARIAN",
  "numberOfServings": 2,
  "prepTimeMinutes": 20,
  "ingredients": [
    {
      "ingredientId": 5,
      "unitId": 6,
      "quantity": 1
    },
    {
      "ingredientId": 16,
      "unitId": 5,
      "quantity": 100
    },
    {
      "ingredientId": 13,
      "unitId": 2,
      "quantity": 1
    }
  ],
  "instructions": "1- put oil in a pan 2- saute the mushrooms 3- add chicken and cook for 15 mins"
}
```

