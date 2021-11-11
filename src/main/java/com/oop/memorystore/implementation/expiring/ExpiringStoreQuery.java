package com.oop.memorystore.implementation.expiring;

import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.StoreQueryImpl;
import com.oop.memorystore.implementation.index.ReferenceIndex;
import com.oop.memorystore.implementation.reference.Reference;
import java.util.LinkedHashSet;

public class ExpiringStoreQuery<V> extends StoreQueryImpl<V> {
    public ExpiringStoreQuery(final Store<V> store) {
        super(store);
    }

    protected ExpiringMemoryStore<V> asExpiringStore() {
        return (ExpiringMemoryStore<V>) this.store;
    }

    @Override
    protected LinkedHashSet<Reference<V>> getValuesOfIndex(final ReferenceIndex<?, V> index, final Object key) {
        final LinkedHashSet<Reference<V>> values = super.getValuesOfIndex(index, key);

        values.removeIf(value -> {
            final boolean shouldBeInvalidated = this.asExpiringStore().getExpirationManager().checkExpiration(value.get());
            if (shouldBeInvalidated) {
                index.removeIndex(value);
            }

            return shouldBeInvalidated;
        });

        return values;
    }
}
