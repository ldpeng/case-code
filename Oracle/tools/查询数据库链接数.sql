--�鿴��ͬ�û���������
select username,count(username) from v$session where username is not null group by username;

--���ݿ���������������
select value from v$parameter where name = 'processes';

--��ǰ��������
select count(*) from v$process;

--��ѯoracle�Ĳ���������
Select count(*) from v$session where status='ACTIVE' ;
