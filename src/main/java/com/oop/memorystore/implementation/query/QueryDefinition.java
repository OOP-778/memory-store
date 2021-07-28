package com.oop.memorystore.implementation.query;

import java.util.List;

public class QueryDefinition {
  private final List<IndexMatch> indexMatches;
  private final Operator operator;

  public QueryDefinition(final List<IndexMatch> indexMatches, final Operator operator) {
    this.indexMatches = indexMatches;
    this.operator = operator;
  }

  public List<IndexMatch> getIndexMatches() {
    return indexMatches;
  }

  public Operator getOperator() {
    return operator;
  }
}
