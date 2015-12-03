DROP DATABASE if exists Users;

CREATE DATABASE Users;

USE Users;

CREATE TABLE UserInfo (
  userID int(11) primary key not null auto_increment,
  username varchar(50) not null,
  password varchar(100) not null,
  exp int(5) not null,
  coin int(5) not null
);

INSERT INTO UserInfo (username, password, exp, coin) VALUES ('zhiwang', '5f4dcc3b5aa765d61d8327deb882cf99', 230, 80);
INSERT INTO UserInfo (username, password, exp, coin) VALUES ('baiyuhua', 'fbdc548ae7fd3d9ad1c7315b9952ce43', 40, 120);
INSERT INTO UserInfo (username, password, exp, coin) VALUES ('ryosuke', '9cdfb439c7876e703e307864c9167a15', 170, 240);
INSERT INTO UserInfo (username, password, exp, coin) VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', 1000, 1000);