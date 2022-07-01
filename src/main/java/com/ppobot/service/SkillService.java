package com.ppobot.service;

import com.ppobot.entity.Skill;
import com.ppobot.exception.NotFoundException;
import com.ppobot.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillService extends BaseService {

    private final SkillRepository repo;

    public void addSkill(String name) {
        Skill entity = new Skill(0, name);
        repo.insert(entity);
    }

    public String getSkillName(int id) throws NotFoundException {
        String skillName = repo.getById(id).getName();
        if (skillName == null) {
            throw new NotFoundException();
        }
        return skillName;
    }
}
