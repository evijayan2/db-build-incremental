connect user4/&2@&1

insert into table2(name) values('its b user4');

commit;

connect user2/&3@&1

insert into table2(name) values('its b user2');

commit;

exit