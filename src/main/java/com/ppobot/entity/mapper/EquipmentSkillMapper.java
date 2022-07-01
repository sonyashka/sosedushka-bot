package com.ppobot.entity.mapper;

import com.ppobot.entity.EquipmentSkill;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipmentSkillMapper implements RowMapper<EquipmentSkill> {
    @Override
    public EquipmentSkill mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new EquipmentSkill(
                rs.getInt("equipmentId"),
                rs.getInt("skillId")
        );
        return entity;
    }
}
