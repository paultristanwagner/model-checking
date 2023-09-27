package me.paultristanwagner.modelchecking.ctl.parse;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import me.paultristanwagner.modelchecking.ctl.formula.path.*;
import me.paultristanwagner.modelchecking.ctl.formula.state.*;
import me.paultristanwagner.modelchecking.parse.*;

public class CTLParser implements Parser<CTLFormula> {

  // todo: add logical operators for implication and equivalence

  /*
   * CTL Grammar:
   *
   *    <A> ::= <B>
   *        | <A> OR <B>
   *    <B> ::= <C>
   *        | <B> AND <C>
   *    <C> ::= NOT <C>
   *        | ALL <PATH>
   *        | EXISTS <PATH>
   *        | '(' <A> ')'
   *        | TRUE
   *        | FALSE
   *        | IDENTIFIER
   *
   *    PATH ::= NEXT <A>
   *         | ALWAYS <A>
   *         | EVENTUALLY <A>
   *         | '(' <A> ')' UNTIL <A>
   *         | '(' <A> UNTIL <A> ')'
   *         | <A> UNTIL <A>
   *
   */

  @Override
  public CTLFormula parse(String input, AtomicInteger index) {
    CTLLexer lexer = new CTLLexer(input);

    lexer.requireNextToken();

    CTLFormula formula = parseStateFormula(lexer);

    lexer.requireNoToken();

    return formula;
  }

  private CTLFormula parseStateFormula(Lexer lexer) {
    return A(lexer);
  }

  private CTLFormula A(Lexer lexer) {
    List<CTLFormula> components = new ArrayList<>();
    CTLFormula first = B(lexer);
    components.add(first);

    while (lexer.hasNextToken() && lexer.getLookahead().getType() == OR) {
      lexer.consume(OR);
      components.add(B(lexer));
    }

    if (components.size() == 1) {
      return first;
    }

    return CTLOrFormula.or(components);
  }

  private CTLFormula B(Lexer lexer) {
    List<CTLFormula> components = new ArrayList<>();
    CTLFormula first = C(lexer);
    components.add(first);

    while (lexer.hasNextToken() && lexer.getLookahead().getType() == AND) {
      lexer.consume(AND);
      components.add(C(lexer));
    }

    if (components.size() == 1) {
      return first;
    }

    return CTLAndFormula.and(components);
  }

  private CTLFormula C(Lexer lexer) {
    lexer.requireNextToken();

    Token token = lexer.getLookahead();
    TokenType tokenType = token.getType();

    if (tokenType == NOT) {
      return parseNotFormula(lexer);
    } else if (tokenType == ALL) {
      return parseAllFormula(lexer);
    } else if (tokenType == EXISTS) {
      return parseExistsFormula(lexer);
    } else if (tokenType == LPAREN) {
      return parseParenthesisFormula(lexer);
    } else if (tokenType == TRUE) {
      return parseTrueFormula(lexer);
    } else if (tokenType == FALSE) {
      return parseFalseFormula(lexer);
    } else {
      return parseIdentifierFormula(lexer);
    }
  }

  private CTLNotFormula parseNotFormula(Lexer lexer) {
    lexer.consume(NOT);

    CTLFormula argument = C(lexer);
    return CTLNotFormula.not(argument);
  }

  private CTLAllFormula parseAllFormula(Lexer lexer) {
    lexer.consume(ALL);

    CTLPathFormula pathFormula = parsePathFormula(lexer);
    return CTLAllFormula.forAll(pathFormula);
  }

  private CTLExistsFormula parseExistsFormula(Lexer lexer) {
    lexer.consume(EXISTS);

    CTLPathFormula pathFormula = parsePathFormula(lexer);
    return CTLExistsFormula.exists(pathFormula);
  }

  private CTLFormula parseParenthesisFormula(Lexer lexer) {
    lexer.consume(LPAREN);
    CTLFormula inner = parseStateFormula(lexer);
    lexer.consume(RPAREN);

    return CTLParenthesisFormula.parenthesis(inner);
  }

  private CTLTrueFormula parseTrueFormula(Lexer lexer) {
    lexer.consume(TRUE);

    return CTLTrueFormula.TRUE();
  }

  private CTLFalseFormula parseFalseFormula(Lexer lexer) {
    lexer.consume(FALSE);

    return CTLFalseFormula.FALSE();
  }

  private CTLIdentifierFormula parseIdentifierFormula(Lexer lexer) {
    Token lookahead = lexer.getLookahead();

    String identifier = lookahead.getValue();
    lexer.consume(IDENTIFIER);

    return CTLIdentifierFormula.identifier(identifier);
  }

  private CTLPathFormula parsePathFormula(Lexer lexer) {
    lexer.requireNextToken();

    Token lookahead = lexer.getLookahead();
    TokenType type = lookahead.getType();
    if (type.equals(NEXT)) {
      return parseNextFormula(lexer);
    } else if (type.equals(EVENTUALLY)) {
      return parseEventuallyFormula(lexer);
    } else if (type.equals(ALWAYS)) {
      return parseAlwaysFormula(lexer);
    } else if (type.equals(LPAREN)) {
      return parseParenthesisUntilFormula(lexer);
    }

    return parseUntilFormula(lexer);
  }

  private CTLNextFormula parseNextFormula(Lexer lexer) {
    lexer.consume(NEXT);

    CTLFormula stateFormula = parseStateFormula(lexer);
    return CTLNextFormula.next(stateFormula);
  }

  private CTLUntilFormula parseUntilFormula(Lexer lexer) {
    lexer.requireNextToken();

    if (lexer.getLookahead().getType() == LPAREN) {
      return parseParenthesisUntilFormula(lexer);
    }

    CTLFormula left = parseStateFormula(lexer);

    lexer.consume(UNTIL);

    CTLFormula right = parseStateFormula(lexer);

    return CTLUntilFormula.until(left, right);
  }

  private CTLUntilFormula parseParenthesisUntilFormula(Lexer lexer) {
    lexer.consume(LPAREN);
    CTLFormula left = parseStateFormula(lexer);
    CTLFormula right;

    lexer.requireNextToken();

    if (lexer.getLookahead().getType() == RPAREN) {
      lexer.consume(RPAREN);
      left = CTLParenthesisFormula.parenthesis(left);

      lexer.consume(UNTIL);
      right = parseStateFormula(lexer);
    } else {
      lexer.consume(UNTIL);
      right = parseStateFormula(lexer);
      lexer.consume(RPAREN);
    }

    return CTLUntilFormula.until(left, right);
  }

  private CTLEventuallyFormula parseEventuallyFormula(Lexer lexer) {
    lexer.consume(EVENTUALLY);

    CTLFormula stateFormula = parseStateFormula(lexer);
    return CTLEventuallyFormula.eventually(stateFormula);
  }

  private CTLAlwaysFormula parseAlwaysFormula(Lexer lexer) {
    lexer.consume(ALWAYS);

    CTLFormula stateFormula = parseStateFormula(lexer);
    return CTLAlwaysFormula.always(stateFormula);
  }
}
