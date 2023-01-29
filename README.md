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

If you encounter in either *Citator* or *DOI Registrar* a red message bar at the bottom of the screen which 
informs about `shadow-cljs Stale Output!`, wait a few seconds and refrsh the page. Also wait a few seconds and refresh
if *Widget Host* does not show the widget yet. Make sure everything is fine before you proceed.

The two primary use cases are documented [here](./docs/README.md).

See also [technical documentation](./docs/README_TECHNICAL.md).

TODO websockets, only one tab of `8020` should be open

### Clean up

To start the test system from scratch again, 
one simply removes some files and folders.

#### Mac and Linux

Execute the following script:

    $ ./clean.sh

#### Windows

Shut down `docker-compose` (if it runs) and
delete all files under `archive`, except `.keep`. 

Delete the directories `citator-data`
and `doi-registrar-data`.

TODO verify if you need some special permissions here.

## Development notes

The *Citator* (port `8020`) and the *DOI Registrar* (port `8021`) 
provide hot code reload via `shadow-cljs`.

### Working with the REPL

- Uncomment one and comment the other entrypoint in docker-compose.yml, for a given service
- `docker-compose up`
- Connect to the given REPL port from from you editor 

then do

```clojure
clj:user:> (start)
{:started ["#'resources/resources" "#'server/http-server"]}
```
