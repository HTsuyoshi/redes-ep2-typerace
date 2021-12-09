package br.usp.each.typerace.client;

public class Logger {

    private Boolean supportsANSI;

    private String ANSI_RED;
    private String ANSI_GREEN;
    private String ANSI_RESET;
    private String ANSI_CLEAR;

    private StringBuilder stringToPrint;

    Logger (Boolean supportsANSI) {
        this.supportsANSI = supportsANSI;

        this.ANSI_RED    = "\u001b[31m";
        this.ANSI_GREEN  = "\u001b[32m";
        this.ANSI_RESET  = "\u001b[0m";
        this.ANSI_CLEAR  = "\u001b[H\u001b[2J";

        stringToPrint = new StringBuilder();
    }

    public void append(String text) {
        stringToPrint.append(text);
    }

    private void reset() {
        stringToPrint.setLength(0);
    }

    public void print() {
        System.out.print(stringToPrint.toString());
        this.reset();
    }

    public void println(String text) {
        stringToPrint.append(text);
        System.out.println(stringToPrint.toString());
        this.reset();
    }
    public void println() {
        System.out.println(stringToPrint.toString());
        this.reset();
    }

    private void appendReset() {
        if (this.supportsANSI) stringToPrint.append(this.ANSI_RESET);
    }

    public void appendErr(String text) {
        if (this.supportsANSI) stringToPrint.append(this.ANSI_RED);
        stringToPrint.append(text);
        this.appendReset();
    }

    public void appendGood(String text) {
        if (this.supportsANSI) stringToPrint.append(this.ANSI_GREEN);
        stringToPrint.append(text);
        this.appendReset();
    }

    public void clear() {
        if (supportsANSI) System.out.println(this.ANSI_CLEAR);
        else System.out.println();
    }

    public void setSupportsANSI(Boolean supportsANSI) {
        this.supportsANSI = supportsANSI;
    }

    public boolean getSupportsANSI() {
        return supportsANSI;
    }

}
