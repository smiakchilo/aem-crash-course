package com.exadel.aem.core.services.impl;

import com.exadel.aem.core.dto.AlbumDto;
import com.exadel.aem.core.dto.ArtistDto;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Component
public class DummyDownloader implements Supplier<List<AlbumDto>> {
    private static final String DEFAULT_IMAGE = "https://live.staticflickr.com/13/17130986_f1c38c12bd_b.jpg";

    @Override
    public List<AlbumDto> get() {
        AlbumDto testAlbumDto = new AlbumDto(
                "Non-existing album",
                new ArtistDto("No one", StringUtils.EMPTY),
                2000,
                DEFAULT_IMAGE,
                Collections.emptyList());
        return Collections.singletonList(testAlbumDto);
    }
}
