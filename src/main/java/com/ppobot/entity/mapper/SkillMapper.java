package com.ppobot.entity.mapper;

import com.ppobot.entity.Skill;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SkillMapper  implements RowMapper<Skill> {
    @Override
    public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new Skill(
                rs.getInt("id"),
                rs.getString("name")
        );
        return entity;
    }
}
