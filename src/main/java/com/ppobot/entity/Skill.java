package com.ppobot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Skill {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private final String name;
}
