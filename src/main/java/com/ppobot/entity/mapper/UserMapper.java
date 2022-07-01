package com.ppobot.entity.mapper;

import com.ppobot.entity.User;
import org.springframework.jdbc.core.RowMapper;

import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Point2D.Double geo = new Point2D.Double(rs.getDouble("geoLat"),
                rs.getDouble("geoLong"));
        var entity = new User(
                rs.getString("tgName"),
                geo,
                rs.getBoolean("customerRole"),
                rs.getBoolean("executorRole"),
                rs.getBoolean("administratorRole"),
                rs.getBoolean("adminVerified")
        );
        return entity;
    }
}
