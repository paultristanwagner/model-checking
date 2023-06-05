package me.paultristanwagner.modelchecking.ltl.parse;

import me.paultristanwagner.modelchecking.ltl.formula.*;
import me.paultristanwagner.modelchecking.parse.Parser;
import me.paultristanwagner.modelchecking.parse.SyntaxError;
import me.paultristanwagner.modelchecking.parse.Token;
import me.paultristanwagner.modelchecking.parse.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.paultristanwagner.modelchecking.ltl.parse.LTLLexer.*;

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

        if (!lexer.hasNextToken()) {
            throw new SyntaxError("Unexpected end of input", input, index.get());
        }

        LTLFormula formula = parseLTLFormula(lexer);
        if (lexer.hasNextToken()) {
            Token token = lexer.getLookahead();
            throw new SyntaxError("Unexpected token " + token.getType().getName(), lexer.getInput(), lexer.getCursor() - token.getValue().length());
        }

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

        return LTLOrFormula.of(components);
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

        return LTLAndFormula.of(components);
    }

    private LTLFormula C(LTLLexer lexer) {
        LTLFormula formula = D(lexer);

        while (lexer.hasNextToken() && lexer.getLookahead().getType() == UNTIL) {
            lexer.consume(UNTIL);
            LTLFormula second = D(lexer);
            formula = LTLUntilFormula.of(formula, second);
        }

        return formula;
    }

    private LTLFormula D(LTLLexer lexer) {
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
        } else {
            throw new SyntaxError("Unexpected token " + type.getName(), lexer.getInput(), lexer.getCursor() - lookahead.getValue().length());
        }
    }

    private LTLNotFormula parseNot(LTLLexer lexer) {
        lexer.consume(NOT);
        return LTLNotFormula.of(D(lexer));
    }

    private LTLNextFormula parseNext(LTLLexer lexer) {
        lexer.consume(NEXT);
        return LTLNextFormula.of(A(lexer));
    }

    private LTLAlwaysFormula parseAlways(LTLLexer lexer) {
        lexer.consume(ALWAYS);
        return LTLAlwaysFormula.of(A(lexer));
    }

    private LTLEventuallyFormula parseEventually(LTLLexer lexer) {
        lexer.consume(EVENTUALLY);
        return LTLEventuallyFormula.of(A(lexer));
    }

    private LTLTrueFormula parseTrue(LTLLexer lexer) {
        lexer.consume(TRUE);
        return new LTLTrueFormula();
    }

    private LTLFalseFormula parseFalse(LTLLexer lexer) {
        lexer.consume(FALSE);
        return new LTLFalseFormula();
    }

    private LTLFormula parseParenthesis(LTLLexer lexer) {
        lexer.consume(LPAREN);
        LTLFormula formula = A(lexer);
        lexer.consume(RPAREN);
        return LTLParenthesisFormula.of(formula);
    }

    private LTLIdentifierFormula parseIdentifier(LTLLexer lexer) {
        Token token = lexer.getLookahead();
        lexer.consume(IDENTIFIER);
        return LTLIdentifierFormula.of(token.getValue());
    }
}
