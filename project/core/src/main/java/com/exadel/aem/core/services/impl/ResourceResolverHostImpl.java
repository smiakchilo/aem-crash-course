package com.exadel.aem.core.services.impl;

import com.exadel.aem.core.services.ResourceResolverHost;
import com.exadel.aem.core.utils.ThrowingConsumer;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Component
public class ResourceResolverHostImpl implements ResourceResolverHost {

    private static final Map<String, Object> AUTHENTICATION_INFO = Collections.singletonMap(
            ResourceResolverFactory.SUBSERVICE,
            "core-service");

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(ThrowingConsumer<ResourceResolver> routine) throws Exception {
        try (ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(AUTHENTICATION_INFO)) {
            routine.accept(resolver);
        }
    }
}
