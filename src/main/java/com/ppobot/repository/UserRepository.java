package com.ppobot.repository;

import com.ppobot.entity.User;
import com.ppobot.entity.mapper.UserMapper;
import com.ppobot.exception.DbException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.List;

@Repository
public class UserRepository {
    protected final String schema = "sosedushka_db.";
    protected final static UserMapper USER_MAPPER = new UserMapper();
    protected final JdbcTemplate template;

    public UserRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public User getByTgName(String tgName) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "user where tgName=?", USER_MAPPER, tgName));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<User> getByGeoPosition(Point2D.Double geoPosition, float distance) throws DbException {
        try {
            return template.query("select * from " + schema + "user where (point(?, ?) <-> point(geoLong, geoLat)) <= ?",
                    USER_MAPPER, geoPosition.getX(), geoPosition.getY(), distance);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<User> getUserListByRole(String role) throws DbException {
        try {
            return template.query("select * from " + schema + "user where ?Role = true", USER_MAPPER, role);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<User> getUserAdminNotVerified() throws DbException {
        try {
            return template.query("select * from " + schema + "user where administratorRole = true and " +
                    "adminVerified=false", USER_MAPPER);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<User> getUserList() throws DbException {
        try {
            return template.query("select * from " + schema + "user", USER_MAPPER);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void insert(User entity) throws DbException {
        try {
            var result = template.update("insert into "+ schema + "user (tgName, geoLat, geoLong, customerRole, " +
                            "executorRole, administratorRole, adminVerified) values (?, ?, ?, ?, ?, ?, ?)",
                    entity.getTgName(),
                    entity.getGeoPosition().getY(),
                    entity.getGeoPosition().getX(),
                    entity.isCustomerRole(),
                    entity.isExecutorRole(),
                    entity.isAdministratorRole(),
                    entity.isAdminVerified());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void update(User entity) throws DbException {
        try {
            var result = template.update("update "+ schema + "user set geoLat=?, geoLong=?, customerRole=?, " +
                            "executorRole=?, administratorRole=?, adminVerified=? where tgName=?",
                    entity.getGeoPosition().getY(),
                    entity.getGeoPosition().getX(),
                    entity.isCustomerRole(),
                    entity.isExecutorRole(),
                    entity.isAdministratorRole(),
                    entity.isAdminVerified(),
                    entity.getTgName());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void delete(User entity) throws DbException {
        try {
            var result = template.update("delete from "+ schema + "user where tgName=?", entity.getTgName());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }
}
