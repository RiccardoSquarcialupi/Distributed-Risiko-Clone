FROM ubuntu:latest

USER root

RUN sed -i "s/archive.ubuntu.com/it.archive.ubuntu.com/" /etc/apt/sources.list

RUN \
    apt -o Acquire::http::Pipeline-Depth=0 -o Acquire::ForceIPv4=true update && \
    apt install -y openjdk-11-jdk dos2unix x11-apps sudo

#Credits to https://www.baeldung.com/linux/docker-container-gui-applications Chapter 4
RUN     export uid=1000 gid=1000
RUN     mkdir -p /home/docker_user
RUN     echo "docker_user:x:${uid}:${gid}:docker_user,,,:/home/docker_user:/bin/bash" >> /etc/passwd
RUN     echo "docker_user:x:${uid}:" >> /etc/group
RUN     echo "docker_user ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/docker_user
RUN     chmod 0440 /etc/sudoers.d/docker_user
RUN     chown ${uid}:${gid} -R /home/docker_user
USER docker_user
ENV HOME /home/docker_user

# Copy the all lobby folder
# Copy the "lobby" folder from your host machine into the container
RUN mkdir /lobby
COPY client/lobby /lobby

# set workdir to lobby
WORKDIR /lobby

# Set the display to the host machine
RUN chmod 777 gradlew
RUN dos2unix gradlew
RUN ./gradlew build -x test --warning-mode all
