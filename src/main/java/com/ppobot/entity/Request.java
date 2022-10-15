package com.ppobot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Request {
    @JsonProperty("id")
    private int id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("periodOfRelevance")
    private Timestamp periodOfRelevance;
    @JsonProperty("explanation")
    private String explanation;
    @JsonProperty("profNecessity")
    private int profNecessity;
    @JsonProperty("equipment")
    private int equipment;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("executor")
    private String executor;
    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    private ReqStatus status;

    public enum ReqStatus {
        OPENED,
        IN_PROCESS,
        DONE,
        CLOSED,
        ELAPSED,
        BANNED
    }
}
