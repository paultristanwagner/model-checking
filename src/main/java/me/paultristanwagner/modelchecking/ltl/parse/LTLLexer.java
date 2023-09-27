package me.paultristanwagner.modelchecking.ltl.parse;

import static me.paultristanwagner.modelchecking.util.Symbol.*;

import me.paultristanwagner.modelchecking.parse.Lexer;
import me.paultristanwagner.modelchecking.parse.TokenType;

public class LTLLexer extends Lexer {

  static final TokenType LPAREN = TokenType.of("(", "^\\(");
  static final TokenType RPAREN = TokenType.of(")", "^\\)");
  static final TokenType TRUE = TokenType.of("true", "^(true|TRUE)");
  static final TokenType FALSE = TokenType.of("false", "^(false|FALSE)");
  static final TokenType NOT = TokenType.of("not", "^(" + NOT_SYMBOL + "|!|not|NOT|~)");
  static final TokenType AND = TokenType.of("and", "^(" + AND_SYMBOL + "|&|and|AND)");
  static final TokenType OR = TokenType.of("or", "^(" + OR_SYMBOL + "|\\||or|OR)");
  static final TokenType NEXT = TokenType.of("next", "^(" + NEXT_SYMBOL + "|next|NEXT|O)");
  static final TokenType UNTIL = TokenType.of("until", "^(" + UNTIL_SYMBOL + "|until|UNTIL)");
  static final TokenType EVENTUALLY =
      TokenType.of(
          "eventually", "^(" + EVENTUALLY_SYMBOL + "|eventually|EVENTUALLY|diamond|DIAMOND)");
  static final TokenType ALWAYS =
      TokenType.of("always", "^(" + ALWAYS_SYMBOL + "|always|ALWAYS|box|BOX)");
  static final TokenType IDENTIFIER = TokenType.of("identifier", "^[a-z]");

  public LTLLexer(String input) {
    super(input);

    registerTokenTypes(
        LPAREN, RPAREN, TRUE, FALSE, NOT, AND, OR, NEXT, UNTIL, EVENTUALLY, ALWAYS, IDENTIFIER);

    this.initialize(input);
  }
}
