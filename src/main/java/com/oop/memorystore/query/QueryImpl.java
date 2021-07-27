package com.oop.memorystore.query;

import java.util.ArrayList;
import java.util.List;

class QueryImpl implements BasicQuery, AndQuery, OrQuery {
  private final List<IndexMatch> indexMatches = new ArrayList<>();
  private Operator operator;

  @Override
  public AndQuery and(final String indexName, final Object key) {
    indexMatches.add(new IndexMatch(indexName, key));
    operator = Operator.AND;
    return this;
  }

  @Override
  public OrQuery or(final String indexName, final Object key) {
    indexMatches.add(new IndexMatch(indexName, key));
    operator = Operator.OR;
    return this;
  }

  @Override
  public QueryDefinition build() {
    return new QueryDefinition(indexMatches, operator);
  }
}
