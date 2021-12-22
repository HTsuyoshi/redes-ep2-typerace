package br.usp.each.typerace.server;

/**
 * Player representar e armazenar as informacoes do jogador
 */

public class Player {

    private final String user;
    private boolean playing;
    private int score;
    private int wrong;
    private int listIndex;
    private long time;

    Player (String user) {
        this.user = user;
        this.playing = true;
        this.score = 0;
        this.wrong = 0;
        this.listIndex = 0;
        this.time = 0;
    }

    /**
     * compareWord compara a palavra, contabiliza o ponto
     * e avanca para a proxima palavra da lista
     */

    public void compareWord(String correct, String userInput) {
        if (correct.equals(userInput)) addScore();
        else addWrong();

        addListIndex();
    }

    public void finish(long startTime) {
        this.time = System.nanoTime() - startTime;
        setPlaying(false);
    }

    /* Getter and Setters */

    public int getScore() {
        return this.score;
    }

    public void addScore() {
        score++;
    }

    public int getWrong() {
        return this.wrong;
    }

    public void addWrong() {
        wrong++;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void addListIndex() {
        this.listIndex++;
    }

    public void resetListIndex() {
        this.listIndex = 0;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public float getVelocity() {
        if (getTimeSeconds() == 0) return 0;
        return ((score + wrong) / getTimeSeconds());
    }

    public String getUser() {
        return this.user;
    }

    public long getTime() {
        return this.time;
    }

    public float getTimeSeconds() {
        return  ((float) getTime() / ((long)(1_000_000_000.0)));
    }
}