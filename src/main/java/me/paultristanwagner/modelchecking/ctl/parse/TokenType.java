package me.paultristanwagner.modelchecking.ctl.parse;

public class TokenType {
  
  private final String name;
  private final String regex;
  
  private TokenType( String name, String regex) {
    this.name = name;
    this.regex = regex;
  }
  
  public static TokenType of( String name, String regex) {
    return new TokenType( name, regex );
  }
  
  public String getName() {
    return name;
  }
  
  public String getRegex() {
    return regex;
  }
}
