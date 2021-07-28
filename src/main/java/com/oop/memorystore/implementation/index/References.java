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
    reducedReferences.add(reference);
    reducedReferences = reduce(reducedReferences);
  }

  public void add(final Reference<V> reference) {
    references.add(reference);
    reducedReferences.add(reference);
    reducedReferences = reduce(reducedReferences);
  }

  public void remove(final Reference<V> reference) {
    references.remove(reference);

    if (reducedReferences.contains(reference)) {
      reducedReferences =
          reduce(references); // on remove, re-reduce all references associated with this key
    }
  }

  public Set<Reference<V>> getAllReferences() {
    return Collections.unmodifiableSet(reducedReferences);
  }

  public List<V> getAll() {
    return reducedReferences.stream().map(Reference::get).collect(Collectors.toList());
  }

  public boolean isEmpty() {
    return references.isEmpty();
  }

  public Optional<V> findFirst() {
    return reducedReferences.stream().map(Reference::get).findFirst();
  }

  public References<K, V> copy() {
    return new References<>(key, references, reducedReferences, reducer);
  }

  private Set<Reference<V>> reduce(final Set<Reference<V>> references) {
    if (reducer == null) {
      return references;
    }

    final List<Element<V>> elements =
        references.stream().map(Element::new).collect(Collectors.toList());
    reducer.reduce(key, elements);

    return elements.stream()
        .filter(element -> !element.isRemoved())
        .map(Element::getReference)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
