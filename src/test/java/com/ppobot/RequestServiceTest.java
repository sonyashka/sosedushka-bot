package com.ppobot;

import com.ppobot.repository.RequestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.ppobot.entity.Request;
import com.ppobot.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

@SpringBootTest
public class RequestServiceTest {

    @Autowired
    private RequestService reqServ;
    @Autowired
    private RequestRepository reqRepo;

    @Test
    public void createRequestTest() {
        System.out.println(System.getProperty("spring.profiles.active"));
        Request reqAfter = reqServ.createRequest("title", Timestamp.valueOf("2022-07-21 19:01:06.0"),
                "tututu", "", "шуруповерт", "Yuri");
        Assertions.assertNotNull(reqAfter);
    }

    @Test
    public void updateTitleTest() {
        String newTitle = "lalala";
        Request reqBefore = new Request(1, "title", Timestamp.valueOf("2022-06-23 12:00:00.0"),
                "tututu", "", 0, "Yuri",  "", Request.ReqStatus.OPENED);
        reqRepo.insert(reqBefore);
        Request reqAfter = reqServ.changeTitle(reqBefore.getOwner(), reqBefore.getId(), newTitle);
        Assertions.assertEquals(reqAfter.getTitle(), newTitle);
    }

    @Test
    public void takeOnExecutionTest() {
        String skill = "сантехника";
        Request reqBefore = new Request(5, "title", Timestamp.valueOf("2022-06-23 12:00:00.0"),
                "tututu", skill, 0, "Yuri", "", Request.ReqStatus.OPENED);
        reqRepo.insert(reqBefore);
        Request reqAfter = reqServ.takeOnExecution("Alexey", reqBefore.getId()); // Alexey has no skill
        Assertions.assertEquals(reqBefore.getExecutor(), reqAfter.getExecutor());
    }
}
