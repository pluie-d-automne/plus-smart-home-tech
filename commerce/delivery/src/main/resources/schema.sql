CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS deliveries;
DROP TABLE IF EXISTS addresses;

CREATE TABLE IF NOT EXISTS addresses (
  uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
  country VARCHAR NOT NULL,
  city VARCHAR NOT NULL,
  street VARCHAR NOT NULL,
  house VARCHAR NOT NULL,
  flat VARCHAR NULL,
  CONSTRAINT pk_addresses PRIMARY KEY (uuid),
  CONSTRAINT uq_address UNIQUE(country, city, street, house, flat)
 );

CREATE TABLE IF NOT EXISTS deliveries (
  uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
  state VARCHAR NOT NULL,
  order_id UUID NOT NULL,
  from_address_uuid UUID NOT NULL,
  to_address_uuid UUID NOT NULL,
  delivery_weight NUMERIC NULL,
  delivery_volume NUMERIC NULL,
  fragile BOOLEAN NULL,
  delivery_cost NUMERIC NULL,
  CONSTRAINT pk_deliveries PRIMARY KEY (uuid),
  CONSTRAINT fk_from_address FOREIGN KEY (from_address_uuid) REFERENCES addresses(uuid),
  CONSTRAINT fk_to_address FOREIGN KEY (to_address_uuid) REFERENCES addresses(uuid)
 );

