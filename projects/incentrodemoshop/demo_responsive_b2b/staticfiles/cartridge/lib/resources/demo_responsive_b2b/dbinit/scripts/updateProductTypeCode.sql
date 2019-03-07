DECLARE
	domainidobt varchar2(4000);
	domainname varchar2(4000):='&param1';
	sql_stmt varchar2(4000);
BEGIN
	sql_stmt := 'select domainid from domaininformation where domainname=:1';
	EXECUTE IMMEDIATE sql_stmt INTO domainidobt USING domainname;

	UPDATE PRODUCT SET TYPECODE = '259' WHERE TYPECODE = '258' AND DOMAINID = domainidobt;	
END;
/