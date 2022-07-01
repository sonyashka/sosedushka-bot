package com.ppobot.controller;

import com.ppobot.controller.check.UserCheck;
import com.ppobot.entity.Connection;
import com.ppobot.entity.ExecutorSkill;
import com.ppobot.exception.DataException;
import com.ppobot.exception.NotConnectedException;
import com.ppobot.exception.NotFoundException;
import com.ppobot.exception.RoleException;
import com.ppobot.service.SkillService;
import com.ppobot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.User;

import java.awt.geom.Point2D;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final SkillService skillService;
    @Autowired
    private UserCheck userCheck;

    public int personalizeUser(User user, String geoLatStr, String geoLongStr, String roleStr) {
        try {
            Point2D.Double geo = userCheck.geoCheck(geoLatStr, geoLongStr);
            boolean cRole = userCheck.roleCheck(roleStr, "customer");
            boolean eRole = userCheck.roleCheck(roleStr, "executor");
            boolean aRole = userCheck.roleCheck(roleStr, "administrator");
            userService.personalizeUser(user.getUserName(), geo, cRole, eRole, aRole, false);
            if (aRole) {
                return HttpStatus.SC_METHOD_NOT_ALLOWED;
            } else {
                userService.registerConnection(user.getUserName(), roleStr.toUpperCase());
                return HttpStatus.SC_OK;
            }
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException nfe) {
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public int changeGeo(User user, String geoLatStr, String geoLongStr) {
        try {
            Point2D.Double geo = userCheck.geoCheck(geoLatStr, geoLongStr);
            userService.changeGeoPosition(user.getUserName(), geo);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException nfe) { // user not personalized
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public int getConnection(User user) {
        try {
            Connection connection = userService.getConnection(user.getUserName());
            if (connection.getCurrentRole().equals(Connection.Roles.CUSTOMER)) {
                return 1;
            } else if (connection.getCurrentRole().equals(Connection.Roles.EXECUTOR)) {
                return 2;
            } else {
                return 3;
            }
        } catch (NotFoundException nfe) { // user not personalized
            return HttpStatus.SC_NOT_FOUND;
        } catch (NotConnectedException nce) { // user not connected
            return HttpStatus.SC_BAD_GATEWAY;
        }
    }

    public int addRole(User user, String roleStr) {
        try {
            boolean role = userCheck.roleCheck(roleStr, roleStr);
            userService.addRole(user.getUserName(), roleStr);
            Connection connection = userService.getConnection(user.getUserName());
//            if (connection == null && !roleStr.equals("administrator")) {
//                userService.registerConnection(user.getUserName(), roleStr.toUpperCase());
//            }
            if (roleStr.equals("administrator")) {
                return HttpStatus.SC_METHOD_NOT_ALLOWED;
            }
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException nfe) { // user not personalized
            return HttpStatus.SC_NOT_FOUND;
        } catch (NotConnectedException nce) { // user not connected
            if (!roleStr.equals("administrator")) {
                userService.registerConnection(user.getUserName(), roleStr.toUpperCase());
                return HttpStatus.SC_OK;
            } else {
                return HttpStatus.SC_METHOD_NOT_ALLOWED;
            }
        }
    }

    public int changeRole(User user, String roleStr) {
        try {
            boolean role = userCheck.roleCheck(roleStr, roleStr);
            userService.changeConnection(user.getUserName(), roleStr);
            return HttpStatus.SC_OK;
        } catch (NotFoundException nfe) { // user not personalized
            return HttpStatus.SC_NOT_FOUND;
        } catch (NotConnectedException nce) { // user not connected
            return HttpStatus.SC_BAD_GATEWAY;
        } catch (RoleException e) { // user has no such role
            return HttpStatus.SC_METHOD_NOT_ALLOWED;
        } catch (DataException de) { //
            return HttpStatus.SC_METHOD_FAILURE;
        }
    }

    public int sendOnVerification(User user, String skillStr) {
        try {
            if (getConnection(user) == 2) {
                userService.sendOnVerification(user.getUserName(), skillStr);
                return HttpStatus.SC_OK;
            } else {return HttpStatus.SC_METHOD_NOT_ALLOWED;}
        } catch (RoleException e) { // user has no executor role
            return HttpStatus.SC_METHOD_NOT_ALLOWED;
        } catch (NotFoundException nfe) { // user not personalized
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public List<ExecutorSkill> notVerifiedSkills(User user) {
        try {
            if (getConnection(user) == 3) {
                return userService.notVerifiedSkills(user.getUserName());
            } else {
                return null;
            }
        } catch (RoleException re) { // user has no administrator role
            return null;
        } catch (NotFoundException nfe) { // no skills to verify
            return null;
        }
    }

    public List<com.ppobot.entity.User> notVerifiedAdmin(User user) {
        try {
            if (getConnection(user) == 3) {
                return userService.notVerifiedAdmin(user.getUserName());
            } else {
                return null;
            }
        } catch (RoleException re) { // user has no administrator role
            return null;
        } catch (NotFoundException nfe) { // no skills to verify
            return null;
        }
    }

    public String skillNameOnId(int id) {
        try {
            return skillService.getSkillName(id);
        } catch (NotFoundException nfe) {
            return null;
        }
    }

    public int skillVerify(User user, String tgName, String skillIdStr) {
        try {
            userCheck.tgNameCheck(tgName);
            int skillId = userCheck.idCheck(skillIdStr);
            if (getConnection(user) == 3) {
                userService.skillVerify(user.getUserName(), tgName, skillId);
                return HttpStatus.SC_OK;
            } else {return HttpStatus.SC_METHOD_NOT_ALLOWED;}
        } catch (DataException e) {
            return  HttpStatus.SC_METHOD_FAILURE;
        }   catch (RoleException re) { // user has no administrator role
            return HttpStatus.SC_METHOD_NOT_ALLOWED;
        }
    }

    public int adminVerify(User user, String tgName) {
        try {
            userCheck.tgNameCheck(tgName);
            if (getConnection(user) == 3) {
                userService.adminVerify(user.getUserName(), tgName);
                return HttpStatus.SC_OK;
            } else {return HttpStatus.SC_METHOD_NOT_ALLOWED;}
        } catch (DataException e) {
            return  HttpStatus.SC_METHOD_FAILURE;
        }   catch (RoleException re) { // user has no administrator role
            return HttpStatus.SC_METHOD_NOT_ALLOWED;
        }
    }
}
