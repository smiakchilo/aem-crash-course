package com.exadel.aem.core.dto;

import org.apache.commons.lang3.StringUtils;

public class ArtistDto {

    private final String name;

    private final String description;

    public ArtistDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return StringUtils.defaultString(getName()).trim().replaceAll("[^\\w-]+", "-").toLowerCase();
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(getName());
    }
}
