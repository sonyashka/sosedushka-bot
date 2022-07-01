package com.ppobot;

import com.ppobot.entity.ExecutorSkill;
import com.ppobot.entity.Skill;
import com.ppobot.entity.User;
import com.ppobot.repository.ExecutorSkillRepository;
import com.ppobot.repository.SkillRepository;
import com.ppobot.repository.UserRepository;
import com.ppobot.service.SkillService;
import com.ppobot.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.geom.Point2D;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userServ;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private SkillService skillServ;
    @Autowired
    private SkillRepository skillRepo;
    @Autowired
    private ExecutorSkillRepository exSkillRepo;

    @Test
    public void personalizeTest() {
        Point2D.Double geo = new  Point2D.Double(12.93, 1.294);
        User userAfter = userServ.personalizeUser("early", geo, true, false, false, false);
        Assertions.assertNotNull(userAfter);
    }

    @Test
    public void addRoleTest() {
        String tgName = "Alexey";
        User userBefore = userRepo.getByTgName(tgName);
        User userAfter = userServ.addRole(tgName, "administrator"); // has no admin earlier
        Assertions.assertNotSame(userBefore.isAdministratorRole(), userAfter.isAdministratorRole());
    }

    @Test
    public void sendOnVerificationTest() {
        String tgName = "___soft_IRQ_";
        String skillName = "программист";// no skill in db
        ExecutorSkill exSkillAfter = userServ.sendOnVerification(tgName, skillName);
        Assertions.assertSame(exSkillAfter.getVerified(), ExecutorSkill.SkillStatus.NOT_VERIFIED);
    }
}
