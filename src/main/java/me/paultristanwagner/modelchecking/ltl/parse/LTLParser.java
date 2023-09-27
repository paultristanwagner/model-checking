package me.paultristanwagner.modelchecking.ltl.parse;

import static me.paultristanwagner.modelchecking.ltl.formula.LTLAlwaysFormula.always;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLAndFormula.and;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLEventuallyFormula.eventually;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLFalseFormula.FALSE;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLIdentifierFormula.identifier;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLNextFormula.next;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLNotFormula.not;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLOrFormula.or;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLParenthesisFormula.parenthesis;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLTrueFormula.TRUE;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLUntilFormula.until;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.ALWAYS;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.AND;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.EVENTUALLY;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.FALSE;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.IDENTIFIER;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.LPAREN;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.NEXT;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.NOT;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.OR;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.RPAREN;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.TRUE;
import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.UNTIL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import me.paultristanwagner.modelchecking.ltl.formula.*;
import me.paultristanwagner.modelchecking.parse.Parser;
import me.paultristanwagner.modelchecking.parse.SyntaxError;
import me.paultristanwagner.modelchecking.parse.Token;
import me.paultristanwagner.modelchecking.parse.TokenType;

public class LTLParser implements Parser<LTLFormula> {

  // todo: add logical operators for implication and equivalence

  /*
   * LTL Grammar:
   *
   *    <A> ::= <B>
   *        | <A> OR <B>
   *    <B> ::= <C>
   *        | <B> AND <C>
   *    <C> :: <D>
   *        | <C> UNTIL <D>
   *    <D> ::= NOT <D>
   *        | NEXT <A>
   *        | ALWAYS <A>
   *        | EVENTUALLY <A>
   *        | '(' <A> ')'
   *        | TRUE
   *        | FALSE
   *        | IDENTIFIER
   *
   */

  @Override
  public LTLFormula parse(String input, AtomicInteger index) {
    LTLLexer lexer = new LTLLexer(input);

    lexer.requireNextToken();

    LTLFormula formula = parseLTLFormula(lexer);

    lexer.requireNoToken();

    return formula;
  }

  private LTLFormula parseLTLFormula(LTLLexer lexer) {
    return A(lexer);
  }

  private LTLFormula A(LTLLexer lexer) {
    List<LTLFormula> components = new ArrayList<>();
    LTLFormula first = B(lexer);
    components.add(first);

    while (lexer.hasNextToken() && lexer.getLookahead().getType() == LTLLexer.OR) {
      lexer.consume(OR);
      components.add(B(lexer));
    }

    if (components.size() == 1) {
      return first;
    }

    return or(components);
  }

  private LTLFormula B(LTLLexer lexer) {
    List<LTLFormula> components = new ArrayList<>();
    LTLFormula first = C(lexer);
    components.add(first);

    while (lexer.hasNextToken() && lexer.getLookahead().getType() == AND) {
      lexer.consume(AND);
      components.add(C(lexer));
    }

    if (components.size() == 1) {
      return first;
    }

    return and(components);
  }

  private LTLFormula C(LTLLexer lexer) {
    LTLFormula formula = D(lexer);

    while (lexer.hasNextToken() && lexer.getLookahead().getType() == UNTIL) {
      lexer.consume(UNTIL);
      LTLFormula second = D(lexer);
      formula = until(formula, second);
    }

    return formula;
  }

  private LTLFormula D(LTLLexer lexer) {
    lexer.requireNextToken();

    Token lookahead = lexer.getLookahead();
    TokenType type = lookahead.getType();

    if (type == NOT) {
      return parseNot(lexer);
    } else if (type == NEXT) {
      return parseNext(lexer);
    } else if (type == ALWAYS) {
      return parseAlways(lexer);
    } else if (type == EVENTUALLY) {
      return parseEventually(lexer);
    } else if (type == TRUE) {
      return parseTrue(lexer);
    } else if (type == FALSE) {
      return parseFalse(lexer);
    } else if (type == LPAREN) {
      return parseParenthesis(lexer);
    } else if (type == IDENTIFIER) {
      return parseIdentifier(lexer);
    }

    throw new SyntaxError(
        "Unexpected token " + type.getName(), lexer.getInput(), lexer.getTokenStart());
  }

  private LTLNotFormula parseNot(LTLLexer lexer) {
    lexer.consume(NOT);
    return not(D(lexer));
  }

  private LTLNextFormula parseNext(LTLLexer lexer) {
    lexer.consume(NEXT);
    return next(A(lexer));
  }

  private LTLAlwaysFormula parseAlways(LTLLexer lexer) {
    lexer.consume(ALWAYS);
    return always(A(lexer));
  }

  private LTLEventuallyFormula parseEventually(LTLLexer lexer) {
    lexer.consume(EVENTUALLY);
    return eventually(A(lexer));
  }

  private LTLTrueFormula parseTrue(LTLLexer lexer) {
    lexer.consume(TRUE);
    return TRUE();
  }

  private LTLFalseFormula parseFalse(LTLLexer lexer) {
    lexer.consume(FALSE);
    return FALSE();
  }

  private LTLFormula parseParenthesis(LTLLexer lexer) {
    lexer.consume(LPAREN);
    LTLFormula formula = A(lexer);
    lexer.consume(RPAREN);
    return parenthesis(formula);
  }

  private LTLIdentifierFormula parseIdentifier(LTLLexer lexer) {
    Token token = lexer.getLookahead();
    lexer.consume(IDENTIFIER);
    return identifier(token.getValue());
  }
}
