CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS products;

CREATE TABLE IF NOT EXISTS products (
  uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
  name VARCHAR NOT NULL,
  description VARCHAR NOT NULL,
  image_src VARCHAR,
  quantity_state VARCHAR NOT NULL,
  product_state VARCHAR NOT NULL,
  category VARCHAR,
  price NUMERIC NOT NULL,
  CONSTRAINT pk_products PRIMARY KEY (uuid)
 );
