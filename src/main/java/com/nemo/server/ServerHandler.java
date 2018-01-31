/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/29 19:00
 */
package com.nemo.server;

import com.alibaba.fastjson.JSONObject;
import com.nemo.bean.MsgBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Map;

/**
 * Created by Nemo on 2018/1/29.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

        //每次连接的时候添加到登录队列
        ServerContext.addLoginQueue(ctx);
//        System.out.println("The channel is registered." );
    }

    //每个信息入站都会调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String json = buf.toString(CharsetUtil.UTF_8);
        MsgBean msgBean = JSONObject.parseObject(json,MsgBean.class);

        System.out.println("The server receive msg :" + json);
        System.out.println(((Map<String,String>)msgBean.getData()).get("name"));

        //如果在登录queue，则去登录，否则则认为登录完成
        boolean needsLogin = ServerContext.checkInLoginQueue(ctx);
        if(needsLogin){
            login(ctx,msgBean.getData());
        }else{
            //TODO
        }
    }

    private void login(ChannelHandlerContext ctx,Object data){
        Map<String,String> map = (Map<String,String>) data;
        String name = map.get("name");
        String pass = map.get("password");

        if(name == null || pass == null){
            close(ctx);
        }

        if(name.equals("Nemo") && pass.equals("123456")){
            ServerContext.removeFromLoginQueue(ctx);
            ServerContext.addClientMap(name,ctx);
        }else {
            close(ctx);
        }
    }

    private void close(ChannelHandlerContext ctx){
        ServerContext.removeFromLoginQueue(ctx);
        ServerContext.removeFromClientMap(ctx);
        ctx.close();
    }


    //通知处理器最后的channelread()是当前批处理中的最后一条消息时调用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
//        System.out.println("The server receive msg complate.");
    }

    //读操作时捕获到异常时调用
    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        close(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        close(ctx);
//        System.out.println("The channel is unregistered." );
    }

}
