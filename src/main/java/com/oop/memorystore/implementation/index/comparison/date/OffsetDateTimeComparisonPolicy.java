package com.oop.memorystore.implementation.index.comparison.date;

import com.oop.memorystore.implementation.index.comparison.ComparisonPolicy;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Comparison policy for comparing two {@link OffsetDateTime} values normalized to an UTC time
 * offset
 */
public class OffsetDateTimeComparisonPolicy implements ComparisonPolicy<OffsetDateTime> {
  @Override
  public boolean supports(final Class<?> clazz) {
    return clazz == OffsetDateTime.class;
  }

  @Override
  public OffsetDateTime createComparable(final OffsetDateTime offsetDateTime) {
    return offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC);
  }
}
