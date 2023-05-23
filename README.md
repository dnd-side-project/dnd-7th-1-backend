# 🏃 땅따먹기 기반 운동 장려 애플리케이션 NEMODU
>DND 7기 프로젝트 (2022.08~)  

<details>
    <summary><b><h2>프로젝트 설명</h2> <a href="https://www.youtube.com/watch?v=gFkrgJt2ttQ">시연 영상 📺</a></b></summary>
<div markdown="1">

![image](https://user-images.githubusercontent.com/77626299/214999568-443dee1f-1ee5-44b4-9197-7faf89205946.png)  
![02](https://user-images.githubusercontent.com/77626299/214999579-4b63111b-4499-49f7-8333-6234f7d29dfc.png)  
![03](https://user-images.githubusercontent.com/77626299/214999588-81f4a98a-b9de-4dd2-90c1-46d424ee023a.png)
![04](https://user-images.githubusercontent.com/77626299/214999593-573763f0-e909-4a61-be2c-0ed87de86487.png)
![05](https://user-images.githubusercontent.com/77626299/214999597-60b1b02e-ac02-4222-a1a3-17eaa3d79927.png)
![06](https://user-images.githubusercontent.com/77626299/214999603-a8eb2ac1-e386-4c36-9e1c-69cda2a01a92.png)
![07](https://user-images.githubusercontent.com/77626299/214999607-b7b9dab9-5ffb-47d0-8696-a8003b451965.png)
![08](https://user-images.githubusercontent.com/77626299/214999612-3164bde3-4d81-4c14-813a-e2655c8a24e9.png)
![09](https://user-images.githubusercontent.com/77626299/214999620-7787e545-bcfc-4fd1-b192-d4498d502b0e.png)
![10](https://user-images.githubusercontent.com/77626299/214999626-562bbcb8-68af-4bca-93de-f0bb87bb7099.png)
![11](https://user-images.githubusercontent.com/77626299/214999639-29f5133f-1d77-4623-99cf-820ed9ab1281.png)
![12](https://user-images.githubusercontent.com/77626299/214999647-d7297159-55ae-4a0d-824e-a49dc016da4c.png)
![13](https://user-images.githubusercontent.com/77626299/214999669-333973af-5620-4637-bac5-dc322efe8f4c.png)
    </div>
</details>

## :page_with_curl: 기능 리스트
- <b>회원</b>
  - 프로필 조회 및 수정
  - 마이 페이지 조회 (운동 기록 정보, 회원 정보 등)
  - 운동 기록 필터 관리 (내 기록 보기, 친구 기록 보기 등)
  - 운동 기록 메시지 수정
  - 메인 화면 조회 (내 영역, 이번 주 영역, 친구 영역, 챌린지 정보, 챌린지 멤버 영역 등)
- <b>챌린지 (지인과 함께 일정 기간동안 운동 기록을 공유하며 경쟁하는 기능)  </b>
  - 챌린지 생성 및 초대
  - 챌린지 수락, 거절
  - 챌린지 상태 관리 
    - 챌린지 시작날짜에 맞춰 진행
    - 월요일 00시 챌린지 종료
    - 챌린지 참여하는 사람(챌린지 멤버)에 대한 상태 관리 및 주최자 관리
  - 챌린지 색깔 계산
  - 상태 별 챌린지 목록 조회
  - 상태 별 챌린지 상세 목록 조회
  - 챌린지 진행 기간동안 기록된 영역 조회
  - 챌린지 삭제
- <b>운동 기록    </b>
  - 운동 기록 생성
  - 운동 기록 조회
- <b>랭킹  </b>
  - 역대 누적 칸 수 랭킹 계산
  - 이번 주 영역 수 랭킹 계산
  - 걸음 수 랭킹 계산
- <b>친구  </b>
  - 친구 신청 및 응답
  - 카카오 친구 목록 조회  
  - 카카오 친구 초대 메시지 전송
  - 친구 삭제  
  - 위치 기반 친구 추천
- <b>회원가입/로그인  </b>
  - 카카오, 애플 소셜 로그인 지원
  - JWT 기반 토큰 인증
  - 카카오 회원 정보 조회
  - 로그아웃  
  - 회원 탈퇴  
- <b>푸시 알람  </b>
  - FCM 토큰 관리
  - 특정 상황에서 푸시 알람 요청 및 알람 관리
- <b>HTTPS 지원</b>

## :large_blue_diamond: Architecture
![image](https://user-images.githubusercontent.com/77626299/236676188-26c76529-39ed-4eec-ad7b-0925ed10a1c9.png)

## :large_orange_diamond: ER-MODEL
![image](https://user-images.githubusercontent.com/77626299/236677094-100ff30a-f513-4617-9b73-b0cdea754d5d.png)

## ⚔️ 기술 스택
### 🔥 SERVER
- <b>Java 11 (OpenJDK 11)</b>
- <b>Spring boot 2.7.1</b>  
  - 의존성 관리
  - Auto Configuration
- <b>Spring Security</b>  
  - JWT 기반 토큰을 활용한 인증
  - ExceptionEntryPoint를 활용한 예외 처리
- <b>Spring Data JPA</b>  
  - 객체 중심의 ORM 쿼리 활용
- <b>QueryDSL</b>  
  - 컴파일 단게에서 쿼리 에러 검출
  - 중복 쿼리 재활용을 통해 유지보수에 용이하도록 함
  - 동적 쿼리를 효율적으로 작성
  - No Offset을 활용한 페이징 쿼리 개선
- <b>Spring Batch</b>  
  - 체계적인 배치 작업: Chunk 단위로 트랜잭션을 관리해 메모리에 한 번에 적재하지 않도록 해 서버의 부담 최소화
  - Challenge의 상태 변경 시 배타 락 설정 -> Chunk 단위 작업 필요
  - AbstractPagingItemReader, JpaItemWriter를 상속받아 네모두의 엔티티 구성에 맞게 구성
### 🎁 DB
- <b>MySQL 8.0.30</b>  
  - 테이블의 성격을 고려한 객체 중심의 엔티티 설계
  - N:M 관계를 조인 테이블을 활용해 1:N, M:1로 풀어내 개발 복잡도 저하  
  - N-gram Parser를 활용한 부분 문자열 검색 (FullText-index)
### 🌉 Infra
- <b>Nginx</b>
  - SSL 인증서를 활용한 암호화/복호화
  - 발급받은 도메인(nemodu.site)에 대한 프록시 역할
### ☁️ Cloud
- <b>AWS EC2</b>  
  - 필요한 만큼 컴퓨팅 자원 사용(온디맨드)
  - 인스턴스 모니터링
- <b>AWS RDS</b>  
  - 데이터베이스 모니터링 및 관리
- <b>AWS S3</b>  
  - 회원 프로필 사진을 비롯한 파일 저장
  - 정적 페이지 저장 및 전달 (약관 페이지)
- <b>AWS ElastiCache</b>  
  - <b>Redis</b>를 활용해 데이터 캐싱
  - 빠른 조회 및 서버 부하 감소
  - Expire Event 구독: 이벤트가 발행되는 경우 데이터 처리 (FCM 토큰 재발급 요청)
  
## 🏋 개선 과정
#### :one: 예외 처리 개선  
>추후 발생할 다양한 예외를 처리하기 위해 유연한 구조 필요
- 발생할 수 있는 예외에 대한 코드 정의 (ExceptionCodeSet.java)
- 인터페이스를 통해 예외 클래스의 역할 정의 (BaseException.java)
- 추상 클래스를 활용해 예외 클래스의 중복 코드 최소화 (BaseExceptionAbs.java)
- @RestControllerAdvice를 활용해 컨트롤러 레벨에서 발생하는 예외 공통 처리
- 필터 레벨에서 발생하는 예외를 처리하기 위해, AuthenticationException을 상속 받은 예외 클래스 생성
- 기존에 정의한 예외 코드를 활용해 AuthenticationEntryPoint를 상속받은 핸들러에서 예외 처리
- 케이스별 예외 클래스 생성 및 각 예외에 대한 개별 처리 <br>(Ex:FilterException.java, 필터 레벨에서 발생한 예외를 처리하기 위해 AuthenticationException 상속)

#### :two: 회원가입/로그인 개선
>카카오, 애플 2가지 소셜 로그인을 지원. 나중에 추가될 수 있는 다양한 회원가입 형태에 대한 대비
- Resource Server(현 시점에서 카카오, 애플)에 맞춰 개발해 구조 변경이 힘들다고 판단 -> Resource Server 의존성 최소화
- 로그인 후 Redirect 받는 형태가 아닌, 클라이언트가 로그인 후 토큰까지 발급하는 것으로 역할 분리.
- 소셜 로그인에 대한 공통 API를 제공해 서버에서 필요한 정보를 받아 회원 정보 관리

#### :three: 메인 화면 API 개선
>데이터가 많아질수록 메인 화면 API 성능이 급격히 저하됨
- 1:N 형태로 설계된 '운동 기록<->영역'을 조회할 때, 2번에 걸쳐 조회하던 것을 Join을 활용해 한 번으로 줄임 (DB I/O 최소화)
- 회원 중심으로 데이터를 조회하는 로직 -> 영역을 중심으로 데이터 조회. 즉, 한 번에 데이터를 조회하도록 로직 개선
- 올바른 인덱스 설계를 통해 쿼리 성능 개선  
  - <b>테스트 결과: 실행 속도 81.95% 향상</b>
- 클라이언트의 지도 축척 정보를 활용해 MBR(Minimum Bounding Rectangle) 계산 -> 불필요한 데이터 최소화
- MySQL의 MBR_CONTAINS() 함수를 활용해 MBR 내 영역만 조회하도록 개선  
  - <b>테스트 결과: 실행 속도 42.86% 향상</b>

#### :four: 랭킹 개선
>랭킹 계산에 활용되던 QueryDSL의 Tuple클래스의 의존성이 전역에 퍼져있음.
- 랭킹 계산에 필요한 공통 DTO 생성
- DTO를 활용해 조회함으로써, Tuple에 대한 의존성 제거
- 잘못된 Join 사용으로 인해 불필요하게 복잡해진 랭킹 계산 로직을 Outer Join을 활용해 간소화
- 랭킹 계산을 공통 모듈로서 활용해 추후 다양한 곳에서 사용하기 쉽도록 함

#### :five: 배치 작업 개선
>사용자가 많아졌을 때, 많은 데이터를 한 번에 메모리에 적재하는 것은 부담
- 일정 단위만큼 데이터를 조회해 처리하도록 함 -> 코드가 복잡해 유지보수가 힘들 것으로 예상
- 특히, 챌린지 상태 변경 배치의 경우, 변경 전 데이터를 조회할 수 없도록 배타 락을 적용함 -> 빠르게 처리해야 함.
- Spring Batch를 도입해 Job, Step 각 단계 별 작업을 Chunk로 단위로 수행
- Postman으로 직접 테스트하기 힘들기 때문에, 테스트 코드 작성

#### :six: 푸시 알람을 위한 FCM 토큰 신선도 유지
>각 사용자의 FCM 토큰이 2달이 경과한 경우, 클라이언트에게 재발급을 요청해야 함.
- 사용자마다 만료 시간이 다르기 때문에, 스케줄러를 통해 직접 확인하는 것은 비효율적
- FCM 토큰을 Redis에 저장하고, TTL을 2달(60일)로 설정
- 토큰이 만료되는 경우 Expire Event를 발행해 서버로 전달 -> 이벤트를 감지하면 key 정보에 맞춰 토큰 재발급 요청
- Apns(iOS의 푸시 알람 서비스)의 Silent Message를 활용해 재발급 요청

#### :seven: 로그 전략 개선 
>서버 모니터링 및 빠른 트러블 슈팅을 위한 로그 전략 개선 필요
- 푸시 알람, 배치 등 특정 서비스에 따른 Logger 분리
- 로그 레벨에 따른 로그 수집
- 시간, 용량 별 Rolling 전략 수립
- Logger 클래스를 Request Scope Bean으로 생성해 쉽게 로그를 확인할 수 있도록 개선<br>-> @Qualifer를 통해 특정 Logger를 주입해야 하는 부분은 개선 필요

#### :eight: Prometheus, Grapana를 활용한 모니터링 (진행 중)
>운영을 위해 JVM 메모리 사용량 및 GC 모니터링이 필요하다고 판단
