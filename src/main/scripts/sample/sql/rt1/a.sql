connect user2/&2@&1

set serveroutput on

DECLARE
   v_count number;
BEGIN
	SELECT count(*) INTO v_count FROM tab 
  	WHERE lower(tname) = 'table1' ;
	
	IF v_count = 1 THEN
		dbms_output.put_line('Table alerady exsits');
		dbms_output.put_line('removing table');
		EXECUTE IMMEDIATE 'drop table table1';
		
		END IF;
		
		
		EXECUTE IMMEDIATE 'create table table1 (
							name varchar(100),
							curdate date default sysdate)';
							
	
END;
/

connect user1/&3@&1


DECLARE
   v_count number;
BEGIN
	SELECT count(*) INTO v_count FROM tab 
  	WHERE lower(tname) = 'table1' ;
	
	IF v_count = 1 THEN

	
		dbms_output.put_line('Table alerady exsits');
 		dbms_output.put_line('removing table');
		EXECUTE IMMEDIATE 'drop table table1';
	END IF;
	
		EXECUTE IMMEDIATE 'create table table1 (
							name varchar(100),
							curdate date default sysdate)';
								
	
END;
/

exit;