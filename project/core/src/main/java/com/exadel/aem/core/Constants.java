package com.exadel.aem.core;

public class Constants {

    private static final String PROJECT_ROOT = "/content/sample-project/us/en";
    public static final String ARTISTS_FOLDER = PROJECT_ROOT + "/artists";
    public static final String ALBUMS_FOLDER = PROJECT_ROOT + "/albums";

    public static final String PAGE_CONTAINER_PATH = "jcr:content/root/container/container";
    public static final String ALBUM_RESOURCE_PATH = PAGE_CONTAINER_PATH + "/album";
    public static final String ARTIST_RESOURCE_PATH = PAGE_CONTAINER_PATH + "/artist";

    private Constants() {
    }
}
