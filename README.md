# Data Citation Service Prototype

A system designed to take snapshots of websites and
generate [DOI](https://en.wikipedia.org/wiki/Digital_object_identifier)s,
aimed at providing permanently citable web resources, which are otherwise
prone to link-rot.
 
The prototype demonstrates the interaction of multiple services, which under
realistic circumstances would be hosted independently at different places.

The main component - the system to be designed - is the **Citator**, the component
generating the DOIs and capable of taking snapshots of 
and providing access to the archived websites.

The **DOI Registrar** emulates a central repository where DOIs from different sources
can be registered and searched. From here one can navigate to the sites registered by the *Citator*.

Although the Citator is standalone, that is, one can enter URLs to archive via its user interface,
there exists an embeddable **Widget**, which can be shown on external websites. It provides a 
button which triggers the archival of the page it is embedded in, via the *Citator*. In the 
context of the prototype an exemplary external system is represented by the **Widget Host**.

## Prerequisites 

- Docker
- docker-compose

Setup tested under `Ubuntu`. The user interfaces are tested with `Chromium`.

## Getting started

    $ docker-compose up

This starts multiple services, three of which have addresses 
one can visit in the browser:

- http://localhost:8020 # The Citator
- http://localhost:8021 # The DOI Registrar
- http://localhost:8022 # The Widget Host

The two primary use cases are documented [here](./docs/README.md).

See [technical documentation](./docs/README_TECHNICAL.md)

TODO websockets, only one tab of 8020 should be open

### Clean up

How to remove existing data from the test system.

#### Mac and Linux

Execute the following script:

    $ ./clean.sh

#### Windows

shut down `docker-compose` (if it runs) and
delete all files under `archive`, except `.keep`. Delete the directories `citator-data`
and `doi-registrar-data`.

TODO verify if you need some special permissions here.

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
