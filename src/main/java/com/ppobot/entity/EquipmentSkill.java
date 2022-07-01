package com.ppobot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentSkill {
    @JsonProperty("equipmentId")
    private int equipmentId;
    @JsonProperty("skillId")
    private int skillId;
}
