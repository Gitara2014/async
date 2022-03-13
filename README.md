# async
Asynchronous Threading to Improve Spring Boot Application Performance


### RUN 
```shell
mvn clean package
java -jar target/*.jar
```

### TEST 1 - Tomcat thread and Callback thread.
- Synchronous call - single thread
    ```shell
    curl "http://localhost:8000/user-sync?id=22"
    ```

- Asynchronous call - 3 threads
    ```shell
    curl http://localhost:8000/user-async?id=44
    ```

### TEST 2 - Use FutureTask and Callable with Multithreading

- Fetch each user information one by one
    ```
    curl "http://localhost:8000/user-info-sequential?userId=22"
    Result: getUserInfoSequential took: 4501ms 
    ```

- Fetch each user information in parallel: three FutureTasks with the Callable. Then pass the FutureTask to Thread classes for multithreading
    ```
    curl "http://localhost:8000/user-info-parallel?userId=pera"
    Result: getUserInfoParallel took: 2004ms
    ```


#### Console : 
Set proper DEBUG log Level for springframework to troubleshoot thread execution:

```yaml
# https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.logging
    logging:
    
      level:
        root: info
        org:
          springframework:
            web: debug
    
```

#### REFERENCE 

- https://medium.com/geekculture/asynchronous-threading-to-improve-spring-boot-application-performance-a3adaea097d4
- https://medium.com/geekculture/use-futuretask-and-callable-with-multithreading-to-boost-your-java-application-performance-47a8fc6cf8a5
- https://stackoverflow.com/questions/3382954/measure-execution-time-for-a-java-method


#### TROUBLESHOOT
- https://stackoverflow.com/questions/44143979/retrieving-http-response-without-blocking-the-main-thread
- https://stackoverflow.co