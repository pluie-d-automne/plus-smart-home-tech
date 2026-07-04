CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS order_content;
DROP TABLE IF EXISTS orders;

CREATE TABLE IF NOT EXISTS orders (
  uuid UUID NOT NULL DEFAULT uuid_generate_v4 (),
  shopping_cart_id UUID NULL,
  payment_id UUID NULL,
  delivery_id UUID NULL,
  state VARCHAR NOT NULL DEFAULT 'NEW',
  delivery_weight NUMERIC NULL,
  delivery_volume NUMERIC NULL,
  fragile BOOLEAN NULL,
  total_price NUMERIC NULL,
  delivery_price NUMERIC NULL,
  product_price NUMERIC NULL,
  user_name VARCHAR NOT NULL,
  CONSTRAINT pk_shopping_carts PRIMARY KEY (uuid)
 );


CREATE TABLE IF NOT EXISTS order_content (
  order_id UUID NOT NULL,
  product_id UUID  NOT NULL,
  quantity BIGINT  NOT NULL,
  CONSTRAINT uq_order_content UNIQUE (order_id, product_id),
  CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(uuid)
 );
