FROM clojure:tools-deps
RUN mkdir -p /usr/local/app
WORKDIR /usr/local/app

COPY deps.edn /usr/local/app/deps.edn
RUN clj -P             
RUN clj -A:nREPL -P              

