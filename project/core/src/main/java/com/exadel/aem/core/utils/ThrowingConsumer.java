package com.exadel.aem.core.utils;

public interface ThrowingConsumer<T> {

    void accept(T t) throws Exception;
}
