FROM selenium/standalone-chrome

USER root

RUN mkdir -p /usr/local/app
WORKDIR /usr/local/app

RUN apt-get update
RUN apt-get install python3-pip -y

RUN pip3 install flask
RUN pip3 install selenium==3.8.0
