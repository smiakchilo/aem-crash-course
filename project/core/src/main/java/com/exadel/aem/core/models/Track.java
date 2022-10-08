package com.exadel.aem.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Named;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class Track {

    @ValueMapValue
    @Named("jcr:title")
    private String title;

    @ValueMapValue
    private String duration;

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }
}
