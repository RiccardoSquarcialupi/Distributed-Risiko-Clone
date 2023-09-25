FROM alpine:3.16

# Copy the all lobby folder
# Copy the "lobby" folder from your host machine into the container
RUN mkdir /lobby
COPY client/lobby /lobby

# set workdir to lobby
WORKDIR /lobby

RUN apk add openjdk11
RUN apk add gradle
RUN apk add xvfb x11vnc

RUN x11vnc -create -forever

RUN gradle build --no-daemon -x test

CMD gradle run -x test --warning-mode all