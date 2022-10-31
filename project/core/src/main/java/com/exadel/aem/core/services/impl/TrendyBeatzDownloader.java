package com.exadel.aem.core.services.impl;

import com.exadel.aem.core.dto.AlbumDto;
import com.exadel.aem.core.dto.ArtistDto;
import com.exadel.aem.core.dto.TrackDto;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component(immediate = true)
@Designate(ocd = TrendyBeatzDownloader.Config.class)
public class TrendyBeatzDownloader implements Supplier<List<AlbumDto>> {

    private static final Logger LOG = LoggerFactory.getLogger(TrendyBeatzDownloader.class);

    private static final int LINKS_LIMIT = 10;

    private String listUrl;
    private Pattern linkPattern;
    private Pattern trackLinkPattern;
    private int timeout;

    @Override
    public List<AlbumDto> get() {
        try {
            Document document = Jsoup.connect(listUrl).timeout(timeout).get();
            Elements albumLinks = document.getElementsByAttributeValueMatching("href", linkPattern);
            return albumLinks
                    .stream()
                    .limit(LINKS_LIMIT)
                    .map(element -> element.attr("href"))
                    .map(this::getAlbum)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("Could not retrieve album list at {}", listUrl, e);
        }
        return Collections.emptyList();
    }

    private AlbumDto getAlbum(String link) {
        Document document;
        try {
            document = Jsoup.connect(link).timeout(timeout).get();
        } catch (IOException e) {
            LOG.error("Could not retrieve album details at {}", link, e);
            return null;
        }

        String albumTitle = getTitle(document);
        String artistName = getArtistName(document);
        int albumYear = getYear(document);
        String albumImage = getImagePath(document);

        return StringUtils.isNoneEmpty(albumTitle, artistName)
                ? new AlbumDto(
                        albumTitle,
                        new ArtistDto(artistName, StringUtils.EMPTY),
                        albumYear,
                        albumImage,
                        getAlbumTracks(document))
                : null;
    }

    private String getTitle(Document document) {
        return Optional
                .of(document.getElementsMatchingOwnText("Title:"))
                .map(Elements::first)
                .map(Element::parent)
                .map(elt -> elt.getElementsByTag("span"))
                .map(Elements::first)
                .map(Element::text)
                .orElse(null);
    }

    private String getArtistName(Document document) {
        return Optional
                .ofNullable(document.getElementById("artistname"))
                .map(elt -> elt.getElementsByTag("span"))
                .map(Elements::text)
                .orElse(null);
    }

    private int getYear(Document document) {
        return Optional
                .of(document.getElementsMatchingOwnText("Year:"))
                .map(Elements::first)
                .map(Element::parent)
                .map(elt -> elt.getElementsByTag("a"))
                .map(Elements::first)
                .map(Element::text)
                .flatMap(text -> Arrays.stream(StringUtils.split(text, " ")).filter(StringUtils::isNumeric).findFirst())
                .map(Integer::parseInt)
                .orElse(0);
    }

    private String getImagePath(Document document) {
        return Optional
                .of(document.getElementsByAttributeValueStarting("alt", "Download"))
                .map(elements -> elements.select("img"))
                .map(Elements::first)
                .map(element -> element.attr("src"))
                .orElse(null);
    }

    private List<TrackDto> getAlbumTracks(Document document) {
        List<TrackDto> result = new ArrayList<>();
        Elements trackLinks = document.getElementsByAttributeValueMatching("href", trackLinkPattern);
        for (Element trackLink : trackLinks) {
            Optional.of(trackLink)
                    .map(Element::parent)
                    .map(elt -> elt.getElementsByTag("h1"))
                    .map(Elements::first)
                    .map(Element::text)
                    .ifPresent(text -> result.add(new TrackDto(text, StringUtils.EMPTY)));
        }
        return result;
    }

    @Activate
    private void doActivate(Config config) {
        this.listUrl = config.listUrl();
        this.linkPattern = Pattern.compile(config.linkPattern());
        this.trackLinkPattern = Pattern.compile(config.trackLinkPattern());
        this.timeout = config.timeout();
    }

    @ObjectClassDefinition(name = "TrendyBeatz Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Albums List URL")
        String listUrl() default "https://trendybeatz.com/artist-albums";

        @AttributeDefinition(name = "Album Link Pattern")
        String linkPattern() default "trendybeatz\\.com/artist-albums/\\d+/[\\w-]+$";

        @AttributeDefinition(name = "Album Track Link Pattern")
        String trackLinkPattern() default "trendybeatz\\.com/download-mp3/\\d+/[\\w-]+$";

        @AttributeDefinition(name = "Connection timeout", type = AttributeType.INTEGER)
        int timeout() default 10_000;
    }
}
