package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.DateWeather;

import java.time.LocalDate;
import java.util.List;

@Repository                                                // 사용할 엔터티, Id(Pk)의 형식은 LocalDate
public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {
    // 리스트 Date Weather 에서 날짜로 날씨 데이터를 찾아주는 메서드
    List<DateWeather> findAllByDate(LocalDate localDate);
}
