package com.abnamro.battleship.exception;

import lombok.Getter;

/**
 * The type In valid game id exception.
 */
@Getter
public class InvalidGameException extends RuntimeException {

  /**
   * Instantiates a new In valid game id exception.
   *
   * @param message the message
   */
  public InvalidGameException(final String message) {
    super(message);
  }

}
