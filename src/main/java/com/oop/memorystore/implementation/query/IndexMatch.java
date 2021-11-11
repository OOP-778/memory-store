package com.oop.memorystore.implementation.query;

public class IndexMatch {
  private final String indexName;
  private final Object key;

  IndexMatch(final String indexName, final Object key) {
    this.indexName = indexName;
    this.key = key;
  }

  public String getIndexName() {
    return this.indexName;
  }

  public Object getKey() {
    return this.key;
  }
}
