package zerobase.weather.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "memo") // 테이블 이름
public class Memo {
    @Id //pk                                           // mysql의 ID값을 가져옴
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 값이 없으면 자동증가
    private int id;
    private String text;
}
