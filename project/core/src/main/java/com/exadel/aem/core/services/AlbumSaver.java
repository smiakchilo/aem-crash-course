package com.exadel.aem.core.services;

import com.exadel.aem.core.dto.AlbumDto;

import java.util.List;

public interface AlbumSaver {

    void save(List<AlbumDto> albums) throws Exception;
}
