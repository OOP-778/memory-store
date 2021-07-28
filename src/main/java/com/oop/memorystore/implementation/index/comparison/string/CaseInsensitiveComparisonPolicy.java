package com.oop.memorystore.implementation.index.comparison.string;

import com.oop.memorystore.implementation.index.comparison.ComparisonPolicy;

import java.util.Locale;

/** Comparison policy for comparing two string elements regardless of case. */
public class CaseInsensitiveComparisonPolicy implements ComparisonPolicy<String> {
  @Override
  public boolean supports(final Class<?> clazz) {
    return clazz == String.class;
  }

  @Override
  public String createComparable(final String item) {
    return item.toLowerCase(Locale.getDefault());
  }
}
