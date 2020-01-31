package com.example.nettyclient.client;


import com.example.nettyclient.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class NettyClient {

    // 要请求的服务器的ip地址
    private String ip = "127.0.0.1";
    // 服务器的端口
    private int port = 9000;

    private SocketChannel socketChannel;

    public NettyClient(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    EventLoopGroup bossGroup = new NioEventLoopGroup();

    @PostConstruct
    public void start(){
        //boss 线程组用于处理连接工作
        Bootstrap bs = new Bootstrap();
        bs.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 处理来自服务端的响应信息
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });

        try {
            ChannelFuture cf = bs.connect(ip,port).sync();
            if (cf.isSuccess()){
                log.info("连接服务器成功");
            }
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
        }
    }
}
