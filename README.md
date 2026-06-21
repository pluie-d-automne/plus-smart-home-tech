# Учебный проект smart-home-tech

## Telemetry
### Collector
1) Принимает данные от хабов пользователей по протоколу gRPC в формате PROTOBUF.
2) Полученные данные кодирует в формат Avro и сохраняет в топики в Kafka:
    * telemetry.sensors.v1 - данные, связанные с показаниями датчиков событиями устройств.
    * telemetry.hubs.v1 - данные, связанные с хабами и сценариями.
   
### Aggregator
1) Читает события из топика telemetry.sensors.v1.
2) Все события агрегируются в снапшоты.
Каждый снапшот содержит текущие показания датчиков, зарегистрированных в конкретном хабе. 
3) Каждый раз, когда состояние снапшота меняется, отправляет его в топик telemetry.snapshots.v1.

### Analyzer
1) Читает данные по сценариям из топика telemetry.hubs.v1 и сохраняет их в postgres.
2) Снапшоты получает из топика telemetry.snapshots.v1 и не хранит их, а только сравнивает со сценариями из БД.
3) При нахождении совпадения снапшота со сценарием - отправляет все actions по gRPC в hub-router


### Serialization
Родительский Maven-модуль, объединяющий модули со схемами Avro и Protobuf.

## Commerce

### shopping-store - витрина товаров
Покупатели интернет-магазина будут получать из этого сервиса информацию о доступных товарах и на её основе
принимать решение о покупке.

Спецификации OpenApi для сервиса shopping-store в SwaggerUI:
```bash
docker run -p 9090:8080 -e API_URL=https://code.s3.yandex.net/Java/project21/shopping-store13012026.json swaggerapi/swagger-ui
```

### shopping-cart - корзина покупателя
Покупатели будут выбирать товары с витрины и добавлять их в корзину.
В дальнейшем на основе этой информации будет оформлен заказ.

Спецификации OpenApi для сервиса shopping-cart в SwaggerUI:
```bash
docker run -p 9090:8080 -e API_URL=https://code.s3.yandex.net/Java/project21/shopping-cart-16062025.json swaggerapi/swagger-ui
```

## Infra

### Config Server


### DiscoveryServer
Eureka Server at http://localhost:8761/
GET http://localhost:8761/eureka/v2/apps