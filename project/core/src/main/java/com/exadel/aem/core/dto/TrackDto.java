package com.exadel.aem.core.dto;

public class TrackDto {

    private final String title;

    private final String duration;

    public TrackDto(String title, String duration) {
        this.title = title;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }
}
