FROM edgelevel/alpine-xfce-vnc

USER root

# Copy the all lobby folder
# Copy the "lobby" folder from your host machine into the container
RUN mkdir /lobby
COPY client/lobby /lobby

# set workdir to lobby
WORKDIR /lobby

RUN apk add openjdk11
RUN apk add gradle
RUN apk add xterm

RUN gradle build --no-daemon -x test

RUN DISPLAY=:0.0 gradle run -x test --warning-mode all &
