## Simple Ping Application

This app checks connectivity and traces routes to a list of configured hosts. The list of hosts can be configured by
editing the **hosts.txt** file.

The properties such as delays between checks, URL of external reporting server, etc. can be set up with **app.properties** file.

To avoid error logs with the default configuration run any HTTP server on port 8080 and ensure it accepts POST requests.

### Prerequisites:

- JDK 17
- Docker (optional)

### Usage:

1. Build

Windows:

```shell
    mvnw.cmd clean package
```

Linux:

```shell
    ./mvnw clean package
```

2. Run

Run jar:

```shell
    cd /target
    java -jar simple-ping-application-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Run with Docker:

```shell
    docker build -t simple-ping-app . 
    docker run simple-ping-app
```