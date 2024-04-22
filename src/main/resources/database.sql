create table fire_extinguishers
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY,
    location VARCHAR(255),
    expirationDate DATE
);

drop table fire_extinguishers;

-- CRUD

-- READ BY ID QUERY
select *
from fire_extinguishers
where id = 1;

select *
from fire_extinguishers
where id = ?;

-- READ ALL QUERY
select *
from fire_extinguishers;

-- CREATE QUERY
insert into fire_extinguishers(location, expirationDate)
values ('test1', now());

insert into fire_extinguishers(name, date)
values (?, ?);

-- UPDATE QUERY
update fire_extinguishers set date = now() where id = 1;

update fire_extinguishers set date = ? where id = ?;

update fire_extinguishers set date = ? and name = ? where id = ?;

-- DELETE QUERY
delete from fire_extinguishers where id = 1;

delete from fire_extinguishers where id = ?;