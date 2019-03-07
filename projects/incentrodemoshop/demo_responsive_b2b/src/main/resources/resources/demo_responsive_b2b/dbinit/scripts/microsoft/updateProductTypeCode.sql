DECLARE
	@domainname nvarchar(4000) = '&param1',
	@sql_stmt nvarchar(4000);
BEGIN
	Set @sql_stmt = CONCAT('UPDATE PRODUCT SET TYPECODE = 259 WHERE TYPECODE = 258 AND DOMAINID = (select domainid from domaininformation where domainname=''', @domainname, ''')')
	
	EXECUTE sp_executesql @sql_stmt
END;
/