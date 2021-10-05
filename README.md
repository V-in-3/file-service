# File service

## Description
File service has endpoints for working with ES 

## Available endpoints

- `POST http://localhost:8080/file` - upload file
- `DELETE http://localhost:8080/file/{id}` - delete file
- `POST http://localhost:8080/file/{id}/tags` - assign tags to file
- `DELETE http://localhost:8080/file/{id}/tags` - remove tags for file
- `GET http://localhost:8080/file/all` - get all files
- `GET http://localhost:8080/file?tags=tag1&page=1&size=5` - get files by filter ... 

Request samples can be found [here](src/test/http/api-test.http).

## Configuration

Application parameters can be configured with environment variables

## Environment variables
|name                           |default value  |description    |
|-------------------------------|---------------|---------------|
|APPLICATION_ELASTICSEARCH_HOST | localhost     | Host for ES   |
|APPLICATION_ELASTICSEARCH_PORT |  9200         | Port for ES   |

## Test project
```
mvn test
```
## Build project
```
mvn clean package -DskipTests
```

## Run project
```
docker-compose up -d
```