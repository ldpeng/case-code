--�鿴�����ı� 
select b.owner,b.object_name,a.session_id,a.locked_mode from v$locked_object a,dba_objects b where b.object_id = a.object_id;

--�鿴�Ǹ��û��Ǹ������ճ�����
select b.username,b.sid,b.serial#,logon_time from v$locked_object a,v$session b where a.session_id = b.sid order by b.logon_time;

--�鿴���ӵĽ��� 
SELECT sid, serial#, username, osuser FROM v$session;

--3.����������sid, serial#,os_user_name, machine_name, terminal������type,mode
SELECT s.sid, s.serial#, s.username, s.schemaname, s.osuser, s.process, s.machine,
s.terminal, s.logon_time, l.type
FROM v$session s, v$lock l
WHERE s.sid = l.sid
AND s.username IS NOT NULL
ORDER BY sid;

--�����佫���ҵ����ݿ������е�DML�����������������Է��֣�
--�κ�DML�����ʵ��������������һ���Ǳ�����һ����������

--ɱ������ sid,serial#
--alter system kill session'210,11562';