package com.oop.memorystore.implementation;

import java.util.Iterator;

public class UnmodifiableIterator<V> implements Iterator<V> {
  private final Iterator<V> iterator;

  public UnmodifiableIterator(final Iterator<V> iterator) {
    this.iterator = iterator;
  }

  @Override
  public boolean hasNext() {
    return this.iterator.hasNext();
  }

  @Override
  public V next() {
    return this.iterator.next();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("remove");
  }
}
