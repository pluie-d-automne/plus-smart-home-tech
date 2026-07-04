CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS payments;

CREATE TABLE IF NOT EXISTS payments (
  uuid UUID NOT NULL DEFAULT uuid_generate_v4 (),
  order_id UUID NOT NULL,
  shopping_cart_id UUID NOT NULL,
  total_payment NUMERIC NOT NULL DEFAULT 0,
  delivery_total NUMERIC NOT NULL DEFAULT 0,
  fee_total  NUMERIC NOT NULL DEFAULT 0,
  CONSTRAINT payments PRIMARY KEY (uuid)
 );
