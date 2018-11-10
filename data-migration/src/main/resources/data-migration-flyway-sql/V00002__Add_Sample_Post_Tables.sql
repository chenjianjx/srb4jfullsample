
create table Post (
  id bigint(15) not null auto_increment,
  userId bigint(15) not null,
  content varchar(500) not null,
  createdBy varchar(255) not null,
  updatedBy varchar(255) null,
  createdAt timestamp not null,
  updatedAt timestamp null ,
  primary key (id)
) engine=innodb  default charset=utf8;