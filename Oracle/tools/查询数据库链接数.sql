--查看不同用户的连接数
select username,count(username) from v$session where username is not null group by username;

--数据库允许的最大连接数
select value from v$parameter where name = 'processes';

--当前的连接数
select count(*) from v$process;

--查询oracle的并发连接数
Select count(*) from v$session where status='ACTIVE' ;
