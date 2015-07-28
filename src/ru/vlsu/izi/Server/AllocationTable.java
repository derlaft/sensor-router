package ru.vlsu.izi.Server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.*;
import java.util.logging.Logger;

public class AllocationTable {

    HashMap<Integer,Channel> channels = new HashMap<>();
    Topology topology;

    public AllocationTable(Topology topology) {
        this.topology = topology;
    }

    public Integer registerChannel(Channel channel) {
        for (int i = 0; i < topology.size(); i++) {
            if (!channels.containsKey(i)) {
                addChannel(i, channel);
                Logger.getGlobal().info(String.format("Registered client %d", i));

                if (!hasFreeSpace()) {
                    startVoting();
                }
                return i;
            }

        }

        return null;
    }

    private void startVoting() {
        for (int i : channels.keySet()) {
            ByteBuf buf = Unpooled.wrappedBuffer(String.format("-2 %d {}\n", i).getBytes());
            channels.get(i).writeAndFlush(buf).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    public Channel getChannelForClient(int client) {
        return channels.get(client);
    }

    public void unregisterAll(ArrayList<Integer> ids) {
        for (int i : ids) {
            channels.remove(i);
            Logger.getGlobal().info(String.format("Unregistered client %d", i));
        }
    }

    public void sendMessage(Message m) {
        if (topology.isLinked(m.source, m.dest)) {
            ByteBuf buf = Unpooled.wrappedBuffer(m.toString().getBytes());
            channels.get(m.dest).writeAndFlush(buf).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    public void addChannel(int i, Channel c) {
        channels.put(i, c);

        HashSet<Integer> links = topology.getNeighbourList(i);
        String linksStr = Arrays.toString(links.toArray(new Integer[0]));

        ByteBuf buf = Unpooled.wrappedBuffer(
                String.format("-1 %d {\"param\": %d, \"links\": %s}\n", i, topology.params.get(i), linksStr)
                        .getBytes());

        c.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    public boolean hasFreeSpace() {
        return channels.keySet().size() < topology.size();
    }

}
