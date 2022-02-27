package com.oop.memorystore.implementation.query;

public class QueryBuilder {

    private final QueryImpl query = new QueryImpl();

    protected QueryBuilder() {
    }

    public static QueryBuilder create() {
        return new QueryBuilder();
    }

    public QueryBuilder or(final String index, final Object equals) {
        this.query.or(index, equals);
        return this;
    }

    public QueryBuilder and(final String index, final Object equals) {
        this.query.and(index, equals);
        return this;
    }

    public Query build() {
        return this.query;
    }
}
