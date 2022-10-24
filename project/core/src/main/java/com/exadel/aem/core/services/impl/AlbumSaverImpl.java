package com.exadel.aem.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
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

    @Reference
    private ResourceResolverHost resourceResolverHost;

    @Override
    public void save(List<AlbumDto> albums) throws Exception {
        resourceResolverHost.execute(resolver -> createAlbums(resolver, albums));
    }

    private static void createAlbums(ResourceResolver resolver, List<AlbumDto> albums) throws PathNotFoundException,
            PersistenceException {
        Resource albumsFolder = resolver.getResource(Constants.ALBUMS_FOLDER);
        if (albumsFolder == null) {
            throw new PathNotFoundException("Node not found: " + Constants.ALBUMS_FOLDER);
        }
        Resource artistsFolder = resolver.getResource(Constants.ARTISTS_FOLDER);
        if (artistsFolder == null) {
            throw new PathNotFoundException("Node not found: " + Constants.ARTISTS_FOLDER);
        }

        for (AlbumDto album : albums) {
            if (!album.isValid()) {
                continue;
            }
            String albumSlug = album.getSlug();
            Resource existingAlbum = albumsFolder.getChild(album.getSlug());
            if (existingAlbum == null) {
                Resource albumResource = resolver.create(albumsFolder, albumSlug, getAlbumValueMap(album));
                createAlbumTracks(resolver, albumResource, album);
                createArtistResource(resolver, artistsFolder, album.getArtist());
            } else {
                ModifiableValueMap valueMap = existingAlbum.adaptTo(ModifiableValueMap.class);
                Optional.ofNullable(valueMap).ifPresent(vm -> vm.putAll(getAlbumValueMap(album)));
            }
            resolver.commit();
        }
    }

    private static Map<String, Object> getAlbumValueMap(AlbumDto album) {
        Map<String, Object> result = new HashMap<>();
        result.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
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

    private static void createAlbumTracks(ResourceResolver resolver, Resource albumResource, AlbumDto album) throws PersistenceException {
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

    private static void createArtistResource(ResourceResolver resolver, Resource artistsFolder, ArtistDto artist) throws PersistenceException {
        Resource existingArtist = artistsFolder.getChild(artist.getSlug());
        if (existingArtist != null) {
            return;
        }
        resolver.create(artistsFolder, artist.getSlug(), getArtistValueMap(artist));
    }

    private static Map<String, Object> getArtistValueMap(ArtistDto artist) {
        return Map.of(
                JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED,
                JcrConstants.JCR_TITLE, artist.getName(),
                JcrConstants.JCR_DESCRIPTION, artist.getDescription());
    }
}
