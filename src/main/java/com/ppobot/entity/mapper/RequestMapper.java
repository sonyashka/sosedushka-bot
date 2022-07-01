package com.ppobot.entity.mapper;

import com.ppobot.entity.Request;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestMapper implements RowMapper<Request> {
    @Override
    public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new Request(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getTimestamp("periodOfRelevance"),
                rs.getString("explanation"),
                rs.getInt("profNecessity"),
                rs.getInt("equipment"),
                rs.getString("owner"),
                rs.getString("executor"),
                Request.ReqStatus.valueOf(rs.getString("status"))
        );
        return entity;
    }
}