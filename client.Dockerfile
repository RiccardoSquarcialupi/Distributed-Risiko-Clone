FROM gradle:4.7.0-jdk8-alpine
WORKDIR .
COPY client/lobby .
RUN gradle build
CMD ["gradle", "run"]