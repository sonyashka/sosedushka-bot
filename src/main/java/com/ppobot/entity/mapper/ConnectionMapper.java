package com.ppobot.entity.mapper;

import com.ppobot.entity.Connection;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionMapper implements RowMapper<Connection>{
    @Override
    public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new Connection(
                rs.getString("userTgName"),
                Connection.Roles.valueOf(rs.getString("currentRole"))
        );
        return entity;
    }
}
