package com.oop.memorystore.implementation.expiring;

import com.oop.memorystore.api.ExpirationManager;
import com.oop.memorystore.api.ExpiringStore;
import com.oop.memorystore.api.StoreQuery;
import com.oop.memorystore.implementation.StoreQueryImpl;
import com.oop.memorystore.implementation.SynchronizedStore;

public class SynchronizedExpiringStore<V> extends SynchronizedStore<V> implements ExpiringStore<V> {

    public SynchronizedExpiringStore(ExpiringMemoryStore<V> store) {
        super(store);
    }

    @Override
    public ExpirationManager<V> getExpirationManager() {
        return ((ExpiringMemoryStore<V>) store).getExpirationManager();
    }

    @Override
    public void invalidate() {
        synchronized (mutex) {
            ((ExpiringMemoryStore<V>) store).invalidate();
        }
    }
}
