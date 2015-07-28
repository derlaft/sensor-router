package ru.vlsu.izi.Server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by user on 08.07.15.
 */
public class RouterHandler extends ChannelInboundHandlerAdapter {

    ArrayList<Integer> ids = new ArrayList<>();
    AllocationTable table;

    public RouterHandler(AllocationTable table) {
        this.table = table;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        table.unregisterAll(ids);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message m = (Message) msg;
        Logger.getGlobal().info("Got message");
        if (m.dest == -1) {
            Logger.getGlobal().info("Message is alloc request");
            Integer alloc = Integer.parseInt(m.contents.trim());
            while (alloc-- > 0 && table.hasFreeSpace()) {
                ids.add(table.registerChannel(ctx.channel()));
            }

        } else {
            table.sendMessage(m);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
