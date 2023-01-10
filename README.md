# data citation service prototype

Run

    $ docker-compose -f docker-compose.yaml run --service-ports --entrypoint "/bin/bash" data-citation-service
    $ docker-compose -f docker-compose.yaml run --service-ports --entrypoint "/bin/bash" doi-service

Inside both of the containers

    /usr/local/app# clj -M -m server

They expose ports 3005 and 3006 respectively.

Rebuild the container after a change of a deps.edn

    $ docker-compose -f docker-compose.yaml build data-citation-service

curl -XPOST -d '{"msg":"hi"}' -H 'Content-Type: application/json' http://172.17.0.1:3005/api
