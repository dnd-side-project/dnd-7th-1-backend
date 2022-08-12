-- 회원 생성
insert into user values(1,"A-Intro", null, null, "A-mail@gmail.com", "NickA", "UserA");
insert into user values(2,"B-Intro", null, null, "B-mail@naver.com", "NickB", "UserB");
insert into user values(3,"C-Intro", null, null, "C-mail@daum.com", "NickC", "UserC");
insert into user values(4,"D-Intro", null, null, "D-mail@gmail.com", "NickD", "UserD");
insert into user values(5,"E-Intro", null, null, "E-mail@dnd.com", "NickE", "UserE");
insert into user values(6,"F-Intro", null, null, "F-mail@gmail.com", "NickF", "UserF");
insert into user values(7,"G-Intro", null, null, "G-mail@naver.com", "NickG", "UserG");
insert into user values(8,"H-Intro", null, null, "H-mail@dnd.com", "NickH", "UserH");
insert into user values(9,"I-Intro", null, null, "I-mail@daum.com", "NickI", "UserI");


-- 친구 관계 생성
insert into friend values(1, "Accept", 1, 2);
insert into friend values(2, "Accept", 1, 3);

insert into friend values(3, "Accept", 4, 5);

insert into friend values(4, "Wait", 6, 7);

insert into friend values(5, "Reject", 8, 9);

insert into friend values(6, "Accept", 2, 3);
