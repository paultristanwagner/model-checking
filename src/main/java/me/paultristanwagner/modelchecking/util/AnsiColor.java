package me.paultristanwagner.modelchecking.util;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public enum AnsiColor {
  RESET("\u001b[0m"),
  RED("\u001b[31m"),
  GREEN("\u001b[32;1m"),
  YELLOW("\u001b[33m"),
  GRAY("\u001b[90m");

  private final String code;

  AnsiColor( String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return code;
  }
}
