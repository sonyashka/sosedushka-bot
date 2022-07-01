package com.ppobot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Connection {
    @JsonProperty("userTgName")
    private final String userTgName;
    @JsonProperty("currentRole")
    private Roles currentRole;

    public enum Roles {
        CUSTOMER,
        EXECUTOR,
        ADMINISTRATOR
    }
}
