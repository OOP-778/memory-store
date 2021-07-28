package com.oop.memorystore.implementation.query;

public interface OrQuery extends Query {
  /**
   * Adds an OR clause to the search query
   *
   * @param indexName name of the index
   * @param key indexed key to match
   * @return query
   */
  OrQuery or(String indexName, Object key);
}
