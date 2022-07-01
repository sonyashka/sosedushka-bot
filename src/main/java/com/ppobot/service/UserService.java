package com.ppobot.service;

import com.ppobot.entity.Connection;
import com.ppobot.entity.ExecutorSkill;
import com.ppobot.entity.Skill;
import com.ppobot.entity.User;
import com.ppobot.exception.NotConnectedException;
import com.ppobot.exception.NotFoundException;
import com.ppobot.exception.RoleException;
import com.ppobot.repository.ConnectionRepository;
import com.ppobot.repository.ExecutorSkillRepository;
import com.ppobot.repository.SkillRepository;
import com.ppobot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService extends BaseService {

    protected final UserRepository repo;
    protected final ConnectionRepository connectRepo;
    protected final SkillRepository skillRepo;
    protected final SkillService skillServ;
    protected final ExecutorSkillRepository exSkillRepo;

    public User personalizeUser(String tgName, Point2D.Double geoPosition, boolean cRole,
                                boolean eRole, boolean aRole, boolean aVerified) throws NotFoundException {
        User entity = repo.getByTgName(tgName);
        if (entity == null) {
            entity = new User(tgName, geoPosition, cRole, eRole, aRole, aVerified);
        } else {
            throw new NotFoundException();
        }
        repo.insert(entity);
        return entity;
    }

    public Connection getConnection(String tgName) throws NotFoundException, NotConnectedException {
        User entity = repo.getByTgName(tgName);
        if (entity == null) {
            throw new NotFoundException();
        }
        Connection connection = connectRepo.getByUserTgName(tgName);
        if (connection == null) {
            throw new NotConnectedException();
        }
        return connection;
    }

    public void registerConnection(String tgName, String currentRole) throws NotFoundException {
        User entity = repo.getByTgName(tgName);
        if (entity == null) {
            throw new NotFoundException();
        }
        Connection connection = new Connection(tgName, Connection.Roles.valueOf(currentRole));
        connectRepo.insert(connection);
    }

    public void changeConnection(String tgName, String nextRole) throws NotFoundException, RoleException {
        User entity = repo.getByTgName(tgName);
        if (entity == null) {
            throw new NotFoundException();
        }
        Connection connection = connectRepo.getByUserTgName(tgName);
        if (connection == null) {
            throw new NotConnectedException();
        }
        switch (nextRole) {
            case "customer":
                if (!entity.isCustomerRole()) {
                    throw new RoleException();
                }
                break;
            case "executor":
                if (!entity.isExecutorRole()) {
                    throw new RoleException();
                }
                break;
            default:
                if (!entity.isAdministratorRole() || !entity.isAdminVerified()) {
                    throw new RoleException();
                }
                break;
        }
        connection.setCurrentRole(Connection.Roles.valueOf(nextRole.toUpperCase()));
        connectRepo.update(connection);
    }

    public User changeGeoPosition(String tgName, Point2D.Double geoPosition) throws NotFoundException {
        User entity = repo.getByTgName(tgName);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.setGeoPosition(geoPosition);
        repo.update(entity);
        return entity;
    }

    public User addRole(String tgName, String role) throws NotFoundException {
        User entity = repo.getByTgName(tgName);
        if (entity == null) {
            throw new NotFoundException();
        }
        if (role.equals("customer")) {
            entity.setCustomerRole(true);
        }
        else if (role.equals("executor")) {
            entity.setExecutorRole(true);
        } else if (role.equals("administrator")) {
            entity.setAdministratorRole(true);
            entity.setAdminVerified(false);
        }
        repo.update(entity);
        return entity;
    }

    public ExecutorSkill sendOnVerification(String tgName, String skillName) throws NotFoundException, RoleException {
        User entity = repo.getByTgName(tgName);
        if (entity == null) {
            throw new NotFoundException();
        }
        if (!entity.isExecutorRole()) {
            throw new RoleException();
        }
        Skill skill = skillRepo.getByName(skillName);
        if (skill == null) {
            skillServ.addSkill(skillName);
            skill = skillRepo.getByName(skillName);
        }
        ExecutorSkill exSkill = new ExecutorSkill(tgName, skill.getId(), ExecutorSkill.SkillStatus.NOT_VERIFIED);
        exSkillRepo.insert(exSkill);
        return exSkill;
    }

    public List<ExecutorSkill> notVerifiedSkills(String tgName) throws NotFoundException, RoleException {
        User user = repo.getByTgName(tgName);
        if (user == null) {
            throw new NotFoundException();
        } else if (user.isAdministratorRole() && user.isAdminVerified()) {
            List<ExecutorSkill> exSkills = exSkillRepo.getNotVerified();
            if (exSkills == null) {
                throw new NotFoundException();
            }
            return exSkills;
        } else {throw new RoleException();} // добавить NotVerified exception (было бы здорово)
    }

    public List<User> notVerifiedAdmin(String tgName) throws NotFoundException, RoleException {
        User user = repo.getByTgName(tgName);
        if (user == null) {
            throw new NotFoundException();
        } else if (user.isAdministratorRole() && user.isAdminVerified()) {
            List<User> users = repo.getUserAdminNotVerified();
            if (users == null) {
                throw new NotFoundException();
            }
            return users;
        } else {throw new RoleException();}
    }

    public ExecutorSkill skillVerify(String tgName, String exTgName, int exSkillId) throws NotFoundException, RoleException {
        User user = repo.getByTgName(tgName);
        if (user == null) {
            throw new NotFoundException();
        } else if (user.isAdministratorRole() && user.isAdminVerified()) {
            ExecutorSkill exSkill = exSkillRepo.findByExIdAndSkillId(exTgName, exSkillId);
            exSkill.setSkillStatus(ExecutorSkill.SkillStatus.VERIFIED);
            exSkillRepo.update(exSkill);
            return exSkill;
        } else {throw new RoleException();}
    }

    public User adminVerify(String tgName, String adminTgName) throws NotFoundException, RoleException {
        User user = repo.getByTgName(tgName);
        if (user == null) {
            throw new NotFoundException();
        } else if (user.isAdministratorRole() && user.isAdminVerified()) {
            User adminUser = repo.getByTgName(adminTgName);
            adminUser.setAdminVerified(true);
            repo.update(adminUser);
            return adminUser;
        } else {throw new RoleException();}
    }
}
