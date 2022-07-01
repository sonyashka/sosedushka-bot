package com.ppobot.repository;

import com.ppobot.entity.Equipment;
import com.ppobot.entity.mapper.EquipmentMapper;
import com.ppobot.exception.DbException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipmentRepository {
    protected final String schema = "sosedushka_db.";
    protected final static EquipmentMapper EQUIPMENT_MAPPER = new EquipmentMapper();

    protected final JdbcTemplate template;

    public EquipmentRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public Equipment getById(int id) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "equipment where id=?", EQUIPMENT_MAPPER, id));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public List<Equipment> getAllEquipment() throws DbException {
        try {
            return template.query("select * from " + schema + "equipment", EQUIPMENT_MAPPER);
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public Equipment getByName(String name) throws DbException {
        try {
            return DataAccessUtils.singleResult(
                    template.query("select * from " + schema + "equipment where name=?", EQUIPMENT_MAPPER, name));
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void insert(Equipment entity) throws DbException {
        try {
            var result = template.update("insert into " + schema + "equipment (name) select distinct ? " +
                            "from " + schema + "equipment where ? not in (select name from " + schema + "equipment)",
                    entity.getName(),
                    entity.getName());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }

    public void delete(Equipment entity) throws DbException {
        try {
            var result = template.update("delete from " + schema + "equipment where id=?",
                    entity.getId());
        } catch (DataAccessException exception) {
            throw new DbException(exception);
        }
    }
}
