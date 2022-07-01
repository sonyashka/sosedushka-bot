package com.ppobot.entity.mapper;

import com.ppobot.entity.Request;
import com.ppobot.entity.RequestForUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestForUserMapper implements RowMapper<RequestForUser> {
    @Override
    public RequestForUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new RequestForUser(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getTimestamp("periodOfRelevance"),
                rs.getString("explanation"),
                rs.getString("profNecessity"),
                rs.getString("equipment"),
                rs.getString("user"),
                RequestForUser.ReqStatus.valueOf(rs.getString("status"))
        );
        return entity;
    }
}