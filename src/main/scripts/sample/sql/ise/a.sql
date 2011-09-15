connect user1/&2@&1

set serveroutput on

DECLARE
   v_count number;
BEGIN
	SELECT count(*) INTO v_count FROM tab 
  	WHERE lower(tname) = 'ise1' ;
	
	IF v_count = 1 THEN
		dbms_output.put_line('Table alerady exsits');
		dbms_output.put_line('removing table');
		EXECUTE IMMEDIATE 'drop table ise1';
		
		END IF;
		
		
		EXECUTE IMMEDIATE 'create table ise1 (
							name varchar(100),
							curdate date default sysdate)';
							
	
END;
/

insert into ise1(name) values('ise values');

/

exit;