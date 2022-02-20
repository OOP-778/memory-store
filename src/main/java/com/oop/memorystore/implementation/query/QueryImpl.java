package com.oop.memorystore.implementation.query;

import java.util.ArrayList;
import java.util.List;

class QueryImpl implements BasicQuery, AndQuery, OrQuery {
    private final List<IndexMatch> indexMatches = new ArrayList<>();
    private Operator operator;

    protected QueryImpl() {
    }

    @Override
    public AndQuery and(final String indexName, final Object key) {
        this.indexMatches.add(new IndexMatch(indexName, key));
        this.operator = Operator.AND;
        return this;
    }

    @Override
    public OrQuery or(final String indexName, final Object key) {
        this.indexMatches.add(new IndexMatch(indexName, key));
        this.operator = Operator.OR;
        return this;
    }

    @Override
    public QueryDefinition build() {
        return new QueryDefinition(this.indexMatches, this.operator);
    }
}
