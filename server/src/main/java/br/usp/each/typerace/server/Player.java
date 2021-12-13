package br.usp.each.typerace.server;

public class Player {

    private String name;
    private int score;
    private int wrong;
    private int time;

    Player (String name) {
        this.name = name;
        this.score = 0;
        this.wrong = 0;
        this.time = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
