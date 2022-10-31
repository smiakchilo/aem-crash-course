package com.exadel.aem.core.services;

import com.exadel.aem.core.utils.ThrowingConsumer;
import org.apache.sling.api.resource.ResourceResolver;

public interface ResourceResolverHost {

    void execute(ThrowingConsumer<ResourceResolver> routine) throws Exception;
}
