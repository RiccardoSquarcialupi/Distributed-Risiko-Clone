FROM eclipse-temurin:17-jdk-jammy
WORKDIR .
COPY client/lobby .
RUN gradle build
CMD ["gradle", "run"]