package ru.vlsu.izi.Server;

/**
 * Created by user on 08.07.15.
 */
public class Message {
    public int source;
    public int dest;
    String contents;

    public Message(int source, int dest, String contents) {
        this.source = source;
        this.dest = dest;
        this.contents = contents;
    }

    @Override
    public String toString() {
        return String.format("%d %d %s", source, dest, contents);
    }
}
