package com.oop.memorystore.reference;

/**
 * Factory to create an instance of a {@link Reference}
 *
 * @param <V> value type
 */
public interface ReferenceFactory<V> {
  /**
   * Create a reference for the given object
   *
   * @param obj object to reference
   * @return reference
   */
  Reference<V> createReference(V obj);
}
