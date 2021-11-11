package com.oop.memorystore.implementation.index;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SynchronizedIndex<T> implements Index<T> {
    private final Index<T> index;
    private final Object mutex;

    public SynchronizedIndex(final Index<T> index, final Object mutex) {
        this.index = index;
        this.mutex = mutex;
    }

    @Override
    public T getFirst(final Object key) {
        final T result;

        synchronized (this.mutex) {
            result = this.index.getFirst(key);
        }

        return result;
    }

    @Override
    public Optional<T> findFirst(final Object key) {
        final Optional<T> result;

        synchronized (this.mutex) {
            result = this.index.findFirst(key);
        }

        return result;
    }

    @Override
    public List<T> get(final Object key) {
        final List<T> results;

        synchronized (this.mutex) {
            results = this.index.get(key);
        }

        return results;
    }

    @Override
    public String getName() {
        return this.index.getName();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }

        final SynchronizedIndex<?> that = (SynchronizedIndex<?>) other;
        return Objects.equals(this.index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.index);
    }

    @Override
    public String toString() {
        return String.valueOf(this.index);
    }

    public Index<T> getIndex() {
        return this.index;
    }
}
