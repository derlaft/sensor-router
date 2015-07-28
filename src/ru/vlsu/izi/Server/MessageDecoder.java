package ru.vlsu.izi.Server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (in.readableBytes() > 0) {
            int read = in.forEachByte(new ByteBufProcessor() {
                @Override
                public boolean process(byte value) throws Exception {
                    if (value == '\n') {
                        return false;
                    }
                    return true;
                }
            });

            if (read > 0) {
                in.resetReaderIndex();
                String tmp[] = in.readBytes(read + 1).toString(Charset.defaultCharset()).split(" ", 3);
                in.discardReadBytes();

                out.add(new Message(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), tmp[2]));
            } else {
                break;
            }


        }

        if (true) return;
        for (int i = 0; i < in.readableBytes(); i++) {
            if (in.getByte(i) == '\n') {
                break;
            }
        }
    }
}