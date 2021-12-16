package br.usp.each.typerace.server;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class TypeRace {

    private boolean running;
    private final Map<String, Player> players;
    private int playersInGame;
    private String[] wordList;

    TypeRace() {
        this.running  = false;
        this.players = new HashMap<String, Player>();
        this.generateList();
        this.playersInGame = 0;
    }

    public void init(Set<String> playerList) {
        this.setPlayers(playerList);
        this.setRunning(true);
        this.running = true;
    }

    public void verifyAnswer(String user, String word) {
        Player player = players.get(user);
        int listIndex = players.get(user).getIndex();
        player.compareWord(wordList[listIndex], word);
        if (listIndex == wordList.length - 1) {
            playerFinished(player);
            if (playersInGame == 0) setRunning(false);
        }
    }

    public String nextWord(String player) {
        int playerIndex = players.get(player).getIndex();
        return wordList[playerIndex];
    }

    public void setPlayers(Set<String> playerList) {
        for (String playerName : playerList) {
            players.put(playerName, new Player(playerName));
        }
        setPlayersInGame(playerList.size());
    }

    public void setPlayersInGame(int playersInGame) {
        this.playersInGame = playersInGame;
    }

    public void playerFinished(Player player) {
        player.finish();
        this.playersInGame--;
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public boolean isRunning() { return this.running; }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void generateList() {
        this.wordList = new String[]{"asdf", "zxcv", "qwer"};
    }

    public String getWord(String player) {
        return wordList[players.get(player).getIndex()];
    }

    public String scoreboard() {

        StringBuilder table = new StringBuilder();
        table.append("PONTUAÇÃO FINAL\n");
        table.append(" _________________________________________________________\n");
        table.append("|_rank_|_name_______________|_palavra/sec_|_score_|_wrong_|\n");
        //            |_4.___|_Ricardo____________|_1,23________|_80____|_20____|

        for(Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            String user = entry.getKey();
            table.append(String.format("| %-4d.| %-19s| %-12.2f| %-6d| %-6d|\n",
                    player.getRanking(),
                    user,
                    player.getVelocity(),
                    player.getScore(),
                    player.getWrong())
                    .replace(" ", "_"));
        }

        return table.toString();
    }
}
