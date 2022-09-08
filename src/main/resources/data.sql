insert into user values(1,"2022-08-01 01:00", "A-Intro", true, true, true, 123456, 37.330436, -122.030216, "A-mail@gmail.com", "NickA", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrQSIsImV4cCI6MTY2MzQwNTM2NX0.H4U5oqkJA77OJTqZxTzzx-kaiUJn34sa_dl4EI6AQ6o");
insert into user values(2,"2022-08-02 02:00", "B-Intro", true, true, true, 123456, 37.331184, -122.02311, "B-mail@naver.com", "NickB", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrQiIsImV4cCI6MTY2MzQwNTQ4NH0.r8gzuAon6o4EG8-va5R_zZsGvEu4Kjr1dS1061wnzPI");
insert into user values(3,"2022-08-03 03:00", "C-Intro", true, true, true, 123456, 37.337542, -122.036574, "C-mail@daum.com", "NickC", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrQyIsImV4cCI6MTY2MzQwNTUwMn0.y6rdfYAaFLsax67t_vzwvW1JKdLPxCxSpzdx9zFEwy8");

insert into user values(4,"2022-08-04 04:00", "D-Intro", true, true, true, 123456, 37.337542, -122.038444, "D-mail@gmail.com", "NickD", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrRCIsImV4cCI6MTY2MzQwNTUyMH0.DbH4AWj7awFPvYsgy871mgp9Z_LHlpLtsd94f9IUCW0");
insert into user values(5,"2022-08-05 05:00", "E-Intro", true, true, true, 123456, 37.337542, -122.041062, "E-mail@dnd.com", "NickE", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrRSIsImV4cCI6MTY2MzQwNTUzNX0.PjbYBjkVrfjbrAyogWVt5dmx3FirTR1-IBrOG5ejoC4");

insert into user values(6,"2022-08-06 06:00", "F-Intro", true, true, true, 123456, 37.337542, -122.041062, "F-mail@gmail.com", "NickF", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrRiIsImV4cCI6MTY2MzQwNTU1NX0.Zogn6tgwsva2VZNOm0cS0hQGJVrQbfRuiodNDLtAfNU");
insert into user values(7,"2022-08-07 07:00", "G-Intro", true, true, true, 123456, 37.337542, -122.041062, "G-mail@naver.com", "NickG", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrRyIsImV4cCI6MTY2MzQwNTU3Mn0.LZ_EpNd4a4_87TfYgd_TjBIFqO7r135NGDlxmH5_HhA");

insert into user values(8,"2022-08-08 08:00", "H-Intro", true, true, true, 123456, 37.337542, -122.041062, "H-mail@dnd.com", "NickH", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrSCIsImV4cCI6MTY2MzQwNTU4N30.gZaVctCFPry9w-yoLwT5w8Pd_QYiVEQ_WYqy8jHg6bs");
insert into user values(9,"2022-08-09 09:00", "I-Intro", true, true, true, 123456, 37.337542, -122.041062, "I-mail@daum.com", "NickI", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrSSIsImV4cCI6MTY2MzQwNTU5OH0.OvflxWgvoAx1M3R7lMi0FrYvSoRG_WP6xxVeYZSxxs0");

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
insert into challenge values(1, "2022-08-22", "챌린지1의 신청 메시지", "챌린지1", "2022-08-23", "Progress", "Widen", "11ed1e26d25aa6b4b02fbb2d0e652b0f");
insert into challenge values(2, "2022-08-22", "챌린지2의 신청 메시지", "챌린지2", "2022-08-23", "Progress", "Accumulate", "11ed1e42ae1af37a895b2f2416025f66");
insert into challenge values(3, "2022-08-22", "챌린지3의 신청 메시지", "챌린지3", "2022-08-23", "Progress", "Widen", "11ed1e42ae5febbb895bf3c6f08ac475");

-- 챌린지에 참여하는 회원 정보
insert into user_challenge values(1, "Red", "Progress", 1, 1);
insert into user_challenge values(2, "Red", "Progress", 1, 2);

insert into user_challenge values(3, "Pink", "Progress", 2, 1);
insert into user_challenge values(4, "Red", "Progress", 2, 3);

insert into user_challenge values(5, "Yellow", "Progress", 3, 1);
insert into user_challenge values(6, "Red", "Progress", 3, 4);

-- 운동 기록 정보
insert into exercise_record values(1, 3000, "2022-08-23 18:00", 1800, "A의 첫 번째 운동기록", "2022-08-23 17:30", 5000, 1);
insert into exercise_record values(2, 5000, "2022-08-23 13:00", 3600, "A의 두 번째 운동기록", "2022-08-23 12:00", 10000, 1);

insert into exercise_record values(3, 1500, "2022-08-23 15:00", 7200, "B의 운동기록", "2022-08-23 13:00", 2500, 2);

insert into exercise_record values(4, 2000, "2022-08-23 22:10", 600, "C의 운동기록", "2022-08-23 22:00", 1000, 3);

insert into exercise_record values(5, 500, "2022-08-22 22:05", 300, "D의 첫 번째 운동기록", "2022-08-23 22:00", 550, 4);
insert into exercise_record values(6, 1000, "2022-08-23 00:15", 900, "D의 두 번째 운동기록", "2022-08-23 00:00", 1100, 4);

insert into exercise_record values(7, 6000, "2022-08-23 10:30", 3600, "E의 운동기록", "2022-08-23 10:00", 15000, 5);

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

-- 발표 영상 용 더미 데이터(마지막 위치 확인!!!!!!!!!!!)
insert into user values(11,"2022-08-23 08:00", "희재횽아 화이팅!", true, true, true, 123456, null, null, "더미본인@naver.com", "희재횽아짱","user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrQSIsImV4cCI6MTY2MzQwNTM2NX0.H4U5oqkJA77OJTqZxTzzx-kaiUJn34sa_dl4EI6AQ6o");
insert into user values(12,"2022-08-23 09:00", "주로 밤에 활동합니다. 밤산책최고야!", true, true, true, 123456, 37.5187680, 126.9311120, "올빼미@naver.com", "여의도올빼미","user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrQSIsImV4cCI6MTY2MzQwNTM2NX0.H4U5oqkJA77OJTqZxTzzx-kaiUJn34sa_dl4EI6AQ6o");
insert into user values(13,"2022-08-23 10:00", "맛집탐방하면서 밥 먹고 산책 알지? ㅎㅎ", true, true, true, 123456, 37.5175520, 126.9292420, "합정@naver.com", "합정먹보","user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrQSIsImV4cCI6MTY2MzQwNTM2NX0.H4U5oqkJA77OJTqZxTzzx-kaiUJn34sa_dl4EI6AQ6o");
insert into user values(14,"2022-08-23 12:00", "생활버닝이 짱이야 다 덤벼", true, true, true, 123456, 37.5215040, 126.9277460, "칼로리@naver.com", "0칼로리", "user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrQSIsImV4cCI6MTY2MzQwNTM2NX0.H4U5oqkJA77OJTqZxTzzx-kaiUJn34sa_dl4EI6AQ6o");
insert into user values(15,"2022-08-23 14:00", "우리동네 내가 다 먹을거야", true, true, true, 123456, 37.5184640, 126.9281200, "우리동네@naver.com", "몽쉘통통","user/profile/default_profile.png", "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOaWNrQSIsImV4cCI6MTY2MzQwNTM2NX0.H4U5oqkJA77OJTqZxTzzx-kaiUJn34sa_dl4EI6AQ6o");

insert into friend values(9, "Accept", 11, 12);
insert into friend values(10, "Accept", 11, 13);
insert into friend values(11, "Accept", 11, 14);
insert into friend values(12, "Accept", 11, 15);

insert into challenge values(4, "2022-08-23", "너만힘드냐고?", "너만힘들어?", "2022-08-24", "Progress", "Widen", "11ed1e26d25aa6b4b02fbb2d0e652b01");
insert into challenge values(5, "2022-08-23", "새벽운동챌린지임", "지금은새벽5시", "2022-08-24", "Progress", "Accumulate", "11ed1e26d25aa6b4b02fbb2d0e652b02");
insert into challenge values(6, "2022-08-23", "지면 치킨임", "치킨빵챌린지", "2022-08-24", "Progress", "Widen", "11ed1e26d25aa6b4b02fbb2d0e652b03");

insert into user_challenge values(7, "Pink", "Progress", 4, 11);
insert into user_challenge values(8, "Pink", "Progress", 4, 12);
insert into user_challenge values(9, "Yellow", "Progress", 5, 11);
insert into user_challenge values(10, "Yellow", "Progress", 5, 13);
insert into user_challenge values(11, "Red", "Progress", 6, 11);
insert into user_challenge values(12, "Red", "Progress", 6, 14);

-- id,거리, 끝시간, 운동시간, 메시지, 시작시간, 발걸음, 유저번호
insert into exercise_record values(8, 585, "2022-08-25 02:39", 540, "졸려", "2022-08-25 02:30", 571, 14);
insert into exercise_record values(9, 411, "2022-08-25 03:27", 360, "배고파", "2022-08-25 03:21", 491, 12);
insert into exercise_record values(10, 358, "2022-08-25 04:32", 360, "살많이빠진듯", "2022-08-25 04:37", 393, 13);
insert into exercise_record values(11, 140, "2022-08-25 05:22:38", 360, "몽쉘먹으러가야겠다", "2022-08-25 04:19:04", 164, 15);

-- 0칼로리
insert into matrix values(81,  37.5199840, 126.9299900, 8);
insert into matrix values(82,  37.5202880, 126.9296160, 8);
insert into matrix values(83,  37.5205920, 126.9292420, 8);
insert into matrix values(84,  37.5208960, 126.9292420, 8);
insert into matrix values(85,  37.5208960, 126.9288680, 8);
insert into matrix values(86,  37.5212000, 126.9288680, 8);
insert into matrix values(87,  37.5215040, 126.9284940, 8);
insert into matrix values(88,  37.5215040, 126.9281200, 8);
insert into matrix values(89,  37.5215040, 126.9277460, 8);
insert into matrix values(90,  37.5215040, 126.9311120, 9);
insert into matrix values(91,  37.5215040, 126.9314860, 9);
insert into matrix values(92,  37.5212000, 126.9314860, 9);
insert into matrix values(93,  37.5212000, 126.9318600, 9);
insert into matrix values(94,  37.5208960, 126.9318600, 9);
insert into matrix values(95,  37.5208960, 126.9322340, 9);
insert into matrix values(96,  37.5205920, 126.9322340, 9);
insert into matrix values(97,  37.5202880, 126.9326080, 9);
insert into matrix values(98,  37.5199840, 126.9329820, 9);
insert into matrix values(99,  37.5196800, 126.9329820, 9);
insert into matrix values(100, 37.5196800, 126.9326080, 9);
insert into matrix values(101, 37.5193760, 126.9326080, 9);
insert into matrix values(102, 37.5193760, 126.9322340, 9);
insert into matrix values(103, 37.5190720, 126.9322340, 9);
insert into matrix values(104, 37.5190720, 126.9318600, 9);
insert into matrix values(105, 37.5187680, 126.9318600, 9);
insert into matrix values(106, 37.5190720, 126.9314860, 9);
insert into matrix values(107, 37.5187680, 126.9314860, 9);
insert into matrix values(108, 37.5187680, 126.9311120, 9);
insert into matrix values(109, 37.5193760, 126.9273720, 11);
insert into matrix values(110, 37.5193760, 126.9273720, 11);
insert into matrix values(111, 37.5190720, 126.9273720, 11);
insert into matrix values(112, 37.5187680, 126.9277460, 11);
insert into matrix values(113, 37.5184640, 126.9281200, 11);
insert into matrix values(114, 37.5178560, 126.9273720, 10);
insert into matrix values(115, 37.5178560, 126.9281200, 10);
insert into matrix values(116, 37.5181600, 126.9281200, 10);
insert into matrix values(117, 37.5181600, 126.9284940, 10);
insert into matrix values(118, 37.5178560, 126.9288680, 10);
insert into matrix values(119, 37.5178560, 126.9292420, 10);
insert into matrix values(120, 37.5178560, 126.9296160, 10);
insert into matrix values(121, 37.5181600, 126.9296160, 10);
insert into matrix values(122, 37.5175520, 126.9273720, 10);
insert into matrix values(123, 37.5175520, 126.9292420, 10);

update hibernate_sequence set next_val=124;