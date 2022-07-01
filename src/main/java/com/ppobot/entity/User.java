package com.ppobot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.geom.Point2D;

@Data
@AllArgsConstructor
public class User {
    @JsonProperty("tgName")
    private final String tgName;
    @JsonProperty("geoPosition")
    private Point2D.Double geoPosition;
    @JsonProperty("customerRole")
    private boolean customerRole;
    @JsonProperty("executorRole")
    private boolean executorRole;
    @JsonProperty("administratorRole")
    private boolean administratorRole;
    @JsonProperty("adminVerified")
    private boolean adminVerified;
}