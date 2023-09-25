FROM edgelevel/alpine-xfce-vnc

# Copy the all lobby folder
# Copy the "lobby" folder from your host machine into the container
RUN mkdir /lobby
COPY client/lobby /lobby

# set workdir to lobby
WORKDIR /lobby

RUN apk add openjdk11
RUN apk add gradle
RUN apk add xterm
RUN apk add xhost

RUN x11vnc -create -reopen -forever &
RUN gradle build --no-daemon -x test
RUN xhost +

CMD xterm
#CMD DISPLAY=:0.0 gradle run --args="-Djava.awt.headless=true" -x test --warning-mode all