package com.exadel.aem.core.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class AlbumList {

    @Self
    private SlingHttpServletRequest request;

    private List<String> items;

    @PostConstruct
    private void init() {
        List<Resource> albumResources = Optional.of(request)
                .map(SlingHttpServletRequest::getResourceResolver)
                .map(resolver -> resolver.adaptTo(PageManager.class))
                .map(pageManager -> pageManager.getContainingPage(request.getResource()))
                .map(page -> page.adaptTo(Resource.class))
                .flatMap(AlbumList::getChildren)
                .orElse(Collections.emptyList());
        items = albumResources
                .stream()
                .map(Resource::getPath)
                .collect(Collectors.toList());
    }

    public List<String> getItems() {
        return items;
    }

    private static Optional<List<Resource>> getChildren(Resource parent) {
        if (parent == null) {
            return Optional.empty();
        }
        Iterator<Resource> resourceIterator = parent.listChildren();
        Spliterator<Resource> resourceSpliterator = Spliterators.spliteratorUnknownSize(resourceIterator, 0);
        Stream<Resource> stream = StreamSupport.stream(resourceSpliterator, false);
        List<Resource> resourceList = stream
                .filter(res -> !JcrConstants.JCR_CONTENT.equals(res.getName()))
                .collect(Collectors.toList());
        return Optional.of(resourceList);
    }
}
