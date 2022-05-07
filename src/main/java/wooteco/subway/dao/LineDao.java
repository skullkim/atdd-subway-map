package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public LineDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate, DataSource dataSource) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());
        final Long id = simpleInsert.executeAndReturnKey(params).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        final String sql = "select id, name, color from LINE";
        return namedParameterJdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new Line(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("color"));
        });
    }

    public Line findById(Long id) {
        final String sql = "select id, name, color from LINE where id = :id";
        SqlParameterSource parameter = new MapSqlParameterSource(Map.of("id", id));
        return namedParameterJdbcTemplate.queryForObject(sql, parameter, (resultSet, rowNum) -> {
            return new Line(resultSet.getLong("id"), resultSet.getString("name"),
                    resultSet.getString("color"));
        });
    }

    public void update(Long id, LineRequest lineRequest) {
        final String sql = "update LINE set name = :name, color = :color where id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());
        params.put("id", id);
        SqlParameterSource parameter = new MapSqlParameterSource(params);
        namedParameterJdbcTemplate.update(sql, parameter);
    }

    public void deleteById(Long id) {
        final String sql = "delete from LINE where id = :id";
        namedParameterJdbcTemplate.update(sql, Map.of("id", id));
    }

}