-- update 12.3.2014
update "NURSA3DEV"."DOI" set url=REGEXP_REPLACE(url,'translational/', 'clinical/')
           where url like '%translational/%';

