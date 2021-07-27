package com.oop.memorystore.expiring;

import com.oop.memorystore.reference.Reference;

public class ExpiringReference<V> implements Reference<V> {

  private final V reference;

  /** When was reference last fetched. By default it's set to it's creation time */
  private long lastFetched = System.currentTimeMillis();

  /**
   * Policy of expiration
   */
  private final ExpiringPolicy policy;

  public ExpiringReference(final V reference, final ExpiringPolicy policy) {
    this.reference = reference;
    this.policy = policy;
  }

  public boolean shouldBeInvalidated() {
    return policy.getUnit().toMillis(policy.getTime()) >= (System.currentTimeMillis() - lastFetched);
  }

  public void updateFetched() {
    if (!policy.shouldResetAfterAccess()) return;
    this.lastFetched = System.currentTimeMillis();
  }

  @Override
  public V get() {
    return reference;
  }
}
