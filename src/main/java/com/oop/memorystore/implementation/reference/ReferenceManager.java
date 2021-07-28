package com.oop.memorystore.implementation.reference;

import java.util.Collection;
import java.util.Optional;

/**
 * The reference managed is internal to a store and should not be exposed outside the class. It is
 * responsible for storing, managing and creating references to objects held with a store.
 *
 * @param <T> type of item referenced
 */
public interface ReferenceManager<T> {
  /**
   * Return all references as a collection. This MUST return a modifiable collection.
   *
   * @return modifiable reference collection
   */
  Collection<Reference<T>> getReferences();

  /**
   * Find a reference matching the item if one exists
   *
   * @param item item to lookup
   * @return reference
   */
  Optional<Reference<T>> findReference(Object item);

  /**
   * Total number of references held by the manager
   *
   * @return total number of references held
   */
  int size();

  /** clear all references held */
  void clear();

  /**
   * Create a new reference for the given object. If a reference already exists for this item,
   * return existing reference without adding duplicate.
   *
   * @param item item to on to reference manager
   * @return reference
   */
  Reference<T> add(T item);

  /**
   * Create a copy of the reference manager
   *
   * @return copy
   */
  ReferenceManager<T> copy();

  /**
   * Remove item in store
   *
   * @param item item to remove
   * @return reference removed
   */
  Reference<T> remove(Object item);
}
