package br.usp.each.typerace.server;

import java.util.*;

public class TypeRace {

    private long startTime;
    private boolean running;
    private int playersPlaying;
    private final Map<String, Player> players;
    private String[] wordList;

    TypeRace() {
        this.startTime = 0;
        this.running = false;
        this.playersPlaying = 0;
        this.players = new HashMap<String, Player>();
        this.generateList();
    }

    public void init(Set<String> playerList) {
        this.setRunning(true);
        this.setPlayers(playerList);
        this.startTimer();
    }

    public void setPlayers(Set<String> playerList) {
        for (String playerName : playerList) {
            players.put(playerName, new Player(playerName));
        }
        setPlayersPlaying(playerList.size());
    }

    public void setPlayersPlaying(int playersInGame) {
        this.playersPlaying= playersInGame;
    }

    public boolean isRunning() { return this.running; }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void verifyAnswer(String user, String word) {
        Player player = players.get(user);
        int listIndex = player.getIndex();

        player.compareWord(wordList[listIndex], word);

        if (listIndex == wordList.length - 1) {
            playerFinished(player);
            if (playersPlaying == 0) setRunning(false);
        }
    }

    public String nextWord(String player) {
        return wordList[players.get(player).getIndex()];
    }

    public void playerFinished(Player player) {
        player.finish(startTime);
        this.playersPlaying--;
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public void generateList() {
        this.wordList = new String[]{"asdf", "zxcv", "qwer"};
    }

    public String getWord(String player) {
        return wordList[players.get(player).getIndex()];
    }

    public String scoreboard() {

        PriorityQueue<Player> orderedList = new PriorityQueue<Player>(new PlayerComparator());
        StringBuilder table = new StringBuilder();
        table.append("                   PONTUAÇÃO FINAL\n");
        table.append(" _________________________________________________________\n");
        table.append("|_rank_|_name_______________|_palavra/sec_|_score_|_wrong_|\n");
        //            |_4.___|_Ricardo____________|_1,23________|_80____|_20____|

        for(Player player : players.values()) {
            orderedList.add(player);
        }

        int i = 0;
        for(Player player : orderedList) {
            table.append(String.format("| %-4d.| %-19s| %-12.2f| %-6d| %-6d|\n",
                    i++,
                    player.getUser(),
                    player.getVelocity(),
                    player.getScore(),
                    player.getWrong())
                    .replace(" ", "_"));
        }

        return table.toString();
    }
}

class PlayerComparator implements Comparator<Player> {
    @Override
    public int compare(Player player1, Player player2) {
        if(player1.getScore() > player2.getScore()) return -1;
        if(player1.getScore() < player2.getScore()) return 1;
        if(player1.getTime() > player2.getTime()) return 1;
        if(player1.getTime() < player2.getTime()) return -1;
        return player1.getUser().compareTo(player2.getUser());
    }
}

