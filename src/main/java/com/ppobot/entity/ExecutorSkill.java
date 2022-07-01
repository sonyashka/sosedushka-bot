package com.ppobot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecutorSkill {
    @JsonProperty("executorId")
    private String executorName;
    @JsonProperty("skillId")
    private int skillId;
    @JsonProperty("skillStatus")
    private SkillStatus skillStatus;

    public enum SkillStatus {
        VERIFIED,
        NOT_VERIFIED
    }
}
