CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS products;

CREATE TABLE IF NOT EXISTS products (
  uuid UUID NOT NULL DEFAULT uuid_generate_v4 (),
  fragile BOOLEAN NOT NULL DEFAULT False,
  width NUMERIC NULL,
  height NUMERIC NULL,
  depth NUMERIC NULL,
  weight NUMERIC NULL,
  quantity BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT pk_products PRIMARY KEY (uuid)
 );
