package com.ppobot.controller;

import com.ppobot.controller.check.RequestCheck;
import com.ppobot.entity.Request;
import com.ppobot.entity.RequestForUser;
import com.ppobot.exception.DataException;
import com.ppobot.exception.NotFoundException;
import com.ppobot.exception.RoleException;
import com.ppobot.exception.SkillException;
import com.ppobot.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestController {

    @Autowired
    private final RequestService requestService;
    @Autowired
    private RequestCheck requestCheck;

    public int createRequest(User user, String title, String periodOfRelevanceStr, String explanation,
                             String profNecessity, String equipment) {
        try {
            requestCheck.titleCheck(title);
            Timestamp periodOfRelevance = requestCheck.periodOfRelevanceCheck(periodOfRelevanceStr);
            requestService.createRequest(title, periodOfRelevance, explanation, profNecessity, equipment, user.getUserName());
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        }
    }

    public int changeTitle(User user, String reqIdStr, String newTitle) {
        try {
            int reqId = requestCheck.idCheck(reqIdStr);
            requestCheck.titleCheck(newTitle);
            requestService.changeTitle(user.getUserName(), reqId, newTitle);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException nfe) {
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public int changePeriodOfRelevance(User user, String reqIdStr, String newPeriodStr) {
        try {
            int reqId = requestCheck.idCheck(reqIdStr);
            Timestamp newPeriod = requestCheck.periodOfRelevanceCheck(newPeriodStr);
            requestService.changePeriodPfRelevance(user.getUserName(), reqId, newPeriod);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException nfe) {
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public int changeProfNecessity(User user, String reqIdStr, String newProfStr) {
        try {
            int reqId = requestCheck.idCheck(reqIdStr);
            requestService.changeProfNecessity(user.getUserName(), reqId, newProfStr);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException nfe) {
            return HttpStatus.SC_NOT_FOUND;
        } catch (SkillException se) {
            return HttpStatus.SC_CONFLICT;
        }
    }

    public int changeExplanation(User user, String reqIdStr, String newExplanation) {
        try {
            int reqId = requestCheck.idCheck(reqIdStr);
            requestService.changeExplanation(user.getUserName(), reqId, newExplanation);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException nfe) {
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public int changeEquipment(User user, String reqIdStr, String newEquipment) {
        try {
            int reqId = requestCheck.idCheck(reqIdStr);
            requestService.changeEquipment(user.getUserName(), reqId, newEquipment);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException nfe) {
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public int takeOnExecution(User user, String reqIdStr) {
        try {
            int reqId = requestCheck.idCheck(reqIdStr);
            requestService.takeOnExecution(user.getUserName(), reqId);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (RoleException re) {
            return HttpStatus.SC_METHOD_NOT_ALLOWED;
        } catch (NotFoundException nfe) {
            return HttpStatus.SC_NOT_FOUND;
        } catch (SkillException se) {
            return HttpStatus.SC_CONFLICT;
        }
    }

    public int done(User user, String  reqIdStr) {
        try {
            int reqId = requestCheck.idCheck(reqIdStr);
            requestService.done(user.getUserName(), reqId);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (RoleException re) {
            return HttpStatus.SC_METHOD_NOT_ALLOWED;
        } catch (NotFoundException nfe) {
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public int closeRequest(User user, String reqIdStr) {
        try {
            int reqId = requestCheck.idCheck(reqIdStr);
            requestService.closeRequest(user.getUserName(), reqId);
            return HttpStatus.SC_OK;
        } catch (DataException e) {
            return HttpStatus.SC_METHOD_FAILURE;
        } catch (NotFoundException ne) {
            return HttpStatus.SC_NOT_FOUND;
        }
    }

    public List<RequestForUser> requestsByOwner(User user) {
        try {
            List<RequestForUser> reqs = requestService.requestsByOwner(user.getUserName());
            return reqs;
        } catch (NotFoundException e) {
            return null;
        }
    }

    public List<RequestForUser> requestsByExecutor(User user) {
        try {
            List<RequestForUser> reqs = requestService.requestsByExecutor(user.getUserName());
            return reqs;
        } catch (NotFoundException e) {
            return null;
        }
    }

    public List<RequestForUser> requestsByExecutorSkills(User user, String distanceStr) {
        try {
            double distance = requestCheck.distanceCheck(distanceStr);
            List<RequestForUser> reqs = requestService.requestsByExecutorSkills(user.getUserName(), distance);
            return reqs;
        } catch (NotFoundException e) {
            return null;
        } catch (DataException de) {
            return null;
        }
    }
}
