package com.ppobot.service;

import com.ppobot.entity.*;
import com.ppobot.exception.NotFoundException;
import com.ppobot.exception.RoleException;
import com.ppobot.exception.SkillException;
import com.ppobot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService extends BaseService {

    @Autowired
    protected final RequestRepository repo;
    @Autowired
    protected final UserRepository userRepo;
    @Autowired
    protected final SkillRepository skillRepo;
    @Autowired
    protected final SkillService skillServ;
    @Autowired
    protected final ExecutorSkillRepository exSkillRepo;
    @Autowired
    protected final EquipmentRepository eqRepo;
    @Autowired
    protected final EquipmentSkillRepository eqSkillRepo;

    public Request createRequest(String title, Timestamp periodOfRelevance, String explanation,
                              String profNecessity, String equipmentName, String owner) {
        int skillId = 0;
        int equipmentId = 0;
        if (!profNecessity.equals("")) {
            Skill skill = skillRepo.getByName(profNecessity);
            if (skill == null) {
                skillServ.addSkill(profNecessity);
                skill = skillRepo.getByName(profNecessity);
                skillId = skill.getId();
            }
        }
        if (!equipmentName.equals("")) {
            Equipment eq = eqRepo.getByName(equipmentName);
            if (eq == null) {
                eqRepo.insert(new Equipment(0, equipmentName));
                eq = eqRepo.getByName(equipmentName);
                equipmentId = eq.getId();
            }
        }
//        if (skillId != 0 && equipmentId != 0) {
//            EquipmentSkill eqSkill = eqSkillRepo.findByEquipmentIdAndSkillId(skillId, equipmentId);
//        }
        Request entity = new Request(0, title, periodOfRelevance, explanation, skillId, equipmentId,
                owner, null, Request.ReqStatus.OPENED);
        repo.insert(entity);
        return entity;
    }

    public Request changeTitle(String tgName, int id, String title) throws NotFoundException {
        Request entity = repo.getById(id);
        if (entity == null || !entity.getOwner().equals(tgName)) {
            throw new NotFoundException();
        }
        entity.setTitle(title);
        repo.update(entity);
        return entity;
    }

    public Request changePeriodPfRelevance(String tgName, int id, Timestamp period) throws NotFoundException {
        Request entity = repo.getById(id);
        if (entity == null || !entity.getOwner().equals(tgName)) {
            throw new NotFoundException();
        }
        entity.setPeriodOfRelevance(period);
        repo.update(entity);
        return entity;
    }

    public Request changeProfNecessity(String tgName, int id, String profNecessity) throws NotFoundException {
        Request entity = repo.getById(id);
        if (entity == null || !entity.getOwner().equals(tgName)) {
            throw new NotFoundException();
        }
        if (entity.getStatus() == Request.ReqStatus.OPENED) {
            if (!profNecessity.equals("")) {
                Skill skill = skillRepo.getByName(profNecessity);
                if (skill == null) {
                    skillServ.addSkill(profNecessity);
                    skill = skillRepo.getByName(profNecessity);
                }
                entity.setProfNecessity(skill.getId());
            } else {
                entity.setProfNecessity(0);
            }
            repo.update(entity);
        } else {
            throw new SkillException();
        }
        return entity;
    }

    public Request changeExplanation(String tgName, int id, String explanation) throws NotFoundException {
        Request entity = repo.getById(id);
        if (entity == null || !entity.getOwner().equals(tgName)) {
            throw new NotFoundException();
        }
        entity.setExplanation(explanation);
        repo.update(entity);
        return entity;
    }

    public Request changeEquipment(String tgName, int id, String equipmentName) throws NotFoundException {
        Request entity = repo.getById(id);
        if (entity == null || !entity.getOwner().equals(tgName)) {
            throw new NotFoundException();
        }
        if (!equipmentName.equals("")) {
            Equipment eq = eqRepo.getByName(equipmentName);
            if (eq == null) {
                eqRepo.insert(new Equipment(0, equipmentName));
                eq = eqRepo.getByName(equipmentName);
            }
            entity.setEquipment(eq.getId());
        }
        else {
            entity.setEquipment(0);
        }
        repo.update(entity);
        return entity;
    }

    public Request takeOnExecution(String userTgName, int requestId) throws RoleException, NotFoundException, SkillException {
        User user = userRepo.getByTgName(userTgName);
        if (user == null || !user.isExecutorRole()) {
            throw new RoleException();
        }
        Request req = repo.getById(requestId);
        if (req == null || !req.getStatus().equals(Request.ReqStatus.OPENED)) {
            throw new NotFoundException();
        }
        Skill skill = skillRepo.getById(req.getProfNecessity());
        int skillId = skill == null ? 0 : skill.getId();
        if (user.isExecutorRole()) {
            if (skillId != 0) {
                ExecutorSkill exSkill = exSkillRepo.findByExIdAndSkillId(userTgName, skillId);
                if (exSkill != null) {
                    req.setExecutor(userTgName);
                    req.setStatus(Request.ReqStatus.IN_PROCESS);
                    repo.update(req);
                } else {
                    throw new SkillException();
                }
            } else {
                req.setExecutor(userTgName);
                req.setStatus(Request.ReqStatus.IN_PROCESS);
                repo.update(req);
            }
        }
        return req;
    }

    public Request done(String userTgName, int requestId) throws RoleException, NotFoundException, SkillException {
        User user = userRepo.getByTgName(userTgName);
        if (user == null || !user.isExecutorRole()) {
            throw new RoleException();
        }
        Request req = repo.getById(requestId);
        if (req == null) {
            throw new NotFoundException();
        }
        if (req.getExecutor().equals(userTgName) && req.getStatus().equals(Request.ReqStatus.IN_PROCESS)) {
            req.setStatus(Request.ReqStatus.DONE);
            repo.update(req);
        } else {
            throw new NotFoundException();
        }
        return req;
    }

    public Request isElapsed(int requestId) {
        Request req = repo.getById(requestId);
        Timestamp current = new Timestamp(System.currentTimeMillis());
        if (req.getPeriodOfRelevance().after(current)) {
            req.setStatus(Request.ReqStatus.ELAPSED);
            repo.update(req);
        }
        return req;
    }

    public Request closeRequest(String userTgName, int requestId) throws NotFoundException {
        Request req = repo.getById(requestId);
        if (req == null || !req.getOwner().equals(userTgName)) {
            throw new NotFoundException();
        }
        if (req.getStatus().equals(Request.ReqStatus.OPENED)) {
            req.setStatus(Request.ReqStatus.CLOSED);
            repo.update(req);
        } else {
            throw new NotFoundException();
        }
        return req;
    }

    public Request doneRequest(String userTgName, int requestId) throws NotFoundException {
        Request req = repo.getById(requestId);
        if (req == null || !req.getExecutor().equals(userTgName)) {
            throw new NotFoundException();
        }
        req.setStatus(Request.ReqStatus.DONE);
        repo.update(req);
        return req;
    }

    public List<RequestForUser> requestsByOwner(String userTgName) throws NotFoundException {
        List<RequestForUser> reqs = repo.getByOwner(userTgName);
        if (reqs == null) {
            throw new NotFoundException();
        }
        return reqs;
    }

    public List<RequestForUser> requestsByExecutor(String userTgName) throws NotFoundException {
        List<RequestForUser> reqs = repo.getByExecutor(userTgName);
        if (reqs == null) {
            throw new NotFoundException();
        }
        return reqs;
    }


    public List<RequestForUser> requestsByExecutorSkills(String userTgName, double distance) throws NotFoundException {
        User user = userRepo.getByTgName(userTgName);
        List<RequestForUser> reqs = repo.getByExecutorSkills(userTgName, user.getGeoPosition().x, user.getGeoPosition().y, distance);
        if (reqs == null) {
            throw new NotFoundException();
        }
        return reqs;
    }
}
