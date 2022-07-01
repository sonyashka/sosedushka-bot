package com.ppobot.repository;

import com.ppobot.entity.ExecutorSkill;
import com.ppobot.entity.Request;
import com.ppobot.entity.RequestForUser;
import com.ppobot.entity.mapper.RequestForUserMapper;
import com.ppobot.entity.mapper.RequestMapper;
import com.ppobot.exception.DbException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RequestRepository {
    protected final String schema = "sosedushka_db.";
    protected final static RequestMapper REQUEST_MAPPER = new RequestMapper();
    protected final static RequestForUserMapper REQUEST_FOR_USER_MAPPER = new RequestForUserMapper();
    protected final JdbcTemplate template;

    public RequestRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public Request getById(int id) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "request where id=?", REQUEST_MAPPER, id));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<Request> getRequestList(){
        try {
            return template.query("select * from " + schema + "request", REQUEST_MAPPER);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<Request> getByProfNecessity(int profNecessity){
        try {
            return template.query("select * from " + schema + "request where profNecessity=?", REQUEST_MAPPER, profNecessity);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<Request> getByEquipment(int equipment){
        try {
            return template.query("select * from " + schema + "request where equipment=?", REQUEST_MAPPER, equipment);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<RequestForUser> getByOwner(String ownerTgName){
        try {
            return template.query("select r.id, title, periodOfRelevance, explanation, s.name as profNecessity, " +
                            "e.name as equipment, executor as user, " +
                            "status from " + schema + "request as r left join " + schema + "skill as s on r.profNecessity=s.id " +
                            "left join "
                            + schema +"equipment as e on r.equipment=e.id where owner=? and status not in (?, ?)",
                    REQUEST_FOR_USER_MAPPER, ownerTgName, Request.ReqStatus.DONE.toString(), Request.ReqStatus.ELAPSED.toString());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<RequestForUser> getByExecutor(String executorTgName){
        try {
            return template.query("select r.id, title, periodOfRelevance, explanation, s.name as profNecessity, " +
                            "e.name as equipment, owner as user, " +
                            "status from " + schema + "request as r left join " + schema + "skill as s on r.profNecessity=s.id " +
                            "left join "
                            + schema +"equipment as e on r.equipment=e.id where executor=? and status not in (?, ?)",
                    REQUEST_FOR_USER_MAPPER, executorTgName, Request.ReqStatus.DONE.toString(), Request.ReqStatus.ELAPSED.toString());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<RequestForUser> getByExecutorSkills(String executorTgName, double x, double y, double distance) {
        try {
            return template.query("select r.id, title, periodOfRelevance, explanation, exs.name as profNecessity," +
                            " e.name as equipment, " +
                    "owner as user, status " +
                    "from " + schema + "request as r left join " + schema + "equipment as e on r.equipment=e.id left join" +
                    "(select s.id, s.name from " + schema + "executorSkill as es join " + schema + "skill as s " +
                    "on es.skillId=s.id where executorId=? and es.skillStatus=?) as exs on r.profNecessity=exs.id join " + schema +
                    "user as u1 on r.owner=u1.tgName " +
                    "where r.status=? and r.owner <> ? and point(u1.geoLat, u1.geoLong) <-> point(?, ?) < ?",
                    REQUEST_FOR_USER_MAPPER, executorTgName, ExecutorSkill.SkillStatus.VERIFIED.toString(),
                    Request.ReqStatus.OPENED.toString(), executorTgName, x, y, distance / 111.111);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<Request> getByStatus(int status){
        try {
            return template.query("select * from " + schema + "request where status=?", REQUEST_MAPPER, status);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void insert(Request entity) throws DbException {
        try {
            var result = template.update("insert into " + schema + "request (title, periodOfRelevance, " +
                            "explanation, profNecessity, equipment, owner, executor, status) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    entity.getTitle(),
                    entity.getPeriodOfRelevance(),
                    entity.getExplanation(),
                    entity.getProfNecessity() == 0 ? null : entity.getProfNecessity(),
                    entity.getEquipment() == 0 ? null : entity.getEquipment(),
                    entity.getOwner(),
                    entity.getExecutor(),
                    entity.getStatus().toString());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void update(Request entity) throws DbException {
        try {
            var result = template.update("update " + schema + "request set title=?, periodOfRelevance=?, " +
                            "explanation=?, profNecessity=?, equipment=?, owner=?, executor=?, status=? where id=?",
                    entity.getTitle(),
                    entity.getPeriodOfRelevance(),
                    entity.getExplanation(),
                    entity.getProfNecessity() == 0 ? null : entity.getProfNecessity(),
                    entity.getEquipment() == 0 ? null : entity.getEquipment(),
                    entity.getOwner(),
                    entity.getExecutor(),
                    entity.getStatus().toString(),
                    entity.getId());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }
}
