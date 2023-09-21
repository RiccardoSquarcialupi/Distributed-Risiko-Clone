FROM jlesage/baseimage-gui:alpine-3.15-v4

# Copy the all lobby folder
# Copy the "lobby" folder from your host machine into the container
COPY client/lobby /lobby

COPY startapp.sh /startapp.sh

# set workdir to lobby
WORKDIR /lobby

RUN apk add openjdk11
RUN apk add gradle
RUN gradle build --no-daemon -x test