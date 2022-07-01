package com.ppobot.entity.mapper;

import com.ppobot.entity.ExecutorSkill;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExecutorSkillMapper  implements RowMapper<ExecutorSkill> {
    @Override
    public ExecutorSkill mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new ExecutorSkill(
                rs.getString("executorId"),
                rs.getInt("skillId"),
                ExecutorSkill.SkillStatus.valueOf(rs.getString("skillStatus"))
        );
        return entity;
    }
}