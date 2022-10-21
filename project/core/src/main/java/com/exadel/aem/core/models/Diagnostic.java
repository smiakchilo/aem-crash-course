package com.exadel.aem.core.models;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import org.apache.sling.models.factory.ModelFactory;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Diagnostic {

    @ValueMapValue(name = "sling:resourceType", injectionStrategy=InjectionStrategy.OPTIONAL)
    private String resourceType;

    @ValueMapValue
    @Default(values = "This is a placeholder for the empty text")
    private String text;

    @ChildResource
    private Resource child1;

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private ModelFactory modelFactory;

    @Inject
    @Via("resource")
    private int numericValue;

    public String getResourceType() {
        return resourceType;
    }

    public String getText() {
        return text;
    }

    public SlingHttpServletRequest getRequest() {
        return request;
    }

    public Resource getResource() {
        return resource;
    }

    public int getNumericValue() {
        return numericValue;
    }
}
