package me.paultristanwagner.modelchecking.ctlstar.parse;

import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarAllFormula.all;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarAlwaysFormula.always;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarEventuallyFormula.eventually;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarExistsFormula.exists;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarFalseFormula.FALSE;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarIdentifierFormula.identifier;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarNextFormula.next;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarNotFormula.not;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarParenthesisFormula.parenthesis;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarTrueFormula.TRUE;
import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarUntilFormula.until;
import static me.paultristanwagner.modelchecking.ctlstar.parse.CTLStarLexer.*;
import static me.paultristanwagner.modelchecking.ctlstar.parse.CTLStarLexer.AND;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import me.paultristanwagner.modelchecking.ctlstar.formula.*;
import me.paultristanwagner.modelchecking.parse.Lexer;
import me.paultristanwagner.modelchecking.parse.Parser;
import me.paultristanwagner.modelchecking.parse.SyntaxError;

public class CTLStarParser implements Parser<CTLStarFormula> {

  /*
  *
  *    <A> ::= <B>
  *        | <A> OR <B>
  *    <B> ::= <C>
  *        | <B> AND <C>
  *    <C> ::= NOT <C>
  *        | EXISTS <PATH>
  *        | ALL <PATH>
  *        | TRUE
  *        | FALSE
  *        | IDENTIFIER
  *        | '(' <A> ')'
  *
  *    <PATH> ::= <Y>
  *           | <PATH> AND <Y>
  *    <Y> ::= <Z>
  *        | <Y> UNTIL <Z>
  *
  *    <Z> ::= NEXT <Z>
  *        | ALWAYS <Z>
  *        | EVENTUALLY <Z>
  *        | NOT <Z>
  *        | IDENTIFIER
  *        | '(' <PATH> ')'
  *        | <Z> UNTIL <Z>
  *        | <A>

  */
  @Override
  public CTLStarFormula parse(String input, AtomicInteger index) {
    CTLStarLexer lexer = new CTLStarLexer(input);
    lexer.requireNextToken();
    CTLStarFormula formula = parseStateFormula(lexer);
    lexer.requireNoToken();

    return formula;
  }

  private CTLStarFormula parseStateFormula(Lexer lexer) {
    return A(lexer);
  }

  private CTLStarFormula A(Lexer lexer) {
    List<CTLStarFormula> components = new ArrayList<>();
    CTLStarFormula first = B(lexer);
    components.add(first);

    while (lexer.hasNextToken() && lexer.getLookahead().getType() == OR) {
      lexer.consume(OR);
      components.add(B(lexer));
    }

    if (components.size() == 1) {
      return first;
    }

    return CTLStarOrFormula.or(components);
  }

  private CTLStarFormula B(Lexer lexer) {
    List<CTLStarFormula> components = new ArrayList<>();
    CTLStarFormula first = C(lexer);
    components.add(first);

    while (lexer.hasNextToken() && lexer.getLookahead().getType() == AND) {
      lexer.consume(AND);
      components.add(C(lexer));
    }

    if (components.size() == 1) {
      return first;
    }

    return CTLStarAndFormula.and(components);
  }

  private CTLStarFormula C(Lexer lexer) {
    if (lexer.getLookahead().getType() == NOT) {
      lexer.consume(NOT);
      return not(C(lexer));
    } else if (lexer.getLookahead().getType() == EXISTS) {
      lexer.consume(EXISTS);
      return exists(parsePathFormula(lexer));
    } else if (lexer.getLookahead().getType() == ALL) {
      lexer.consume(ALL);
      return all(parsePathFormula(lexer));
    } else if (lexer.getLookahead().getType() == TRUE) {
      lexer.consume(TRUE);
      return TRUE();
    } else if (lexer.getLookahead().getType() == FALSE) {
      lexer.consume(FALSE);
      return FALSE();
    } else if (lexer.getLookahead().getType() == IDENTIFIER) {
      String identifier = lexer.getLookahead().getValue();
      lexer.consume(IDENTIFIER);
      return identifier(identifier);
    } else if (lexer.getLookahead().getType() == LPAREN) {
      return parseParenthesisStateFormula(lexer);
    }

    throw new SyntaxError(
        "Unexpected token " + lexer.getLookahead().getType().getName(),
        lexer.getInput(),
        lexer.getTokenStart());
  }

  private CTLStarFormula parseParenthesisStateFormula(Lexer lexer) {
    lexer.consume(LPAREN);
    CTLStarFormula formula = parseStateFormula(lexer);
    lexer.consume(RPAREN);
    return parenthesis(formula);
  }

  private CTLStarFormula parsePathFormula(Lexer lexer) {
    List<CTLStarFormula> components = new ArrayList<>();
    CTLStarFormula first = Y(lexer);
    components.add(first);

    while (lexer.hasNextToken() && lexer.getLookahead().getType() == AND) {
      lexer.consume(AND);
      components.add(Y(lexer));
    }

    if (components.size() == 1) {
      return first;
    }

    return CTLStarAndFormula.and(components);
  }

  private CTLStarFormula Y(Lexer lexer) {
    CTLStarFormula left = Z(lexer);
    if (lexer.getLookahead() != null && lexer.getLookahead().getType() == UNTIL) {
      lexer.consume(UNTIL);
      CTLStarFormula right = Y(lexer);
      return until(left, right);
    } else {
      return left;
    }
  }

  private CTLStarFormula Z(Lexer lexer) {
    if (lexer.getLookahead().getType() == NEXT) {
      lexer.consume(NEXT);
      return next(Z(lexer));
    } else if (lexer.getLookahead().getType() == NOT) {
      lexer.consume(NOT);
      return not(Z(lexer));
    } else if (lexer.getLookahead().getType() == EVENTUALLY) {
      lexer.consume(EVENTUALLY);
      return eventually(Z(lexer));
    } else if (lexer.getLookahead().getType() == ALWAYS) {
      lexer.consume(ALWAYS);
      return always(Z(lexer));
    } else if (lexer.getLookahead().getType() == LPAREN) {
      return parseParenthesisPathFormula(lexer);
    } else if (lexer.getLookahead().getType() == IDENTIFIER) {
      String identifier = lexer.getLookahead().getValue();
      lexer.consume(IDENTIFIER);
      return identifier(identifier);
    } else {
      CTLStarFormula left = A(lexer);
      if (lexer.getLookahead() != null && lexer.getLookahead().getType() == UNTIL) {
        lexer.consume(UNTIL);
        CTLStarFormula right = Z(lexer);
        return until(left, right);
      } else {
        return left;
      }
    }
  }

  private CTLStarFormula parseParenthesisPathFormula(Lexer lexer) {
    lexer.consume(LPAREN);
    CTLStarFormula formula = parsePathFormula(lexer);
    lexer.consume(RPAREN);
    return parenthesis(formula);
  }
}
