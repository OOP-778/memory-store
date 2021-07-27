package com.oop.memorystore;

import com.oop.memorystore.expiring.ExpiringPolicy;

/**
 * Expiring store contains values that can expire after x time
 */
public interface ExpiringStore<T> extends Store<T> {

    /**
     * Get expiring policy
     */
    ExpiringPolicy getExpiringPolicy();

    /**
     * Invalidate expired references
     */
    void invalidate();

}
