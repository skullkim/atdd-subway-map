package wooteco.subway.station;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class StationH2Dao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO STATION (name) VALUES (?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, station.getName());
    }

    @Override
    public List<Station> findAll() {
        String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, rowMapper());
    }

    @Override
    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM STATION WHERE id=?";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, rowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Station> findByName(String name) {
        String sql = "SELECT * FROM STATION WHERE name=?";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, rowMapper(), name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM STATION WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Station> rowMapper() {
        return (rs, rowNum) -> {
            return new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );
        };
    }
}