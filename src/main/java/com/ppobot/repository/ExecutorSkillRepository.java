package com.ppobot.repository;

import com.ppobot.entity.ExecutorSkill;
import com.ppobot.entity.mapper.ExecutorSkillMapper;
import com.ppobot.exception.DbException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExecutorSkillRepository {
    protected final String schema = "sosedushka_db.";
    protected final static ExecutorSkillMapper EXECUTOR_SKILL_MAPPER = new ExecutorSkillMapper();
    protected final JdbcTemplate template;

    public ExecutorSkillRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public List<ExecutorSkill> getByExecutorId(String executorId) throws DbException {
        try {
            return template.query("select * from " + schema + "executorSkill where executorId=?", EXECUTOR_SKILL_MAPPER, executorId);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<ExecutorSkill> getBySkillId(int id) throws DbException {
        try {
            return template.query("select * from " + schema + "executorSkill where skillId=?", EXECUTOR_SKILL_MAPPER, id);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<ExecutorSkill> getAllExecutorSkill() throws DbException {
        try {
            return template.query("select * from " + schema + "executorSkill", EXECUTOR_SKILL_MAPPER);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public ExecutorSkill findByExIdAndSkillId(String executorId, int skillId) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "executorSkill where executorId=? and skillId=?",
                            EXECUTOR_SKILL_MAPPER, executorId, skillId));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<ExecutorSkill> getNotVerified() throws DbException {
        try {
            return template.query("select * from " + schema + "executorSkill where skillStatus='NOT_VERIFIED'", EXECUTOR_SKILL_MAPPER);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void insert(ExecutorSkill entity) throws DbException {
        try {
            var result = template.update("insert into " + schema + "executorSkill (executorId, skillId, " +
                            "skillStatus) values (?, ?, ?) ",
                    entity.getExecutorName(),
                    entity.getSkillId(),
                    entity.getSkillStatus().toString());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void update(ExecutorSkill entity) throws DbException {
        try {
            var result = template.update("update " + schema + "executorSkill set skillStatus=? where executorId=? and skillId=?",
                    entity.getSkillStatus().toString(),
                    entity.getExecutorName(),
                    entity.getSkillId());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void delete(ExecutorSkill entity) throws DbException {
        try {
            var result = template.update("delete from " + schema + "executorSkill where executorId=? and skillId=?",
                    entity.getExecutorName(),
                    entity.getSkillId());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }
}
