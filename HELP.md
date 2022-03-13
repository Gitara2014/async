### Asynch
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


- https://medium.com/geekculture/asynchronous-threading-to-improve-spring-boot-application-performance-a3adaea097d4
- https://medium.com/geekculture/use-futuretask-and-callable-with-multithreading-to-boost-your-java-application-performance-47a8fc6cf8a5
- https://stackoverflow.com/questions/3382954/measure-execution-time-for-a-java-method


### TROUBLESHOOT
- https://stackoverflow.com/questions/44143979/retrieving-http-response-without-blocking-the-main-thread
- https://stackoverflow.com/questions/2461819/when-does-the-call-method-get-called-in-a-java-executor-using-callable-objects