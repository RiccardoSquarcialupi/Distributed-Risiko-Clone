FROM gradle:7.1.1-jdk11-openj9

# Copy the all lobby folder
# Copy the "lobby" folder from your host machine into the container
COPY client/lobby /lobby

# set workdir to lobby
WORKDIR /lobby

# Build the project with Gradle and resolve Maven dependencies withour running tests
RUN gradle build --no-daemon -x test


# Specify the command to run your Gradle-based application
CMD ["gradle", "run","-x","test","--warning-mode", "all"]