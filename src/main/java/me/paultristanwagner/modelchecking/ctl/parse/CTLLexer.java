package me.paultristanwagner.modelchecking.ctl.parse;

import static me.paultristanwagner.modelchecking.util.Symbol.*;

import me.paultristanwagner.modelchecking.parse.Lexer;
import me.paultristanwagner.modelchecking.parse.TokenType;
import me.paultristanwagner.modelchecking.util.Symbol;

public class CTLLexer extends Lexer {

  static final TokenType LPAREN = TokenType.of("(", "^\\(");
  static final TokenType RPAREN = TokenType.of(")", "^\\)");
  static final TokenType TRUE = TokenType.of("true", "^(true|TRUE)");
  static final TokenType FALSE = TokenType.of("false", "^(false|FALSE)");
  static final TokenType NOT = TokenType.of("not", "^(" + Symbol.NOT + "|!|not|NOT|~)");
  static final TokenType AND = TokenType.of("and", "^(" + Symbol.AND + "|&|and|AND)");
  static final TokenType OR = TokenType.of("or", "^(" + Symbol.OR + "|\\||or|OR)");
  static final TokenType NEXT = TokenType.of("next", "^(" + Symbol.NEXT + "|next|NEXT|O)");
  static final TokenType UNTIL = TokenType.of("until", "^(" + Symbol.UNTIL + "|until|UNTIL)");
  static final TokenType EVENTUALLY =
      TokenType.of(
          "eventually", "^(" + Symbol.EVENTUALLY + "|eventually|EVENTUALLY|diamond|DIAMOND)");
  static final TokenType ALWAYS =
      TokenType.of("always", "^(" + Symbol.ALWAYS + "|always|ALWAYS|box|BOX)");
  static final TokenType ALL =
      TokenType.of("all", "^(" + UNIVERSAL_QUANTIFIER + "|all|ALL|A)");
  static final TokenType EXISTS =
      TokenType.of("exists", "^(" + EXISTENTIAL_QUANTIFIER + "|exists|EXISTS|ex|EX|E)");
  static final TokenType IDENTIFIER = TokenType.of("identifier", "^[a-z]");

  public CTLLexer(String input) {
    super(input);

    registerTokenTypes(
        LPAREN,
        RPAREN,
        TRUE,
        FALSE,
        NOT,
        AND,
        OR,
        NEXT,
        UNTIL,
        EVENTUALLY,
        ALWAYS,
        ALL,
        EXISTS,
        IDENTIFIER);

    this.initialize(input);
  }
}
