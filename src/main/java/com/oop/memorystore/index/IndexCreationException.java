package com.oop.memorystore.index;

/** Thrown by an Index when indexing of a new item fails. */
public class IndexCreationException extends Exception {
  IndexCreationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
