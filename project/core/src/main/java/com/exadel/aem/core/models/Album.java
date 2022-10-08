package com.exadel.aem.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class Album {

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resolver;

    @OSGiService
    private ModelFactory modelFactory;

    @ValueMapValue(name = "jcr:title")
    private String title;

    @ValueMapValue
    private int year;

    @ValueMapValue
    private String imagePath;

    @ValueMapValue
    private String artistId;
    private Artist artist;

    private List<Track> tracks;

    @PostConstruct
    private void init() {
        artist = Optional.ofNullable(artistId)
                .map(id -> resolver.getResource("/content/sample-project/us/en/artists/" + id))
                .map(res -> modelFactory.getModelFromWrappedRequest(request, res, Artist.class))
                .orElse(null);
        Resource tracksResource = resource.getChild("tracks");
        if (tracksResource != null) {
            tracks = StreamSupport.stream(tracksResource.getChildren().spliterator(), false)
                    .map(trackResource -> trackResource.adaptTo(Track.class))
                    .collect(Collectors.toList());
        } else {
            tracks = Collections.emptyList();
        }
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Artist getArtist() {
        return artist;
    }

    public List<Track> getTracks() {
        return tracks;
    }
}
