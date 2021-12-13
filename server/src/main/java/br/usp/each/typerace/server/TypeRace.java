package br.usp.each.typerace.server;

import java.util.Map;

public class TypeRace {

    private boolean running;
    private final Map<String, Player> players;

    TypeRace() {

    }

    public boolean isTheGameRunning() { return this.running; }

    public String scoreTable() {

        return "|   |";
    }
}
