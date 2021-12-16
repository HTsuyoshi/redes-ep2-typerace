package br.usp.each.typerace.server;

public class Player {

    private String name;
    private int ranking;
    private int score;
    private int wrong;
    private int listIndex;
    private long startTime;
    private long endTime;
    private boolean playing;

    Player (String name) {

        this.name = name;
        this.ranking = -1;
        this.score = 0;
        this.wrong = 0;
        this.listIndex = 0;
        this.startTime = System.nanoTime();
        this.endTime = -1;
        this.playing = true;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void finish() {
        this.playing = false;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getRanking() {
        return this.ranking;
    }

    public int getIndex() {
       return listIndex;
    }

    public void endGame() {
        this.endTime = System.nanoTime();
    }

    public String getName() {
        return name;
    }

    public float getVelocity() {
        long timeSeconds = (this.endTime - this.startTime) / ((long)(1_000_000_000.0));
        return ((score + wrong) / timeSeconds); // words/second
    }

    public int getScore() {
        return this.score;
    }

    public int getWrong() {
        return this.wrong;
    }

    public void addScore() {
        score++;
    }

    public void addWrong() {
        wrong++;
    }

    public void compareWord(String correct, String word) {
        if (correct.equals(word)) {
            this.addScore();
        } else {
            this.addWrong();
        }

        listIndex++;
    }
}
