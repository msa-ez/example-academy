# SKAdemy

![image](https://user-images.githubusercontent.com/48303857/79727114-d3956280-8326-11ea-8862-e01ca9a0f949.png)

# 최종 조별과제 - 인터넷 강의수강 시스템

- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [최종 조별과제 - 인터넷 강의수강 시스템](#최종 조별과제 - 인터넷 강의수강 시스템)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현)
    - [DDD 의 적용](#ddd-의-적용)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  
# 서비스 시나리오

유데미, Learning Portal

기능적 요구사항
1. 학생이 강의를 선택하여 수강신청 한다
1. 학생이 결제한다
1. 수강신청이 되면 수강신청 내역이 강사의 강의시스템에 전달된다
1. 학생이 수강신청을 취소한다
1. 수강신청이 취소되면 결제가 취소된다
1. 강사가 강의를 개설한다
1. 강사가 개설된 강의를 취소한다
1. 강사가 강의를 취소하면 학생의 수강신청이 취소된다
1. 학생이 수강신청 내용을 조회한다
1. 강사가 강의수강 인원을 조회한다


비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않은 수강신청은 아예 신청이 성립되지 않아야 한다  Sync 호출 
1. 장애격리
    1. 강의 관리 기능이 수행되지 않더라도 수강신청은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 학생이 강의관리에서 확인할 수 있는 수강신청내용을 수강신청시스템(프론트엔드)에서 확인할 수 있어야 한다 CQRS


# 체크포인트

- 분석 설계


  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계

## TO-BE 조직 (Vertically-Aligned)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  http://msaez.io/#/storming/RYTliHDEOOYT0NAZ6Xoodg4HP3H3/every/18a58ddb3072e7c25041a1c9361a9635/-M5LUZ65cww2eehQz7RL



### 조직 및 요구사항 도출 도출
![image](https://user-images.githubusercontent.com/48303857/79729383-5cfa6400-832a-11ea-89b6-53eca4de1ab8.jpeg)

### 이벤트도출, 액터 커맨드 부착, 어그리게잇, 바운디드 컨텍스트로 묶기
![image](https://user-images.githubusercontent.com/48303857/79729452-74d1e800-832a-11ea-9b08-0d2807c69a28.jpeg)

    - 도메인 서열 분리 
        - Core Domain:  수강신청(front), 강의관리 : 핵심 서비스이며, 연간 Up-time SLA 수준을 99.999% 목표, 배포주기는 수강신청의 경우 1주일 1회 미만, 강의관리의 경우 1개월 1회 미만
        - Supporting Domain:   Dashboard : 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 60% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함.
        - General Domain:   결제 : 결제서비스로 3rd Party 외부 서비스를 사용하는 것이 경쟁력이 높음 (핑크색으로 이후 전환할 예정)

### 폴리시 부착

![image](https://user-images.githubusercontent.com/48303857/79729649-b4003900-832a-11ea-875f-c0e8dfc6ccb4.jpeg)

### 폴리시의 이동과 컨텍스트 매핑 (Blue는 Pub/Sub, Orange는 Req/Resp)

![image](https://user-images.githubusercontent.com/48303857/79729705-c67a7280-832a-11ea-828f-fc0cc5510e17.jpeg)

![image](https://user-images.githubusercontent.com/48303857/79729768-d72ae880-832a-11ea-9900-8e0e0e281d87.jpeg)

### 완성된 1차 모형

![image](https://user-images.githubusercontent.com/48303857/79729946-15c0a300-832b-11ea-8247-4e261f22690d.jpeg)

    - View Model 추가


### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

    - 학생이 강의를 선택하여 수강신청 한다 (ok)
    - 학생이 결제한다 (ok -sync)
    - 수강신청이 되면 수강신청 내역이 강사의 강의시스템에 전달된다 (ok - event driven)
    - 학생이 수강신청을 취소한다 (ok)
    - 수강신청이 취소되면 결제가 취소된다 (ok)
    - 강사가 강의를 개설한다 (ok)
    - 강사가 개설된 강의를 취소한다 (ok)
    - 강사가 강의를 취소하면 학생의 수강신청이 취소된다 (ok)
    - 학생이 수강신청 내용을 조회한다 (view)
    - 강사가 강의수강 인원을 조회한다 (view)

### 1차 모형에서 요구사항을 커버하도록 모델링됨

![image](https://user-images.githubusercontent.com/48303857/79814397-17d14300-83b9-11ea-8c7e-3517658dff13.png)


    - 강의 신청 시 결제처리 : 서비스는 강의를 제공하는 강사의 이익을 제공해야 하기 때문에 수강신청시 결제처리에 대해서는  Request-Response 방식 처리한다.
    - 강의 관리 기능은 서비스 제공의 측면이 강하며, 한 번 등록 시 여러명이 학생들이 수강신청을 하기 때문에 수강신청(Front)에 대해 강의관리 서비스는 Async (event-driven), Eventual Consistency 방식으로 처리한다.
    - 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다 Circuit breaker를 사용하여 
    - 학생이 강의관리에서 확인할 수 있는 수강신청내용을 수강신청시스템(프론트엔드)에서 확인할 수 있어야 한다 CQRS
    - 결제를 제외한 나머지 inter-microservice 트랜잭션: 모든 이벤트에 대해 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.    


## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/63028469/79846797-d3b26280-83f9-11ea-9ad7-a7e6b4bea18e.png)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd courseRegistrationSystem
mvn spring-boot:run

cd paymentSystem
mvn spring-boot:run 

cd lectureSystem
mvn spring-boot:run  
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 paymentSystem 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하였다. 모델링 시에 영문화 완료하였기 때문에 그대로 개발하는데 큰 지장이 없었다.

```
package skademy;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="PaymentSystem_table")
public class PaymentSystem {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long courseId;

    @PostPersist
    public void onPostPersist(){
        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PaymentCompleted paymentCompleted = new PaymentCompleted();
        BeanUtils.copyProperties(this, paymentCompleted);
        paymentCompleted.publish();
    }

    @PostRemove
    public void onPostRemove(){
        PaymentCanceled paymentCanceled = new PaymentCanceled();
        BeanUtils.copyProperties(this, paymentCanceled);
        paymentCanceled.publish();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package skademy;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface PaymentSystemRepository extends PagingAndSortingRepository<PaymentSystem, Long>{
}
```
- 적용 후 REST API 의 테스트
```
# courseRegistrationSystem 서비스의 수강신청 처리
http POST localhost:8081/courseRegistrationSystem lectureId=1
```
![image](https://user-images.githubusercontent.com/48303857/79857038-272bad00-8408-11ea-8096-7f54b482ea54.png)


```
# 주문 상태 확인
http localhost:8081/courseRegistrationSystem
```
![image](https://user-images.githubusercontent.com/48303857/79857153-4d514d00-8408-11ea-83be-cf9e002c9ce5.png)



## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 수강신청(courseRegistrationSystem)->결제(paymentSystem) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# (courseRegistrationSystem) PaymentService.java

@FeignClient(name ="paymentSystems", url="http://52.231.118.204:8080")
public interface PaymentService {
    @RequestMapping(method = RequestMethod.POST, value = "/paymentSystems", consumes = "application/json")
    void makePayment(PaymentSystem paymentSystem);

}
```

- 수강신청 직후(@PostPersist) 결제를 요청하도록 처리
```
#CourseRegistrationSystem.java (Entity)

    @PostPersist
    public void onPostPersist(){
        CourseRegistered courseRegistered = new CourseRegistered();
        BeanUtils.copyProperties(this, courseRegistered);
        courseRegistered.publish();

        this.setLectureId(courseRegistered.getLectureId());
        this.setStudentId(12334);
        this.setStatus("수강신청중");

        System.out.println("##### POST CourseRegistrationSystem 수강신청 : " + this);

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        PaymentSystem paymentSystem = new PaymentSystem();
        paymentSystem.setCourseId(this.id);
        // mappings goes here

        //결제 시작
        PaymentService paymentService = Application.applicationContext.getBean(PaymentService.class);
        paymentService.makePayment(paymentSystem);

    }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 주문도 못받는다는 것을 확인:


```
# 결제(paymentSystem) 서비스를 잠시 내려놓음

#수강신청 처리
http POST localhost:8081/courseRegistrationSystem lectureId=1   #Fail
http POST localhost:8081/courseRegistrationSystem lectureId=2   #Fail
```
![image](https://user-images.githubusercontent.com/48303857/79857341-9a352380-8408-11ea-908a-d776d192bb8e.png)

```
#결제서비스 재기동
cd paymentSystem
mvn spring-boot:run

#수강신청 처리
http POST localhost:8081/courseRegistrationSystem lectureId=1   #Success
http POST localhost:8081/courseRegistrationSystem lectureId=2   #Success
```
![image](https://user-images.githubusercontent.com/48303857/79857434-c05ac380-8408-11ea-88d4-8a6ce4af0100.png)


- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커, 폴백 처리는 운영단계에서 설명한다.)



## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트


결제가 이루어진 후에 수강신청시스템으로 이를 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여 수강신청 완료처리를 위하여 결제가 블로킹 되지 않도록 처리한다.
 
- 이를 위하여 결제시스템에 기록을 남긴 후에 곧바로 결제완료이 되었다는 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
...
    @PostPersist
    public void onPostPersist(){
   
        PaymentCompleted paymentCompleted = new PaymentCompleted();
        BeanUtils.copyProperties(this, paymentCompleted);
        paymentCompleted.publish();
    }

```
- 수강신청 서비스에서는 결제완료 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
public class PolicyHandler{
 ...
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCompleted_수강신청완료(@Payload PaymentCompleted paymentCompleted){
        try {
            if (paymentCompleted.isMe()) {
                System.out.println("##### listener 수강신청완료 : " + paymentCompleted.toJson());
                Optional<CourseRegistrationSystem> courseRegistrationSystemOptional = courseRegistrationSystemRepository.findById(paymentCompleted.getCourseId());
                CourseRegistrationSystem courseRegistrationSystem = courseRegistrationSystemOptional.get();
                courseRegistrationSystem.setStatus("결제 완료");
                courseRegistrationSystem.setStudentId(courseRegistrationSystem.getStudentId());

                courseRegistrationSystemRepository.save(courseRegistrationSystem);
            }
        }catch(Exception e) {

        }
    }

```
강의 시스템은 수강신청/결제와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 강의 시스템이 유지보수로 인해 잠시 내려간 상태라도 수강신청을 받는데 문제가 없다:
```
# 강의 서비스 (lectureSystem) 를 잠시 내려놓음

#수강신청 처리
http POST localhost:8081/courseRegistrationSystem lectureId=1   #Success
http POST localhost:8081/courseRegistrationSystem lectureId=2   #Success
```
![image](https://user-images.githubusercontent.com/48303857/79857884-6d354080-8409-11ea-9307-02288463bb13.png)

```
#수강신청 완료상태 까지 Event 진행확인
```
![image](https://user-images.githubusercontent.com/48303857/79857914-79b99900-8409-11ea-8658-030267f42214.png)
```
#강의 서비스 기동
cd lectureSystem
mvn spring-boot:run

#강의수강인원 Update 확인
콘솔창에서 확인
```
![image](https://user-images.githubusercontent.com/48303857/79857956-8f2ec300-8409-11ea-98dd-2dd3667855b5.png)

# 운영

## CI/CD 설정


각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 Azure를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 azure-pipeline.yml 에 포함되었다.

- devops를 활용하여 pipeline을 구성하였고, CI CD 자동화를 구현하였다.
![image](https://user-images.githubusercontent.com/18453570/79851343-2262fb00-8400-11ea-85e9-b4627f9a6d0d.PNG)

- 아래와 같이 pod 가 정상적으로 올라간 것을 확인하였다.
![image](https://user-images.githubusercontent.com/18453570/79851342-21ca6480-8400-11ea-914a-e80e14ea93c7.PNG)

- 아래와 같이 쿠버네티스에 모두 서비스로 등록된 것을 확인할 수 있다.
![image](https://user-images.githubusercontent.com/18453570/79851335-20993780-8400-11ea-988b-33018c526631.PNG)


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 수강신청(courseRegistration)-->결제(payment) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml

hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610

```

- 피호출 서비스(결제:payment) 의 임의 부하 처리 - 400 밀리에서 증감 220 밀리 정도 왔다갔다 하게
```
# (paymentSystem) PaymentSystem.java (Entity)

    @PostPersist
    public void onPostPersist(){  //결제이력을 저장한 후 적당한 시간 끌기

        ...
        
        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 120초 동안 실시


* 요청이 과도하여 CB를 동작함 요청을 차단
![image](https://user-images.githubusercontent.com/63028499/79851238-01020f00-8400-11ea-85fb-409dd5f9bfd6.PNG)

* 요청을 어느정도 돌려보내고나니, 기존에 밀린 일들이 처리되었고, 회로를 닫아 요청을 다시 받기 시작
![image](https://user-images.githubusercontent.com/63028499/79851242-01020f00-8400-11ea-9cc9-fdd639a91ed8.PNG)

* 이후 이러한 패턴이 계속 반복되면서 시스템은 도미노 현상이나 자원 소모의 폭주 없이 잘 운영됨
![image](https://user-images.githubusercontent.com/63028499/79851236-ffd0e200-83ff-11ea-9941-3e6038bbc89f.PNG)

- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 하지만, 66.62% 가 성공하였고, 33.38%가 실패했다는 것은 고객 사용성에 있어 좋지 않기 때문에 Retry 설정과 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.

- Availability 가 높아진 것을 확인 (siege)

### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 


- 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy pay --min=1 --max=10 --cpu-percent=15
```
- CB 에서 했던 방식대로 워크로드를 2분 동안 걸어준다.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://52.231.118.204:8080/courseRegistrationSystems POST {"lectureId": 1}'

```

- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
kubectl get deploy pay -w
```
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:
![image](https://user-images.githubusercontent.com/63028499/79851254-02cbd280-8400-11ea-9c75-4d60ce42d54d.PNG)

- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 
![image](https://user-images.githubusercontent.com/63028499/79851251-02cbd280-8400-11ea-96e7-ea092375e77d.PNG)

## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Readiness Probe 와 Autoscaler가 있는 상태에서 테스트를 진행함.
그 결과, 100%로 배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.
![image](https://user-images.githubusercontent.com/18453570/79856578-79b89980-8407-11ea-9daf-697365e0a388.PNG)

* 이후, Readiness와 Autoscaler를 제거한 상태에서 테스트를 진행하여 Availability의 변화를 확인함. 그 결과 20% 대로 떨어진 것을 확인할 수 있음.
![image](https://user-images.githubusercontent.com/18453570/79856571-79200300-8407-11ea-84a9-946f3a2a076d.PNG)





