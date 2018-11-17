
create table StaffUser (
  id bigint(15) not null auto_increment,
  username varchar(255) not null,
  password varchar(255) not null,
  lastLoginDate datetime null,
  createdBy varchar(255) not null,
  updatedBy varchar(255) null,
  createdAt timestamp not null,
  updatedAt timestamp null,
  primary key (id),
  unique key uni_idx_username (username)
) engine=innodb  default charset=utf8;

