package com.oop.memorystore.implementation.index;

import com.oop.memorystore.implementation.index.reducer.Reducer;
import com.oop.memorystore.implementation.reference.Reference;

import java.util.*;
import java.util.stream.Collectors;

public class References<K, V> {
  private final K key;
  private final Reducer<K, V> reducer;
  private final Set<Reference<V>> references;
  private Set<Reference<V>> reducedReferences;

  private References(
      final K key,
      final Set<Reference<V>> references,
      final Collection<Reference<V>> reducedReferences,
      final Reducer<K, V> reducer) {
    this.key = key;
    this.references = new LinkedHashSet<>(references);
    this.reducedReferences = new LinkedHashSet<>(reducedReferences);
    this.reducer = reducer;
  }

  public References(final K key, final Reference<V> reference, final Reducer<K, V> reducer) {
    this(key, Collections.singleton(reference), Collections.emptySet(), reducer);
      this.reducedReferences.add(reference);
      this.reducedReferences = this.reduce(this.reducedReferences);
  }

  public void add(final Reference<V> reference) {
      this.references.add(reference);
      this.reducedReferences.add(reference);
      this.reducedReferences = this.reduce(this.reducedReferences);
  }

  public void remove(final Reference<V> reference) {
      this.references.remove(reference);

    if (this.reducedReferences.contains(reference)) {
        this.reducedReferences =
          this.reduce(this.references); // on remove, re-reduce all references associated with this key
    }
  }

  public Set<Reference<V>> getAllReferences() {
    return Collections.unmodifiableSet(this.reducedReferences);
  }

  public List<V> getAll() {
    return this.reducedReferences.stream().map(Reference::get).collect(Collectors.toList());
  }

  public boolean isEmpty() {
    return this.references.isEmpty();
  }

  public Optional<V> findFirst() {
    return this.reducedReferences.stream().map(Reference::get).findFirst();
  }

  public References<K, V> copy() {
    return new References<>(this.key, this.references, this.reducedReferences, this.reducer);
  }

  private Set<Reference<V>> reduce(final Set<Reference<V>> references) {
    if (this.reducer == null) {
      return references;
    }

    final List<Element<V>> elements =
        references.stream().map(Element::new).collect(Collectors.toList());
      this.reducer.reduce(this.key, elements);

    return elements.stream()
        .filter(element -> !element.isRemoved())
        .map(Element::getReference)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
