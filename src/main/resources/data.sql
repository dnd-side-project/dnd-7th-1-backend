-- 회원 생성
insert into user values(1, "M", 178.2, null, null, "A-mail@gmail.com", "NickA", "UserA", "70.5");
insert into user values(2, "F", 164.1, null, null, "B-mail@naver.com", "NickB", "UserB", "58.2");
insert into user values(3, "M", 186.6, null, null, "C-mail@daum.com", "NickC", "UserC", "74.8");

insert into user values(4, "F", 158.0, null, null, "D-mail@gmail.com", "NickD", "UserD", "46.5");
insert into user values(5, "M", 172.9, null, null, "E-mail@dnd.com", "NickE", "UserE", "73.1");

insert into user values(6, "M", 174.3, null, null, "F-mail@gmail.com", "NickF", "UserF", "70.2");
insert into user values(7, "F", 170.3, null, null, "G-mail@naver.com", "NickG", "UserG", "60.6");

insert into user values(8, "M", 182.1, null, null, "H-mail@dnd.com", "NickH", "UserH", "74.3");
insert into user values(9, "M", 177.8, null, null, "I-mail@daum.com", "NickI", "UserI", "72.7");

-- 친구 관계 생성
insert into friend values(1, "Accept", 1, 2);
insert into friend values(2, "Accept", 1, 3);

insert into friend values(3, "Accept", 4, 5);

insert into friend values(4, "Wait", 6, 7);

insert into friend values(5, "Reject", 8, 9);

insert into friend values(6, "Accept", 2, 3);
