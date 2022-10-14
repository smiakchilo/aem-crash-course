package com.exadel.aem.core.services.impl;

import com.exadel.aem.core.dto.AlbumDto;
import com.exadel.aem.core.services.AlbumRetriever;
import com.exadel.aem.core.services.AlbumSaver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component(immediate = true)
public class AlbumRetrieverImpl implements AlbumRetriever {
    private static final Logger LOG = LoggerFactory.getLogger(AlbumRetrieverImpl.class);

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
    private List<Supplier<List<AlbumDto>>> suppliers;

    @Reference
    private AlbumSaver albumSaver;

    @Override
    public void retrieveNewAlbums() throws Exception {
        if (suppliers == null || suppliers.isEmpty()) {
            return;
        }
        List<AlbumDto> albums = suppliers
                .stream()
                .flatMap(supplier -> supplier.get().stream())
                .collect(Collectors.toList());
        albumSaver.save(albums);
    }

    @Activate
    private void doActivate() {
        LOG.info("Service started");
    }

    @Deactivate
    private void onDeactivate() {
        LOG.info("Service stopped");
    }
}
