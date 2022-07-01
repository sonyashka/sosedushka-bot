package com.ppobot.repository;

import com.ppobot.entity.Equipment;
import com.ppobot.entity.EquipmentSkill;
import com.ppobot.entity.ExecutorSkill;
import com.ppobot.entity.mapper.EquipmentSkillMapper;
import com.ppobot.exception.DbException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipmentSkillRepository {
    protected final String schema = "sosedushka_db.";
    protected final static EquipmentSkillMapper EQUIPMENT_SKILL_MAPPER = new EquipmentSkillMapper();
    protected final JdbcTemplate template;

    public EquipmentSkillRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public List<EquipmentSkill> getByEquipmentId(int equipmentId) throws DbException {
        try {
            return template.query("select * from " + schema + "equipmentSkill where equipmentId=?", EQUIPMENT_SKILL_MAPPER, equipmentId);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<EquipmentSkill> getBySkillId(int skillId) throws DbException {
        try {
            return template.query("select * from " + schema + "equipmentSkill where skillId=?", EQUIPMENT_SKILL_MAPPER, skillId);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public EquipmentSkill findByEquipmentIdAndSkillId(int equipmentId, int skillId) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "equipmentSkill where " +
                    "equipmentId=? and skillId=?", EQUIPMENT_SKILL_MAPPER, equipmentId, skillId));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void insert(EquipmentSkill entity) throws DbException {
        try {
            var result = template.update("insert into " + schema + "equipmentSkill (equipmentId, skillId) values (?, ?) ",
                    entity.getEquipmentId(),
                    entity.getSkillId());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void delete(EquipmentSkill entity) throws DbException {
        try {
            var result = template.update("delete from " + schema + "equipmentSkill where equipmentId=? and skillId=?",
                    entity.getEquipmentId(),
                    entity.getSkillId());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }
}
