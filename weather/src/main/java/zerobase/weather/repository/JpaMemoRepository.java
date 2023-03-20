package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

@Repository
// 이미 만들어져 있는 자바의 JpaRepository에서 기능들을 가져다 씀
//어떤 클래스를 쓸건지 : Memo, 어떤 키인지 Integer
public interface JpaMemoRepository extends JpaRepository<Memo, Integer> {
}

// 이렇게만 써두면 jparepository에서 가져왔기 때문에 save, finall 등의 메서드들을 쓸 수 있음
