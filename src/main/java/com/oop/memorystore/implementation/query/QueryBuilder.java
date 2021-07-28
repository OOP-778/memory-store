package com.oop.memorystore.implementation.query;

public class QueryBuilder {

  private final QueryImpl query = new QueryImpl();

  protected QueryBuilder() {}

  public static QueryBuilder create() {
    return new QueryBuilder();
  }

  public QueryBuilder or(String index, Object equals) {
    query.or(index, equals);
    return this;
  }

  public QueryBuilder and(String index, Object equals) {
    query.and(index, equals);
    return this;
  }

  public Query build() {
    return query;
  }
}
