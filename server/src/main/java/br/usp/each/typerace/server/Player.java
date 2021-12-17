package br.usp.each.typerace.server;

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

    public void compareWord(String correct, String word) {
        if (correct.equals(word)) addScore();
        else addWrong();

        addIndex();
    }

    public void finish(long startTime) {
        this.time = System.nanoTime() - startTime;
        setPlaying(false);
    }

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


    public boolean isPlaying() {
        return this.playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public int getIndex() {
       return listIndex;
    }

    public void addIndex() {
        this.listIndex++;
    }
    public long getTime() {
        return this.time;
    }

    public float getTimeSeconds() {
        return  ((float) getTime() / ((long)(1_000_000_000.0)));
    }

    public String getUser() {
        return this.user;
    }

    public float getVelocity() {
        return ((score + wrong) / getTimeSeconds());
    }

}