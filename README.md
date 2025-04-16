# 도서 등록 및 관리 백엔드 API 시스템

## 개요
- 학습한 DDD Layered architecture 를 최대한 적용해보며 레이어를 분할했습니다.
- 도메인 레이어는 POJO로 유지하는 방향으로 진행했습니다.
- JUnit을 활용해 classic, mockito 테스트 방식을 직접 작성해보며 테스트 방법에 대해 고민했습니다.
- 검색에 Spring-Redis 캐싱을 적용해보았습니다.
- 캐시 삭제시 Publisher 적용한 비동기 이벤트를 적용해 보았습니다. 

## **공수 및 개발내용**
- 총 개발기간 7일
- 백엔드 API 전체 개발
  
## **환경**
- Spring Boot 3.3.4
- Spring Security: Role 적용
- java 17
- JPA
- Redis 7.2: 도서 검색 캐싱
- Swagger 2.3: API 명세
- DB h2: mariaDB 버전 사용

## **도메인**

- Book: 도서
- Category: 카테고리
- BookCategory: 연관관계 도메인
- Rental: 대여
- User: 사용자, 대여자

## **서비스**

- 도서 관리(도서 등록, 카테고리 변경, 도서상태 변경)
- 도서 대여, 반납
- 도서 검색(제목, 지은이, 카테고리, ISBN 검색)

## **주안점**
- DDD Layered architecture 를 최대한 적용했습니다.
  - domain: 상태변경, 핵심로직
  - application: 도메인 응용 및 활용
  - infastruture: repository 활용, Redis 같은 외부 infra 연동
  - presentation: API 등 사용자 interface에 집중
  - Domain layer 를 POJO(Plain old java object)로 유지하여 애그리거트 독립성을 높였습니다.
- 상위 layer가 하위 layer를 참조하지 않도록 유지했습니다.
  - (domain → application → infrastructure → presentation)
- layer 간 의존 관계 최소화를 위해 중간 역할을 하는 객체를 사용했습니다.
  - **mapper**를 활용해 application(service) - infrastructure(repository) 레이어 간 소통
  - **DTO** 를 활용해 application(service) - presentation(External Interface) 레이어 간 소통
- 각 서비스의 테스트 코드를 작성했습니다.
- Junit 활용 - mockito, classic 방법 혼합
- Book은 상태값을 가지고 있기 때문에, quantity를 가진 종류가 아닌, 실물 도서 고유의 ID를 갖도록 했습니다.
같은 종류의 도서의 경우 ISBN 값을 동일하게 설계했습니다.
- BookCatetory 활용해 Book-Category 간 다대다 연관관계를 작성했습니다.
- 도서를 대여하고 반납하는 시나리오를 추가 요구사항인 도서 훼손, 분실 등 상태 변화 변경하도록 연계했습니다.
- jpql join 등을 활용해 불필요한 쿼리를 최소화하고자 노력했습니다.
- 조회용 Service 를 분리했습니다.
    - 조회용 서비스는 인증 받지 않은 Role의 요청을 허용합니다.
    - Redis(or local cache) 활용하여 캐싱하여 조회 성능을 높였습니다.
- 도서 업데이트 시, Cache Clear Event 발행(카테고리 변경, 도서상태 변경 시)하여 
- 보조 로직을 비동기로 처리하고 데이터 무결성을 유지했습니다.
- Dto, Service 를 통해 validation 을 촘촘하게 진행했습니다.
- RestControllerAdvice 를 통해 Exception에 대해 공통으로 처리했습니다.

## **느낀점**

**domain 을 pojo로 유지하며 장점은**

- 핵심 도메인 레이어가 독립되어 있어, 
새로운 기술의 도입 또는 변경 시에 사이드이펙트 고려할 지점이 적고, 사이드이펙트가 적어졌습니다.
- 상위를 참조하지 않고, 도메인에서부터 아래로 확장되는 구조로
핵심 로직의 파악이 비교적 쉽고, 명확한 장점이 있었습니다.

**domain 을 pojo로 유지하며 단점은**

- 코드 복잡도가 증가하고 코드량이 많아 생산성이 떨어졌습니다.
- jpa 연관관계 이점을 사용하기 어려워 조회를 위한 코드량 증가했으며,
복잡성이 조금만 올라가도 쿼리를 줄이기 위한 추가적인 방안을 고려해야했습니다.

## **테스트 관련**

- **InitData.java 를 통해 로컬환경 데이터 init**
    - CommandLineRunner 인터페이스 활용 초기데이터 세팅
    - 테스트 격리를 위해 JUnit Test 실행시 제외되도록 설정
- **테스트 케이스 작성**
    - JUnit mockito, classic 테스트 방식 복합 사용
    - 캐싱의 경우 Test에는 local 캐싱 사용
- **Swagger 활용하여 권한 및 API 명세, 테스트 가능**
    
    *(자세한 내용은 하단 swagger 참조)*

# **[API 문서: Swagger]**

- **권한(Role)에 따른 실행 가능한 API 갈음**
- **Swagger 어노테이션을 적극 활용하여 명세에서 원클릭 테스트 가능하도록 구성**

### **[접속 주소] - 어플리케이션 실행 후**

[**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

### **[Role 계정 정보]**

**ADMIN: 도서 관리, 도서 대여, 도서 검색**

- ID: admin@admin.com
- PW: admin

**USER: 도서 대여, 도서 검색**

- ID: user@user.com
- PW: user

**NO ROLE: 도서 검색**

# **[캐싱 - Redis]**

- **많이 사용될 것으로 예상되는 도서 조회 부분 캐싱**
- **docker-compose 활용**

  *** Redis 실행되지 않을 경우 로컬 캐시 실행하도록 예외 설정**

### **[redis 실행 명령어]**

cd {저장 경로}/bookstore/docker

docker-compose up -d

### **[redis 종료 명령어]**

cd {저장 경로}/bookstore/docker

docker-compose down

### **[Redis 접속, 캐싱 여부 확인 명령어]**

docker exec -it redis /bin/bash

(or docker exec -it redis bash)

redis-cli

keys *
