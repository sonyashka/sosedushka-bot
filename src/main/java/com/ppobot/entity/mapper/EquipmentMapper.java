package com.ppobot.entity.mapper;

import com.ppobot.entity.Equipment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipmentMapper implements RowMapper<Equipment> {
    @Override
    public Equipment mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new Equipment(
                rs.getInt("id"),
                rs.getString("name")
        );
        return entity;
    }
}
