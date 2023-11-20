FROM ubuntu:latest

USER root

RUN sed -i "s/archive.ubuntu.com/it.archive.ubuntu.com/" /etc/apt/sources.list

RUN \
    apt -o Acquire::http::Pipeline-Depth=0 -o Acquire::ForceIPv4=true update && \
    apt install -y openjdk-11-jdk xterm nano dos2unix

# Copy the all lobby folder
# Copy the "lobby" folder from your host machine into the container
RUN mkdir /lobby
COPY client/lobby /lobby

# set workdir to lobby
WORKDIR /lobby

# Set the display to the host machine
ENV DISPLAY=host.docker.internal:0.0
RUN dos2unix gradlew
#CMD java -jar build/libs/lobby-1.0-SNAPSHOT-all.jar
