package me.paultristanwagner.modelchecking.parse;

import static me.paultristanwagner.modelchecking.Main.OUT;

public class SyntaxError extends Error {

    private String internalMessage;
    private final String input;
    private final int index;

    public SyntaxError(String input, int index) {
        this.input = input;
        this.index = index;
    }

    public SyntaxError(String message, String input, int index) {
        super(message + " at index " + index);
        this.internalMessage = message;
        this.input = input;
        this.index = index;
    }

    public void printWithContext() {
        OUT.println("Syntax Error: " + getMessage());
        OUT.println(input);
        for (int i = 0; i < index; i++) {
            OUT.print(" ");
        }
        OUT.println("^");
    }

    public String getInternalMessage() {
        return internalMessage;
    }

    public String getInput() {
        return input;
    }

    public int getIndex() {
        return index;
    }
}
