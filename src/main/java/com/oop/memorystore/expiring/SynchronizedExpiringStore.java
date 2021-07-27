package com.oop.memorystore.expiring;

import com.oop.memorystore.ExpiringStore;
import com.oop.memorystore.SynchronizedStore;

public class SynchronizedExpiringStore<V> extends SynchronizedStore<V> implements ExpiringStore<V> {

  public SynchronizedExpiringStore(ExpiringStore<V> store) {
    super(store);
  }

  @Override
  public ExpiringPolicy getExpiringPolicy() {
    return ((ExpiringStore<V>)store).getExpiringPolicy();
  }

  @Override
  public void invalidate() {
    synchronized (mutex) {
      ((ExpiringStore<V>)store).invalidate();
    }
  }
}
