# CiVers Prototype
 
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

Although the *Citator* works standalone, that is, one can enter URLs to archive via its user interface, 
it also provides a **Widget**, which external websites can embed. The widget provides a 
button which triggers the archival of the page it is embedded in, by sending a request for archival to the *Citator*. 
In the context of the prototype the **Widget Host** represents an exemplary external website.

## Prerequisites 

- `Docker`
- `docker-compose`

Under Mac and Windows this means just installing `Docker Desktop`, which includes both.

When running under Windows it is also recommended to make use of `GitBash`.

Setup tested under **Ubuntu** Linux, **Mac**, **Windows** (Docker Desktop with WSL-2). 
The user interfaces are tested with `Chromium`, `Chrome` and `Firefox`.

## Getting started

    $ docker-compose up

This starts multiple services, three of which have addresses 
one can visit in the browser:

- http://localhost:8020 # The Citator
- http://localhost:8021 # The DOI Registrar
- http://localhost:8022 # The Widget Host

The two primary use cases are documented [here](./docs/README.md).

Note that the generated artifacts, screenshots and html files of archived sites, can be found in the `archvive` folder
in the root directory of this project.

Consult the [technical documentation](./docs/README_TECHNICAL.md) for an architectural overview.

### Notes and Troubleshooting

### Websockets

Of the two sites at `http://localhost:8020/` and `http://localhost:8021` make sure to only keep one tab open for each of them.
This is because only the last opened tab will keep a websocket connection, which is used for automatic updates when resources change.
However, for the `8021` service this does not apply to subsites like `http://localhost:8021/<somePath>`. Here it does not matter how many tabs one opens.

### Red bar on the bottom of the screen

If you encounter in either *Citator* or *DOI Registrar* a red message bar at the bottom of the screen which 
informs about `shadow-cljs - Stale Output!` or `shadow-cljs - Reconnecting ...`, wait a few seconds and refrsh the page. Also wait a few seconds and refresh
if *Widget Host* does not show the widget yet. Make sure everything is fine before you proceed.

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

The *Citator* UI (port `8020`) and the *DOI Registrar* (port `8021`) UI
provide hot code reload via `shadow-cljs`. 

Also, hot code reload is provided for the backend code. The reload happens
on each http request against one of the routes configured in `defroutes`.

### Working with the REPL

- Uncomment one and comment the other entrypoint in docker-compose.yml, for a given service
- `docker-compose up`
- Connect to the given REPL port from from you editor 

then do

```clojure
clj:user:> (start)
{:started ["#'resources/resources" "#'server/http-server"]}
```
