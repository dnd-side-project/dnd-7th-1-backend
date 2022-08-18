-- 회원 생성
insert into user values(1,"2022-08-01 01:00", "A-Intro", 37.330436, -122.030216, "A-mail@gmail.com", "NickA", "UserA");
insert into user values(2,"2022-08-02 02:00", "B-Intro", 37.331184, -122.02311, "B-mail@naver.com", "NickB", "UserB");
insert into user values(3,"2022-08-03 03:00", "C-Intro", 37.337542, -122.036574, "C-mail@daum.com", "NickC", "UserC");

insert into user values(4,"2022-08-04 04:00", "D-Intro", 37.337542, -122.038444, "D-mail@gmail.com", "NickD", "UserD");
insert into user values(5,"2022-08-05 05:00", "E-Intro", 37.337542, -122.041062, "E-mail@dnd.com", "NickE", "UserE");

insert into user values(6,"2022-08-06 06:00", "F-Intro", 37.337542, -122.041062, "F-mail@gmail.com", "NickF", "UserF");
insert into user values(7,"2022-08-07 07:00", "G-Intro", 37.337542, -122.041062, "G-mail@naver.com", "NickG", "UserG");

insert into user values(8,"2022-08-08 08:00", "H-Intro", 37.337542, -122.041062, "H-mail@dnd.com", "NickH", "UserH");
insert into user values(9,"2022-08-09 09:00", "I-Intro", 37.337542, -122.041062, "I-mail@daum.com", "NickI", "UserI");

-- 친구 관계 생성
-- A-B-C는 서로 친구 관계
insert into friend values(1, "Accept", 1, 2);
insert into friend values(2, "Accept", 1, 3);
insert into friend values(3, "Accept", 2, 3);

-- A-B, A-C는 친구 관계
insert into friend values(4, "Accept", 1, 4);
insert into friend values(5, "Accept", 1, 5);

-- D-E는 친구 관계
insert into friend values(6, "Accept", 4, 5);

-- F-G는 수락 대기 중
insert into friend values(7, "Wait", 6, 7);

-- H-I는 요청했으나 거절된 상태
insert into friend values(8, "Reject", 8, 9);

-- 챌린지 정보
insert into challenge values(1, "2022-08-15", "챌린지1의 신청 메시지", "챌린지1", "2022-08-16", "Progress", "Widen", "11ed1e26d25aa6b4b02fbb2d0e652b0f");
insert into challenge values(2, "2022-08-15", "챌린지2의 신청 메시지", "챌린지2", "2022-08-16", "Progress", "Accumulate", "11ed1e42ae1af37a895b2f2416025f66");
insert into challenge values(3, "2022-08-15", "챌린지3의 신청 메시지", "챌린지3", "2022-08-16", "Progress", "Widen", "11ed1e42ae5febbb895bf3c6f08ac475");

-- 챌린지에 참여하는 회원 정보
insert into user_challenge values(1, "Red", "Progress", 1, 1);
insert into user_challenge values(2, "Red", "Progress", 1, 2);

insert into user_challenge values(3, "Pink", "Progress", 2, 1);
insert into user_challenge values(4, "Red", "Progress", 2, 3);

insert into user_challenge values(5, "Yellow", "Progress", 3, 1);
insert into user_challenge values(6, "Red", "Progress", 3, 4);

-- 운동 기록 정보
insert into exercise_record values(1, 3000, "2022-08-16 18:00", 1800, "A의 첫 번째 운동기록", "2022-08-16 17:30", 5000, 1);
insert into exercise_record values(2, 5000, "2022-08-16 13:00", 3600, "A의 두 번째 운동기록", "2022-08-16 12:00", 10000, 1);

insert into exercise_record values(3, 1500, "2022-08-16 15:00", 7200, "B의 운동기록", "2022-08-16 13:00", 2500, 2);

insert into exercise_record values(4, 2000, "2022-08-16 22:10", 600, "C의 운동기록", "2022-08-16 22:00", 1000, 3);

insert into exercise_record values(5, 500, "2022-08-15 22:05", 300, "D의 첫 번째 운동기록", "2022-08-15 22:00", 550, 4);
insert into exercise_record values(6, 1000, "2022-08-16 00:15", 900, "D의 두 번째 운동기록", "2022-08-16 00:00", 1100, 4);

insert into exercise_record values(7, 6000, "2022-08-16 10:30", 3600, "E의 운동기록", "2022-08-16 10:00", 15000, 5);

-- 영역 정보
insert into matrix values(1, 37.331558, -122.030216, 1);
insert into matrix values(2, 37.331558, -122.03059, 1);
insert into matrix values(3, 37.331184, -122.03059, 1);

insert into matrix values(4, 37.33081, -122.03059, 2);
insert into matrix values(5, 37.330436, -122.03059, 2);
insert into matrix values(6, 37.330436, -122.030216, 2);

insert into matrix values(7, 37.330062, -122.024232, 3);
insert into matrix values(8, 37.330062, -122.023858, 3);
insert into matrix values(9, 37.330062, -122.023484, 3);
insert into matrix values(10, 37.330062, -122.02311, 3);
insert into matrix values(11, 37.330436, -122.02311, 3);
insert into matrix values(12, 37.33081, -122.02311, 3);
insert into matrix values(13, 37.331184, -122.02311, 3);

insert into matrix values(14, 37.337542, -122.035826, 4);
insert into matrix values(15, 37.337542, -122.0362, 4);
insert into matrix values(16, 37.337542, -122.036574, 4);

insert into matrix values(17, 37.337542, -122.037696, 5);

insert into matrix values(18, 37.337542, -122.03807, 6);
insert into matrix values(19, 37.337542, -122.038444, 6);

insert into matrix values(20, 37.337542, -122.040314, 7);
insert into matrix values(21, 37.337542, -122.040688, 7);
insert into matrix values(22, 37.337542, -122.041062, 7);