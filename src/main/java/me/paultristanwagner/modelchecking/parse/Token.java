package me.paultristanwagner.modelchecking.parse;

public class Token {
  
  private final TokenType type;
  private final String value;
  
  private Token( TokenType type, String value ) {
    this.type = type;
    this.value = value;
  }
  
  public static Token of( TokenType type, String value ) {
    return new Token( type, value );
  }
  
  public TokenType getType() {
    return type;
  }
  
  public String getValue() {
    return value;
  }
}
