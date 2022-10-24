package com.exadel.aem.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.exadel.aem.core.Constants;
import com.exadel.aem.core.dto.AlbumDto;
import com.exadel.aem.core.dto.ArtistDto;
import com.exadel.aem.core.dto.TrackDto;
import com.exadel.aem.core.services.AlbumSaver;
import com.exadel.aem.core.services.ResourceResolverHost;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.PathNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component(service = AlbumSaver.class)
public class AlbumSaverImpl implements AlbumSaver {

    private static final String ALBUM_PAGE_TEMPLATE = "/conf/sample-project/settings/wcm/templates/page-content";
    private static final String RESOURCE_TYPE_ALBUM = "sample-project/components/album";
    private static final String RESOURCE_TYPE_ARTIST = "sample-project/components/artist";

    @Reference
    private ResourceResolverHost resourceResolverHost;

    @Override
    public void save(List<AlbumDto> albums) throws Exception {
        resourceResolverHost.execute(resolver -> createAlbums(resolver, albums));
    }

    private static void createAlbums(ResourceResolver resolver, List<AlbumDto> albums)
            throws PathNotFoundException, PersistenceException, WCMException {
        Resource albumsFolder = resolver.getResource(Constants.ALBUMS_FOLDER);
        if (albumsFolder == null) {
            throw new PathNotFoundException("Node not found: " + Constants.ALBUMS_FOLDER);
        }
        Resource artistsFolder = resolver.getResource(Constants.ARTISTS_FOLDER);
        if (artistsFolder == null) {
            throw new PathNotFoundException("Node not found: " + Constants.ARTISTS_FOLDER);
        }

        for (AlbumDto album : albums) {
            if (album.isValid()) {
                createOrModifyAlbumPage(resolver, albumsFolder, artistsFolder, album);
                resolver.commit();
            }
        }
    }

    private static void createOrModifyAlbumPage(
            ResourceResolver resolver,
            Resource albumsFolder,
            Resource artistsFolder,
            AlbumDto album) throws WCMException, PersistenceException {
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            throw new WCMException("Could not retrieve PageManager");
        }

        Resource existingAlbum = albumsFolder.getChild(album.getSlug());
        if (existingAlbum == null) {
            Page albumPage = pageManager.create(
                    albumsFolder.getPath(),
                    album.getSlug(),
                    ALBUM_PAGE_TEMPLATE,
                    album.getTitle());
            Resource albumContentResource = createAlbumContentResource(resolver, albumPage, album);
            createAlbumTracks(resolver, albumContentResource, album);
            createArtistPage(resolver, artistsFolder, album.getArtist());
            return;
        }

        Optional.ofNullable(existingAlbum.getChild(Constants.ALBUM_RESOURCE_PATH))
                .map(res -> res.adaptTo(ModifiableValueMap.class))
                .ifPresent(vm -> vm.putAll(getAlbumValueMap(album)));
    }

    private static Resource createAlbumContentResource(ResourceResolver resolver, Page albumPage, AlbumDto album)
            throws PersistenceException {
        Resource container = resolver.getResource(albumPage.adaptTo(Resource.class), Constants.PAGE_CONTAINER_PATH);
        if (container == null) {
            throw new PersistenceException("Container resource not found at " + albumPage.getPath());
        }
        return resolver.create(container, "album", getAlbumValueMap(album));
    }

    private static Map<String, Object> getAlbumValueMap(AlbumDto album) {
        Map<String, Object> result = new HashMap<>();
        result.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        result.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, RESOURCE_TYPE_ALBUM);
        result.put(JcrConstants.JCR_TITLE, album.getTitle());
        if (album.getArtist() != null) {
            result.put("artistId", album.getArtist().getSlug());
        }
        if (album.getYear() > 0) {
            result.put("year", album.getYear());
        }
        if (album.getImage() != null) {
            result.put("imagePath", album.getImage());
        }
        return result;
    }

    private static void createAlbumTracks(ResourceResolver resolver, Resource albumResource, AlbumDto album)
            throws PersistenceException {
        if (CollectionUtils.isEmpty(album.getTracks())) {
            return;
        }
        Resource tracksRoot = resolver.create(
                albumResource,
                "tracks",
                Collections.singletonMap(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED));
        int trackIndex = 1;
        for (TrackDto track : album.getTracks()) {
            resolver.create(
                    tracksRoot,
                    "track_" + trackIndex++,
                    Map.of(
                            JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED,
                            JcrConstants.JCR_TITLE, track.getTitle(),
                            "duration", track.getDuration()));
        }
    }

    private static void createArtistPage(ResourceResolver resolver, Resource artistsFolder, ArtistDto artist)
            throws PersistenceException, WCMException {
        Resource existingArtist = artistsFolder.getChild(artist.getSlug());
        if (existingArtist != null) {
            return;
        }

        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            throw new WCMException("Could not retrieve PageManager");
        }

        Page artistPage = pageManager.create(
                artistsFolder.getPath(),
                artist.getSlug(),
                ALBUM_PAGE_TEMPLATE,
                artist.getName());
        createArtistContentResource(resolver, artistPage, artist);
    }

    private static void createArtistContentResource(ResourceResolver resolver, Page artistPage, ArtistDto artist)
            throws PersistenceException {
        Resource container = resolver.getResource(artistPage.adaptTo(Resource.class), Constants.PAGE_CONTAINER_PATH);
        if (container == null) {
            throw new PersistenceException("Container resource not found at " + artistPage.getPath());
        }
        resolver.create(container, "artist", getArtistValueMap(artist));
    }

    private static Map<String, Object> getArtistValueMap(ArtistDto artist) {
        return Map.of(
                JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED,
                JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, RESOURCE_TYPE_ARTIST,
                JcrConstants.JCR_TITLE, artist.getName(),
                JcrConstants.JCR_DESCRIPTION, artist.getDescription());
    }
}
