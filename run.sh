cd inventory-service
mvn clean install -DskipTests
docker build -t ufukhalis/inventory-service .
cd ..

cd order-service
mvn clean install -DskipTests
docker build -t ufukhalis/order-service .
cd ..

cd payment-service
mvn clean install -DskipTests
docker build -t ufukhalis/payment-service .
cd ..

docker-compose up
