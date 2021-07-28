package com.oop.memorystore.api;

public interface ExpirationManager<V> {

    void onAdd(final V value);

    void onRemove(final V value);

    boolean checkExpiration(V value);
}
