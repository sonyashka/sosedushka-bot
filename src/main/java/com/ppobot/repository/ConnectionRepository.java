package com.ppobot.repository;

import com.ppobot.entity.Connection;
import com.ppobot.entity.mapper.ConnectionMapper;
import com.ppobot.exception.DbException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConnectionRepository {
    protected final String schema = "sosedushka_db.";
    protected final static ConnectionMapper CONNECTION_MAPPER = new ConnectionMapper();
    protected final JdbcTemplate template;

    public ConnectionRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public Connection getByUserTgName(String userTgName) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "connection where userTgName=?", CONNECTION_MAPPER, userTgName));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<Connection> getAllConnection(){
        try {
            return template.query("select * from " + schema + "connection", CONNECTION_MAPPER);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<Connection> getByCurrentRole(String role){
        try {
            return template.query("select * from " + schema + "connection where ?Role=true", CONNECTION_MAPPER, role);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void insert(Connection entity) throws DbException {
        try {
            var result = template.update("insert into " + schema + "connection (userTgName, currentRole) values (?, ?)",
                    entity.getUserTgName(),
                    entity.getCurrentRole().toString());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void update(Connection entity) throws DbException {
        try {
            var result = template.update("update " + schema + "connection set currentRole=? where userTgName=?",
                    entity.getCurrentRole().toString(),
                    entity.getUserTgName());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void delete(Connection entity) throws DbException {
        try {
            var result = template.update("update " + schema + "connection set currentRole=? where userTgName=?",
                    entity.getCurrentRole(),
                    entity.getUserTgName());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }
}
