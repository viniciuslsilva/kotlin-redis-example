To set up the docker environment executing the following command:

```
$ docker-compose -f docker/docker-compose.yaml up --build
```

Wait until all services to be ready and then you can run all tests (including integration tests) by executing this command:

```
$ mvn clean verify
```

To generate .jar file and starting the server executing the following command:

```
$ mvn package && java -jar target/redis-example-1.0.0-SNAPSHOT.jar
```