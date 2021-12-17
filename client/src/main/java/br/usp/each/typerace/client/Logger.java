package br.usp.each.typerace.client;

/**
 * Logger gerenciar a saida do programa do cliente
 *
 */

public class Logger {

    private Boolean supportsANSI;

    private final String ANSI_RED;
    private final String ANSI_GREEN;

    private final String ANSI_UNDERLINE;
    private final String ANSI_BOLD;

    private final String ANSI_RESET;
    private final String ANSI_CLEAR;

    private final StringBuilder stringToPrint;

    Logger (Boolean supportsANSI) {
        this.supportsANSI = supportsANSI;

        this.ANSI_RED        = "\u001b[31m";
        this.ANSI_GREEN      = "\u001b[32m";

        this.ANSI_UNDERLINE  = "\u001b[4m";
        this.ANSI_BOLD       = "\u001b[1m";

        this.ANSI_RESET      = "\u001b[0m";
        this.ANSI_CLEAR      = "\u001b[H\u001b[2J";

        stringToPrint = new StringBuilder();
    }

    public void append(String text) {
        stringToPrint.append(text);
    }

    private void reset() {
        stringToPrint.setLength(0);
    }

    public void print() {
        System.out.print(stringToPrint);
        this.reset();
    }

    public void println() {
        System.out.println(stringToPrint);
        this.reset();
    }

    private void appendReset() {
        if (this.supportsANSI) stringToPrint.append(this.ANSI_RESET);
    }

    /*
     * Adicionar cor ou modo de saida para uma string
     *
     * @param text      texto para imprimir
     * @param color cor usada para imprimir o texto
     * @param mode modo usado para imprimir o texto
     *
     */

    public void appendEffect(String text, Color color, Mode mode) {
        String colorAppend,
                modeAppend;

        switch (color) {
            case RED:
                colorAppend = this.ANSI_RED;
                break;
            case GREEN:
                colorAppend = this.ANSI_GREEN;
                break;
            default:
                colorAppend = "";
        }

        switch (mode) {
            case BOLD:
                modeAppend = this.ANSI_BOLD;
                break;
            case UNDERLINE:
                modeAppend = this.ANSI_UNDERLINE;
                break;
            default:
                modeAppend = "";
        }

        if (this.supportsANSI) {
            stringToPrint.append(colorAppend);
            stringToPrint.append(modeAppend);
        }
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

    enum Color {
        RED,
        GREEN,
        NONE
    }
    enum Mode {
        BOLD,
        UNDERLINE,
        NONE
    }
}

