package com.oop.memorystore.implementation.identity;

/** Provides identity object as self */
public class DefaultIdentityProvider implements IdentityProvider {
  @Override
  public Object getIdentity(final Object obj) {
    return obj;
  }
}
