[] 각 기능에 대한 단위 테스트 목록 작성
- 30명 이하가 신청한 특강을 특정 userId로 특강 신청 API를 호출할 시(Param userId, lectureId) 정상적으로 테이블에 해당 내역이 적재되는가?
- 동일한 신청자가 이미 특강신청내역 테이블에 적재되어있는 lectureId에 대해 재신청을 할 시 '이미 신청이 된 강의입니다.'라는 문구 등이 반환되며 예외처리 되는가?
- 30명이 신청한 특강을 신청했을 시 '신청이 마감되었습니다.'라는 문구가 반환되며 요청을 실패처리되는가?
- 날짜를 param으로 받아 특강 신청 가능 목록 조회 API를 호출할 시 날짜별로 신청 가능한 특강 목록이 조회되는가?
- 특정 userId를 param으로 받아 특강신청완료목록조회 API를 호출할 시 userId로 신청완료된 특강 목록이 정상적으로 조회되는가?
- 특강신청완료목록 조회API는 특강 ID 및 이름, 강연자 정보를 담고있는가?
### Repository 단위 테스트도 이루어져야하는가? 
- 특강 정원 확인 쿼리 테스트(정원이 초과된 경우 제대로 된 카운트 반환 확인)
- 특강 신청 중복 확인 테스트(동일 사용자와 특강ID의 조합이 있을 경우 true 반환 확인)

[] 통합테스트 목록 작성
### H2와 TestContainer 중에 어떤 것을 사용할 것인가?
H2와 TestContainer : H2는 데이터베이스에 독립적인 환경에서 수행됨. Testcontainer는 실제 데이터베이스 환경에서 테스트 수행. 높은 신뢰성

- 특강 신청 API는 정상적으로 주어진 초기데이터를 받아 신청에 성공하고 성공 메세지를 반환하는가?
- 신청 가능 목록 API는 정상적으로 주어진 초기데이터를 받아 신청 가능 목록을 조회하고 예상했던 강의 목록을 반환하는가?
- 신청 완료 목록 API는 정상적으로 주어진 초기데이터를 받아 신청 완료 목록을 조회하고 예상했던 신청 완료 강의 목록을 반환하는가? 
- 30명 이상이 수강 신청했을 때 어떻게 처리되는가?(동시에 동일한 특강에 대해 40명이 신청했을 때, 30명만 성공하는 것을 검증)
- 같은 사용자가 동일한 특강에 대해 신청했을 시 실패하는가? (동일한 유저 정보로 같은 특강을 5번 신청했을 떄, 1번만 성공하는 것 검증)
- 
- [] ERD 설계
- user의 정보를 담고있는 Users 테이블
- Users ↔ Lecture_Applications 1:N 하나의 사용자는 여러 특강에 신청할 수 있음
- lectur의 정보를 담고있는 Lectures 테이블
- Lectures ↔ Lecture_Applications  1:N  하나의 특강에 여러 신청자가 있을 수 있음.
- user가 신청한 lectures 정보 (history)를 담고있는 Lecture_Application 테이블
- 같은 사용자에게 여러 번의 특강 슬롯이 제공되지 않게 제한할 것인가? (UNIQUE KEY를 만들어 user_id와 lecture_id의 조합이 중복되지 않게함, 중복 시 에러 발생)
- 30명 제한은 CHECK로 관리할 수 있으나 Service에서 비지니스로직으로 관리하는 것이 좋을 것 같음.
- @Transactional 을 통해 동시성 관리, Lecture_Application 테이블에서 신청된 인원 카운트해 정원 초과 여부 미리 확인 후 신청 처리하도록

[] 애플리케이션 설계

- repository를 인터페이스로 설계 
- repository를 구현하는 repositoryImpl 작성.(비즈니스 로직과 데이터의 접근을 분리하기)
- 이번 주차의 핵심 포인트는 DIP 그리고 DBLock! 

- DIP를 어떻게 이룰 것인가?
- UseCases 생성해서 다시 짜보기
- facade 클래스 만들어서 각각 비즈니스 로직과 데이터 접근에만 집중하게하기 (**데이터 변환 로직 관리)

- DBLOCK? 어떤 식으로? 
- 낙관적 잠금(Optimistic Lock)과 비관적 잠금(Perssimistic Lock) 차이 알기 
- Optimistic Lock (낙관적 잠금) : 데이터의 충돌이 드물다는 가정하에, 데이터 수정 시점에만 버전 검증, 트랜잭션 간 충돌이 발생하면 데이터 변경 허용하지 않고 예외 발생(롤백시킴, 재처리 로직 필요)
```angular2html
@Transactional
public void applyLecture(Long lectureId) {
    Lecture lectures = lectureRepository.findById(lectureId)
            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

    lectures.setCapacity(lectures.getCapacity() - 1);
    lectureRepository.save(lectures); // 버전 충돌 발생 시 OptimisticLockException 발생
}
```
- Perssimistic Lock(비관적 잠금) : 데이터의 동시성 문제를 방지하기 위해 데이터를 읽거나 수정할 때 데이터베이스에서 레코드를 잠금, 다른 트랜잭션 접근 시 대기하거나 예외 발생, 여러 트랜잭션이 서로 락을 기다리면 데드락 주의! 
```angular2html
@Transactional
public void applyLecture(Long lectureId) {
    Lecture lectures = entityManager.find(Lecture.class, lectureId, LockModeType.PESSIMISTIC_WRITE);
    if (lectures == null) {
        throw new IllegalArgumentException("강의를 찾을 수 없습니다.");
    }
}
```
[] 특강신청 API 작성
- 특정 userId 로 선착순으로 제공되는 특강을 신청하는 API 를 작성합니다.
- 동일한 신청자는 동일한 강의에 대해서 한 번의 수강 신청만 성공할 수 있습니다.
- 특강은 선착순 30명만 신청 가능합니다.
- 이미 신청자가 30명이 초과 되면 이후 신청자는 요청을 실패합니다.

[] 특강 신청 가능 목록 API
- 날짜별로 현재 신청 가능한 특강 목록을 조회하는 API 를 작성합니다.
- 특강의 정원은 30명으로 고정이며, 사용자는 각 특강에 신청하기 전 목록을 조회해 볼 수 있어야 합니다.

[] 특강 신청 완료 목록 조회 API
- 특정 userId 로 신청 완료된 특강 목록을 조회하는 API 를 작성합니다.
- 각 항목은 특강 ID 및 이름, 강연자 정보를 담고 있어야 합니다.
- 주의 사항 : 정확하게 30명의 사용자에게만 특강을 제공해야함 
- 같은 사용자에게 여러 번의 특강 슬롯이 제공되지 않도록 제한할 방법을 고민해보자.

[] 코드 리팩토링