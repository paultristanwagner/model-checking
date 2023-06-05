package me.paultristanwagner.modelchecking.ctl.parse;

import me.paultristanwagner.modelchecking.ctl.formula.path.*;
import me.paultristanwagner.modelchecking.ctl.formula.state.*;
import me.paultristanwagner.modelchecking.parse.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.*;

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
   *         | '(' <A> UNTIL <A> ')'
   *         | <A> UNTIL <A>
   *
   */

  @Override
  public CTLFormula parse(String input, AtomicInteger index) {
    CTLLexer lexer = new CTLLexer(input);

    if(!lexer.hasNextToken()) {
      throw new SyntaxError("Unexpected end of input", input, index.get());
    }

    CTLFormula formula = parseStateFormula(lexer);
    if(lexer.hasNextToken()) {
      Token token = lexer.getLookahead();
      throw new SyntaxError("Unexpected token " + token.getType().getName(), lexer.getInput(), lexer.getCursor() - token.getValue().length());
    }
    
    return formula;
  }

  private CTLFormula parseStateFormula( Lexer lexer) {
    return A(lexer);
  }

  private CTLFormula A( Lexer lexer) {
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

    return CTLOrFormula.of(components);
  }

  private CTLFormula B( Lexer lexer) {
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

    return CTLAndFormula.of(components);
  }

  private CTLFormula C( Lexer lexer) {
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

  private CTLNotFormula parseNotFormula( Lexer lexer) {
    lexer.consume(NOT);

    CTLFormula argument = C(lexer);
    return CTLNotFormula.of(argument);
  }

  private CTLAllFormula parseAllFormula( Lexer lexer) {
    lexer.consume(ALL);

    CTLPathFormula pathFormula = parsePathFormula(lexer);
    return CTLAllFormula.of(pathFormula);
  }

  private CTLExistsFormula parseExistsFormula( Lexer lexer) {
    lexer.consume(EXISTS);

    CTLPathFormula pathFormula = parsePathFormula(lexer);
    return CTLExistsFormula.of(pathFormula);
  }

  private CTLFormula parseParenthesisFormula( Lexer lexer) {
    lexer.consume(LPAREN);
    CTLFormula inner = parseStateFormula(lexer);
    lexer.consume(RPAREN);

    return CTLParenthesisFormula.of(inner);
  }

  private CTLTrueFormula parseTrueFormula( Lexer lexer) {
    lexer.consume(TRUE);

    return new CTLTrueFormula();
  }

  private CTLFalseFormula parseFalseFormula( Lexer lexer) {
    lexer.consume(FALSE);

    return new CTLFalseFormula();
  }

  private CTLIdentifierFormula parseIdentifierFormula( Lexer lexer) {
    Token lookahead = lexer.getLookahead();

    String identifier = lookahead.getValue();
    lexer.consume(IDENTIFIER);

    return CTLIdentifierFormula.of(identifier);
  }

  private CTLPathFormula parsePathFormula( Lexer lexer) {
    Token lookahead = lexer.getLookahead();
    TokenType type = lookahead.getType();
    if ( type.equals( NEXT ) ) {
      return parseNextFormula( lexer );
    } else if ( type.equals( EVENTUALLY ) ) {
      return parseEventuallyFormula( lexer );
    } else if ( type.equals( ALWAYS ) ) {
      return parseAlwaysFormula( lexer );
    } else if( type.equals( LPAREN )) {
      lexer.consume( LPAREN );
      CTLUntilFormula untilFormula = parseUntilFormula( lexer );
      lexer.consume( RPAREN );
      return untilFormula;
    }
    
    return parseUntilFormula( lexer );
  }

  private CTLNextFormula parseNextFormula( Lexer lexer) {
    lexer.consume(NEXT);

    CTLFormula stateFormula = parseStateFormula(lexer);
    return CTLNextFormula.of(stateFormula);
  }

  private CTLUntilFormula parseUntilFormula( Lexer lexer) {
    CTLFormula left = parseStateFormula(lexer);

    lexer.consume(UNTIL);

    CTLFormula right = parseStateFormula(lexer);

    return CTLUntilFormula.of(left, right);
  }
  
  private CTLEventuallyFormula parseEventuallyFormula( Lexer lexer) {
    lexer.consume( EVENTUALLY );
    
    CTLFormula stateFormula = parseStateFormula( lexer );
    return CTLEventuallyFormula.of(stateFormula);
  }
  
  private CTLAlwaysFormula parseAlwaysFormula( Lexer lexer) {
    lexer.consume( ALWAYS );
    
    CTLFormula stateFormula = parseStateFormula( lexer );
    return CTLAlwaysFormula.of(stateFormula);
  }
}
