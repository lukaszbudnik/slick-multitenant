drop view v_product if exists;

drop table product if exists;

drop sequence product_ref_seq if exists;

create table product (ref int not null, ts timestamp not null, description text not null, specialCode int, deleted bool not null);

create sequence product_ref_seq;

create view v_product as select * from product p1 where p1.deleted = false and p1.ts IN (select max(p2.ts) from product p2 where p1.ref = p2.ref);
