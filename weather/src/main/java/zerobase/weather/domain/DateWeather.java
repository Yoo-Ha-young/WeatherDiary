package zerobase.weather.domain;


// 매일 날씨를 저장하는 클래스

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "date_weather")
@NoArgsConstructor
public class DateWeather {
    @Id
    private LocalDate date;

    private String weather;
    private String icon;

    private double temperature;
}

/*
* [매일 자정에 날씨데이터 불러오기]
* 캐싱이란? 데이터 등을 미리 복사해서 저장해두는 것
* 장점 : 한번 불러와진 데이터이므로 이후에 요청을 빠르게 처리해줄 수 있다.
*
* [캐싱방법]
 - 웹 브라우저에서 캐시
 - 서버에서 캐시
* Client<--(DTO)-->Controller<--(DTO)-->Service<--(DTO)-->Repository<--(Entity)-->DB
  같은 데이터가 들어온다면, DB 단까지 가지 않아도 컨트롤러 단이나 서비스 단에서 처리를 해주게 된다. 그렇게 되면 사용하는 사람의 응답속도도 높이고 서버의 부하 또한 줄일 수 있다.
*
* [캐싱할 때 유의할 점]
* 요청한 것에 대한 응답이 변하지 않을때만 사용할 수 있다!
* 2033년 3월 30일의 서울 날씨 예측값 -> 변할 수 있음(캐싱 불가능) = 미래, 예측을 할 수 없는 것
* 2022년 2월 22일의 서울 날씨 -> 변하지 않음(캐싱 가능) = 과거
*
*
* [캐싱 적용 전]
* 1. OpenWeatherMap에서 데이터 받아오기
* 2. 받아온 데이터(json) 사용 가능하게 파싱하기
* 3. 우리 DB에 저장하기
*
* 캐싱 적용 후 두번째 요청시 우리 DB에서 받아오기만 하면된다.
* 장점은 요청을 빠르게 처리해주고,
* 서버의 부하를 줄여주고 또는 사용료가 있을 시 절감이 된다.
*
*
* [날씨일기 프로젝트 캐싱 적용하기]
* 하루에 한번만 적용!
* 1. 매일 새벽 1시에 외부 api에서 전일 날씨 데이터 얻어오기
* 2. 우리 DB에 해당 데이터 저장해두기
* 3. 전일 날씨 데이터가 필요할때는 외부 api(X) 우리 DB(O)에서 얻어오기
*
*
* @Scheduled(cron"0 0 1 * * *")
  cron은 어떤 명령을 특정시간에만 수행시킬 때 쓰는 리눅스 명령어이다.

* 각각의 자리에 아래에 해당하는 순서대로 값을 써주고, 미기입시 *로 표시해준다.
*  second(0~59) minute(0~59) hour(0~23) day of the month(1~31)
* month(1~12 or JAN-DEC) day of the week(0~7 or MON-SUN -- 0 or 7 is Sunday)
*
* 0 0 * * * * : 매 시각
* /10 * * * * * : 10초 마다
* 0 0 8-10 * * * : 매일, 9시, 10시
*
*@Scheduled(cron="0 0 1 * * *")은 스프링 프레임워크에서 제공하는 스케줄링 기능으로, 주기적으로 메서드를 실행하기 위한 어노테이션입니다. 여기서 cron 속성값은 cron 표현식을 사용하여 실행 시간을 지정합니다.

cron 표현식은 다음과 같이 구성됩니다.
초(0-59) 분(0-59) 시(0-23) 일(1-31) 월(1-12) 요일(0-7)
각각의 구성 요소에 대해 지정 가능한 값은 다음과 같습니다.
초 (0-59)
분 (0-59)
시 (0-23)
일 (1-31)
월 (1-12)
요일 (0-7, 일요일부터 토요일까지 0~6, 7은 일요일)
여기서 *은 "모든 값"을 의미합니다. 따라서 위의 cron 표현식에서 * * * 1 * *는 매월 1일 0시 0분에 실행되도록 지정한 것입니다. 즉, "모든 초, 모든 분, 모든 시간, 1일의 모든 요일, 모든 월, 모든 요일"을 의미합니다.
*
*
*
*
* 5일 이상의 과거 날씨를 조회하는 것은 유료
* Scheduling을 써서 절약해보자
*
* 날씨를 저장하기 위한 테이블이 필요
*
*
* */
