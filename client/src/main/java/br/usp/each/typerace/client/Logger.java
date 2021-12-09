package br.usp.each.typerace.client;

public class Logger {

    private Boolean supportsANSI;
    private String ANSI_RED;
    private String ANSI_GREEN;
    private String ANSI_RESET;
    private String ANSI_CLEAR;

    Logger (Boolean supportsANSI) {
        this.supportsANSI = supportsANSI;

        this.ANSI_RED    = "\u001b[31m";
        this.ANSI_GREEN  = "\u001b[32m";
        this.ANSI_RESET  = "\u001b[0m";
        this.ANSI_CLEAR  = "\u001b[H\u001b[2J";
    }

    public void setSupportsANSI(Boolean supportsANSI) {
        this.supportsANSI = supportsANSI;
    }

    public boolean getSupportsANSI() {
        return supportsANSI;
    }

    public void printErr(String text) {
        if (supportsANSI) text = this.ANSI_RED + text + this.ANSI_RESET;
        System.out.println(text);
    }

    public void printGood(String text) {
        if (supportsANSI) text = this.ANSI_GREEN + text + this.ANSI_RESET;
        System.out.println(text);
    }

    public void emptyLine() {
        System.out.println();
    }

    public void clear() {
        if (supportsANSI) System.out.println(this.ANSI_CLEAR);
        else System.out.println();
    }
}
