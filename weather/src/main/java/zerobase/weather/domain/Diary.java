package zerobase.weather.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity// 테이블 이름
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Diary {
    @Id //pk                                           // mysql의 ID값을 가져옴
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 값이 없으면 자동증가
    private int id;
    private String weather;
    private String icon;
    private double temperature;
    private String text;
    private LocalDate date;

    public void setDateWeather(DateWeather dateWeather){
        this.date = dateWeather.getDate();
        this.weather = dateWeather.getWeather();
        this.icon = dateWeather.getIcon();
        this.temperature = dateWeather.getTemperature();
    }


}
//create table diary(
//	id INT not null primary key auto_increment,
//	weather VARCHAR(50) not null,
//	icon VARCHAR(50) not null,
//	temperature DOUBLE not null,
//	text VARCHAR(500) not null,
//	date DATE not NULL
//);
