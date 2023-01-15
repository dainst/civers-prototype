FROM clojure:tools-deps
RUN mkdir -p /usr/local/app
RUN mkdir -p /usr/local/app/resources/public
WORKDIR /usr/local/app

COPY src /usr/local/app/src
COPY package.json /usr/local/app/package.json
COPY shadow-cljs.edn /usr/local/app/shadow-cljs.edn

RUN apt-get update
RUN apt-get install npm -y
RUN npm i

RUN npx shadow-cljs compile app
