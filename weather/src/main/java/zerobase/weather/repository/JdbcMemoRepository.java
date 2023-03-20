package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired // 이걸 넣어줘야 어플리케이션 프로퍼티에서 가져올 수 있음
    public JdbcMemoRepository(DataSource dataSource){

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo){
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    // 조회 기능
    public List<Memo> findAll(){
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    // Id 기준으로 조회
    public Optional<Memo> findById(int id){
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

    // 데이터를 가져오는 Memo 형태의 RowMapper
    private RowMapper<Memo> memoRowMapper(){
        // ResultSet 데이터 형식
        // {id = 1, text = 'this is memo~'}
        return (rs,roNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }
}
