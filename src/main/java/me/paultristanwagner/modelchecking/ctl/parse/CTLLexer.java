package me.paultristanwagner.modelchecking.ctl.parse;

import me.paultristanwagner.modelchecking.parse.Lexer;

public class CTLLexer extends Lexer {

    public static final String NOT_SYMBOL = "¬";
    public static final String AND_SYMBOL = "∧";
    public static final String OR_SYMBOL = "∨";
    public static final String NEXT_SYMBOL = "◯";
    public static final String UNTIL_SYMBOL = "U";
    public static final String EVENTUALLY_SYMBOL = "◊";
    public static final String ALWAYS_SYMBOL = "□";
    public static final String UNIVERSAL_QUANTIFIER_SYMBOL = "∀";
    public static final String EXISTENTIAL_QUANTIFIER_SYMBOL = "∃";

    public static final TokenType LPAREN = TokenType.of("(", "^\\(");
    public static final TokenType RPAREN = TokenType.of(")", "^\\)");
    public static final TokenType TRUE = TokenType.of("true", "^(true|TRUE)");
    public static final TokenType FALSE = TokenType.of("false", "^(false|FALSE)");
    public static final TokenType NOT = TokenType.of("not", "^(" + NOT_SYMBOL + "|!|not|NOT|~)");
    public static final TokenType AND = TokenType.of("and", "^(" + AND_SYMBOL + "|&|and|AND)");
    public static final TokenType OR = TokenType.of("or", "^(" + OR_SYMBOL + "|\\||or|OR)");
    public static final TokenType NEXT = TokenType.of("next", "^(" + NEXT_SYMBOL + "|next|NEXT|O)");
    public static final TokenType UNTIL = TokenType.of("until", "^(" + UNTIL_SYMBOL + "|until|UNTIL)");
    public static final TokenType EVENTUALLY = TokenType.of("eventually", "^(" + EVENTUALLY_SYMBOL + "|eventually|EVENTUALLY|diamond|DIAMOND)");
    public static final TokenType ALWAYS = TokenType.of("always", "^(" + ALWAYS_SYMBOL + "|always|ALWAYS|box|BOX)");
    public static final TokenType ALL = TokenType.of("all", "^(" + UNIVERSAL_QUANTIFIER_SYMBOL + "|all|ALL|A)");
    public static final TokenType EXISTS = TokenType.of("exists", "^(" + EXISTENTIAL_QUANTIFIER_SYMBOL + "|exists|EXISTS|ex|EX|E)");
    public static final TokenType IDENTIFIER = TokenType.of("identifier", "^[a-z]");

    public CTLLexer(String input) {
        super(input);

        registerTokenTypes(
                LPAREN, RPAREN, TRUE, FALSE, NOT, AND, OR,
                NEXT, UNTIL, EVENTUALLY, ALWAYS, ALL, EXISTS, IDENTIFIER
        );

        this.initialize(input);
    }


}
