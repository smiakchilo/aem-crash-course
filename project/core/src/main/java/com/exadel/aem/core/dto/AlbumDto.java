package com.exadel.aem.core.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlbumDto {

    private final String title;

    private final ArtistDto artist;

    private final int year;

    private final List<TrackDto> tracks;

    public AlbumDto(String title, ArtistDto artist, int year, List<TrackDto> tracks) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.tracks = tracks;
    }

    public String getSlug() {
        String result = Stream.of(
                        artist.getSlug(),
                        StringUtils.defaultString(getTitle()).trim().replaceAll("[^\\w-]+", "-").toLowerCase()
                )
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("-"));
        if (getYear() > 0) {
            result += "-" + getYear();
        }
        return result;
    }

    public String getTitle() {
        return title;
    }

    public ArtistDto getArtist() {
        return artist;
    }

    public int getYear() {
        return year;
    }

    public List<TrackDto> getTracks() {
        return tracks;
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(getTitle()) && getArtist() != null && getArtist().isValid();
    }
}
