package com.oop.memorystore.implementation.query;

public interface AndQuery extends Query {
  /**
   * Adds an AND clause to the search query
   *
   * @param indexName name of the index
   * @param key indexed key to match
   * @return query
   */
  AndQuery and(String indexName, Object key);
}
