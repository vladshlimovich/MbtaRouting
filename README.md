# newApp project (MBTA Routing)
This is NOT a production grade application, rather a working PROTOTYPE.
The areas that need to be taken care off:

1. ~~Unit tests~~ 
2. ~~Integration test (via maven fail-safe plugin)~~ 
3. Comprehensive logging
4. Comprehensive exception handling
5. ~~MBTA-side caching (via response/request headers)~~
6. Application-level caching
7. Smart handling of GreenLine-like situations

## Intellij IDEA
From the IntelliJ terminal window, copy preconfigured run configurations into the .idea folder:
``````
  Xcopy /E /I .idea-preconfig .idea
  run NEWAPP
``````

## Build
```bash
./mvnw clean install
````


## Start
```bash
cd target
./startup.sh
````
## Stop
```bash
cd target
./shutdown.sh
````

## Docker
```bash
# Build:
docker image rm newapp --force
docker build . -f ./Dockerfile -t newapp

# Start:
docker run \
 --name newapp \
 --rm \
 -p 1984:1984 \
 newapp \
 &

# Stop:
docker stop newapp

```

## Use
```bash
chttp://localhost:1984/routes_with_stops.html
````

## Logs
```bash
By default the logs can be found in C:/local/logs
One can set LOG_DIR environment variable to alter the location
````
