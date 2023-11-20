cd client/lobby
call gradlew.bat build
cd ..
cd ..
docker-compose -f test-compose.yaml up --build