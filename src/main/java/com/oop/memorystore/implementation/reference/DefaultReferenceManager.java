package com.oop.memorystore.implementation.reference;

import com.oop.memorystore.implementation.identity.IdentityProvider;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of reference manager that maintains unique references
 *
 * @param <V> value type
 */
public class DefaultReferenceManager<V> implements ReferenceManager<V> {
  private final IdentityProvider identityProvider;
  private final ReferenceFactory<V> referenceFactory;
  private final Map<Object, Reference<V>> referenceMap;

  public DefaultReferenceManager(
      final IdentityProvider identityProvider, final ReferenceFactory<V> referenceFactory) {
    this.identityProvider = identityProvider;
    this.referenceFactory = referenceFactory;
    this.referenceMap = new LinkedHashMap<>();
  }

  private DefaultReferenceManager(
      final IdentityProvider identityProvider,
      final ReferenceFactory<V> referenceFactory,
      final Map<Object, Reference<V>> referenceMap) {
    this.identityProvider = identityProvider;
    this.referenceFactory = referenceFactory;
    this.referenceMap = new LinkedHashMap<>(referenceMap);
  }

  @Override
  public Collection<Reference<V>> getReferences() {
    return this.referenceMap.values();
  }

  @Override
  public Optional<Reference<V>> findReference(final Object item) {
    final Object identity = this.identityProvider.getIdentity(item);

    if (identity == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(this.referenceMap.get(identity));
  }

  @Override
  public int size() {
    return this.referenceMap.size();
  }

  @Override
  public void clear() {
      this.referenceMap.clear();
  }

  @Override
  public Reference<V> add(final V item) {
    final Object identity = this.identityProvider.getIdentity(item);

    if (identity == null) {
      return null;
    }

    if (this.referenceMap.containsKey(identity)) {
      return this.referenceMap.get(identity);
    }

    final Reference<V> reference = this.referenceFactory.createReference(item);
      this.referenceMap.put(identity, reference);
    return reference;
  }

  @Override
  public ReferenceManager<V> copy() {
    return new DefaultReferenceManager<>(this.identityProvider, this.referenceFactory, this.referenceMap);
  }

  @Override
  public Reference<V> remove(final Object item) {
    final Object identity = this.identityProvider.getIdentity(item);

    if (identity == null) {
      return null;
    }

    return this.referenceMap.remove(identity);
  }
}
