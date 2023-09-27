FROM ubuntu:latest

USER root

RUN \
    apt update && \
    apt install -y openjdk-11-jdk xterm nano

# Copy the all lobby folder
# Copy the "lobby" folder from your host machine into the container
RUN mkdir /lobby
COPY client/lobby /lobby

# set workdir to lobby
WORKDIR /lobby

CMD gradlew shadowJar

# Set the display to the host machine
ENV DISPLAY=host.docker.internal:0.0

CMD java -jar build/libs/lobby-1.0-SNAPSHOT-all.jar
