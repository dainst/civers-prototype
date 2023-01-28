# Data Citation Service Prototype

TODO add high level description
See [documentation](./docs/README.md)

## Prerequisites 

- Docker
- docker-compose

## Getting started

    $ docker-compose up

This starts multiple services, three of which have addresses 
one can visit in the browser:

- http://localhost:8020 # The Citator
- http://localhost:8021 # The DOI Registrar
- http://localhost:8022 # The Widget Host

See [documentation](./docs/README.md)

## Clean up

    $ ./clean.sh (Mac and Linux; TODO write instructions for windows)

## Development notes

    Visit http://localhost:8020
    Check site renders. Check console (msg: hi).

This gives you hot code reload for frontend and backend. TODO review

### Working with the REPL

- Uncomment one and comment the other entrypoint in docker-compose.yml, for a given service
- `docker-compose up`
- Connect to the given REPL port from from you editor 

then do

```clojure
clj:user:> (start)
{:started ["#'resources/resources" "#'server/http-server"]}
```

### Rebuilding containers

- TODO describe when necessary
- mention docker system prune

## Notes

TODO review what of these notes would be interesting to keep

Run

    $ docker-compose -f docker-compose.yaml run --service-ports --entrypoint "/bin/bash" citator

Inside both of the containers

    /usr/local/app# clj -M -m server

They expose ports 3005 and 3006 respectively.

Rebuild the container after a change of a deps.edn

    $ docker-compose -f docker-compose.yaml build data-citation-service

curl -XPOST -d '{"msg":"hi"}' -H 'Content-Type: application/json' http://172.17.0.1:3005/api
