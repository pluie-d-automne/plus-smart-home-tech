CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS shopping_cart_content;
DROP TABLE IF EXISTS shopping_carts;

CREATE TABLE IF NOT EXISTS shopping_carts (
  uuid UUID NOT NULL DEFAULT uuid_generate_v4 (),
  state VARCHAR NOT NULL,
  user_name VARCHAR NOT NULL,
  CONSTRAINT pk_shopping_carts PRIMARY KEY (uuid)
 );

CREATE TABLE IF NOT EXISTS shopping_cart_content (
  shopping_cart_id UUID NOT NULL,
  product_id UUID  NOT NULL,
  quantity BIGINT  NOT NULL,
  CONSTRAINT UQ_CART_CONTENT UNIQUE (shopping_cart_id, product_id),
  CONSTRAINT fk_cart_content FOREIGN KEY (shopping_cart_id) REFERENCES shopping_carts(uuid)
 );
