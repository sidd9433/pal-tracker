package io.pivotal.pal.tracker;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        KeyHolder generatedKayHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

            return statement;
        }, generatedKayHolder);

        return find(generatedKayHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        return jdbcTemplate.query(
                "SELECT id, project_id, user_id, date, hours from time_entries WHERE id=?",
                new Object[]{id},
                rs -> rs.next() ? rowMapper.mapRow(rs, 1) : null);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query(
                "SELECT id, project_id, user_id, date, hours from time_entries",
                rowMapper
        );
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        jdbcTemplate.update(
                "UPDATE time_entries SET project_id=?, user_id=?, date=?, hours=? WHERE id=?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours(),
                id);

        return find(id);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(
                "DELETE from time_entries WHERE id=?", id);
    }


    private final RowMapper<TimeEntry> rowMapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

}
