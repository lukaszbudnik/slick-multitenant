drop view v_payment if exists;

drop table payment if exists;

drop sequence payment_ref_seq if exists;

create table payment (ref int not null, ts timestamp not null, description bytea not null, who bytea not null, amount bytea not null, deleted bool not null);

create sequence payment_ref_seq;

create view v_payment as select * from payment p1 where p1.deleted = false and p1.ts IN (select max(p2.ts) from payment p2 where p1.ref = p2.ref);
