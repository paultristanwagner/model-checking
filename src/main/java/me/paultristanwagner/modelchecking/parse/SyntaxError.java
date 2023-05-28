package me.paultristanwagner.modelchecking.parse;

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
        System.out.println("Syntax Error: " + getMessage());
        System.out.println(input);
        for (int i = 0; i < index; i++) {
            System.out.print(" ");
        }
        System.out.println("^");
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
