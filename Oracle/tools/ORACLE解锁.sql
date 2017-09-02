--查看被锁的表 
select b.owner,b.object_name,a.session_id,a.locked_mode from v$locked_object a,dba_objects b where b.object_id = a.object_id;

--查看那个用户那个进程照成死锁
select b.username,b.sid,b.serial#,logon_time from v$locked_object a,v$session b where a.session_id = b.sid order by b.logon_time;

--查看连接的进程 
SELECT sid, serial#, username, osuser FROM v$session;

--3.查出锁定表的sid, serial#,os_user_name, machine_name, terminal，锁的type,mode
SELECT s.sid, s.serial#, s.username, s.schemaname, s.osuser, s.process, s.machine,
s.terminal, s.logon_time, l.type
FROM v$session s, v$lock l
WHERE s.sid = l.sid
AND s.username IS NOT NULL
ORDER BY sid;

--这个语句将查找到数据库中所有的DML语句产生的锁，还可以发现，
--任何DML语句其实产生了两个锁，一个是表锁，一个是行锁。

--杀掉进程 sid,serial#
--alter system kill session'210,11562';