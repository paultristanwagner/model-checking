package me.paultristanwagner.modelchecking.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Lexer {

  public static TokenType WHITESPACE = TokenType.of("whitespace", "^\\s+");

  private String input;
  private final List<TokenType> tokenTypes = new ArrayList<>();

  private int cursor;
  private Token lookahead;

  public Lexer(String input) {
    registerTokenType(WHITESPACE);
  }

  protected void initialize(String input) {
    this.input = input;
    this.cursor = 0;

    this.nextToken();
  }

  public void registerTokenType(TokenType tokenType) {
    this.tokenTypes.add(tokenType);
  }

  public void registerTokenTypes(TokenType... tokenTypes) {
    for (TokenType tokenType : tokenTypes) {
      registerTokenType(tokenType);
    }
  }

  public Token nextToken() {
    if (cursor >= input.length()) {
      lookahead = null;
      return null;
    }

    for (TokenType tokenType : tokenTypes) {
      String regex = tokenType.getRegex();
      String remaining = input.substring(cursor);

      Matcher matcher = Pattern.compile(regex).matcher(remaining);

      if (!matcher.find()) {
        continue;
      }

      String group = matcher.group();
      cursor += group.length();

      if (tokenType == WHITESPACE) {
        return nextToken();
      }

      lookahead = Token.of(tokenType, group);
      return lookahead;
    }

    throw new SyntaxError("Unrecognized token", input, cursor);
  }

  public void consume(TokenType token) {
    if (lookahead == null) {
      throw new SyntaxError("Expected token '" + token.getName() + "'", input, cursor);
    }

    if (lookahead.getType() != token) {
      throw new SyntaxError(
          "Expected token '"
              + token.getName()
              + "' but got token '"
              + lookahead.getType().getName()
              + "'",
          input,
          cursor - lookahead.getValue().length());
    }

    nextToken();
  }

  public String getInput() {
    return input;
  }

  public int getCursor() {
    return cursor;
  }

  public Token getLookahead() {
    return lookahead;
  }

  public boolean hasNextToken() {
    return lookahead != null;
  }

  public void requireNextToken() {
    if (!hasNextToken()) {
      throw new SyntaxError("Unexpected end of input", input, cursor);
    }
  }

  public void requireNoToken() {
    if (hasNextToken()) {
      throw new SyntaxError(
          "Unexpected token " + lookahead.getType().getName(),
          input,
          cursor - lookahead.getValue().length());
    }
  }

  public int getTokenStart() {
    return cursor - lookahead.getValue().length();
  }
}
