connect user2/&2@&1

insert into table1(name) values('its a user2');

commit;

connect user1/&3@&1

insert into table1(name) values('its a user1');

commit;

exit