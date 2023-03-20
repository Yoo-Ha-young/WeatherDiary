package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository // Diary 객체, id=int형
public interface DiaryRepository extends JpaRepository<Diary, Integer>{
    // findAllByDate를 자동완성

    List<Diary> findAllByDate(LocalDate date);
    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
    Diary getFirstByDate(LocalDate date); // First가 가장 첫번째를 가져오게 해줌

    @Transactional
    void deleteAllByDate(LocalDate date); // date에 해당하는 모든 데이터를 지워준다
}
/*
Spring Data JPA는 메소드 이름의 패턴을 분석하여 해당 메소드와 연결되는
쿼리를 자동으로 생성합니다. 이러한 쿼리 메소드를 사용하면 일반적인 CRUD 작업을
수행하는 데 필요한 메소드를 쉽게 생성할 수 있습니다.

  Spring Data JPA에서 쿼리 메소드 이름은 다음과 같은 규칙을 따릅니다.

1. 메소드 이름은 "find", "read", "get", "count", "query"와 같은 검색 동사로 시작합니다.

2. 이어지는 단어는 검색 대상 엔티티의 필드 이름이나 필드와 관련된 조건 키워드로 이루어집니다. 대소문자를 구분하지 않습니다.

3. 조건 키워드는 "By"로 시작하며, 이어지는 단어는 해당 필드의 이름이나 연산자입니다.

4. 필드 이름이나 조건 키워드 뒤에는 "And", "Or" 등의 논리 연산자를 추가하여 여러 필드를 조합할 수 있습니다.

5. 메소드 이름의 끝에는 반환 타입을 나타내는 단어를 추가합니다.
  예를 들어, "findByTitle"은 "title" 필드를 검색하고, "findByTitleAndAuthor"는 "title"과 "author" 필드를 모두 검색합니다.

 또한, Spring Data JPA는 다양한 연산자를 지원합니다.
 예를 들어, "findByTitleContaining"은 "title" 필드에서 특정 문자열이 포함된
 모든 엔티티를 찾으며, "findByTitleIsNotNull"은 "title" 필드가 null이 아닌 모든 엔티티를 찾습니다.

 Spring Data JPA는 이러한 규칙에 따라 메소드 이름을 분석하여 적절한 쿼리를
 자동으로 생성합니다. 따라서 개발자는 별도의 쿼리를 작성하지 않아도
 쉽게 CRUD 작업을 수행할 수 있습니다.
  */
