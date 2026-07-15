DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS booked_products;
DROP TABLE IF EXISTS order_bookings;

CREATE TABLE IF NOT EXISTS products (
  uuid UUID NOT NULL,
  fragile BOOLEAN NOT NULL DEFAULT False,
  width NUMERIC NULL,
  height NUMERIC NULL,
  depth NUMERIC NULL,
  weight NUMERIC NULL,
  quantity BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT pk_products PRIMARY KEY (uuid)
 );

CREATE TABLE IF NOT EXISTS order_bookings (
  order_id UUID NOT NULL,
  delivery_id  UUID NULL,
  CONSTRAINT pk_order_bookings PRIMARY KEY (order_id)
 );

 CREATE TABLE IF NOT EXISTS booked_products (
   order_id UUID NOT NULL,
   product_id  UUID NOT NULL,
   quantity BIGINT NOT NULL,
   CONSTRAINT pk_booked_products PRIMARY KEY (order_id, product_id),
   CONSTRAINT fk_booked_products_order_bookings FOREIGN KEY (order_id) REFERENCES order_bookings(order_id)
  );