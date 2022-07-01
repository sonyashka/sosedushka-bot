package com.ppobot.repository;

import com.ppobot.entity.Skill;
import com.ppobot.entity.mapper.SkillMapper;
import com.ppobot.exception.DbException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SkillRepository {
    protected final String schema = "sosedushka_db.";
    protected final static SkillMapper SKILL_MAPPER = new SkillMapper();
    protected final JdbcTemplate template;

    public SkillRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public Skill getById(int id) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "skill where id=?", SKILL_MAPPER, id));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<Skill> getAllSkill() throws DbException {
        try {
            return template.query("select * from " + schema + "skill", SKILL_MAPPER);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public Skill getByName(String name) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "skill where name=?", SKILL_MAPPER, name));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void insert(Skill entity) throws DbException {
        try {
            var result = template.update("insert into " + schema + "skill (name) select distinct ?" +
                            "from " + schema + "skill where ? not in (select name from " + schema + "skill) ",
                    entity.getName(),
                    entity.getName());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void delete(Skill entity) throws DbException {
        try {
            var result = template.update("delete from " + schema + "skill where id=?", entity.getId());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }
}
