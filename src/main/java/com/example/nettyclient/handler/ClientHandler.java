package com.example.nettyclient.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 连接到服务端的时候调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        byte[] req = "I love you".getBytes("UTF-8");
        ByteBuf first = Unpooled.buffer(req.length);
        first.writeBytes(req);
        ctx.writeAndFlush(first).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    /**
     * 一个是读取服务端的信息调用。
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req,"UTF-8");
        if(body != null){
            ByteBuf resp = Unpooled.copiedBuffer("不灰心不丧气，继续我爱你".getBytes());
            ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }
}
