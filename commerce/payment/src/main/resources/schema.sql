CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS payments;

CREATE TABLE IF NOT EXISTS payments (
  uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
  order_id UUID NOT NULL,
  shopping_cart_id UUID NOT NULL,
  total_payment NUMERIC NULL,
  delivery_total NUMERIC NULL,
  fee_total NUMERIC NULL,
  product_price NUMERIC NULL,
  state VARCHAR NOT NULL,
  CONSTRAINT pk_payments PRIMARY KEY (uuid)
 );
