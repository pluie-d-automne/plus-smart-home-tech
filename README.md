# Учебный проект smart-home-tech

## Collector
1) Принимает данные от хабов пользователей по протоколу HTTP в формате JSON.
2) Полученные данные кодирует в формат Avro и сохраняет в топики в Kafka:
    * telemetry.sensors.v1 - данные, связанные с показаниями датчиков событиями устройств.
    * telemetry.hubs.v1 - данные, связанные с хабами и сценариями.
   
Спецификации OpenApi для сервиса Collector в SwaggerUI:
```bash
docker run -p 9090:8080 -e API_URL=https://code.s3.yandex.net/Java/project19/http-api-spec.json swaggerapi/swagger-ui
```
## Serialization
Родительский Maven-модуль, объединяющий модули со схемами Avro и Protobuf.