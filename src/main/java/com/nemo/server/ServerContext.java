/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/31 14:31
 */
package com.nemo.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Nemo on 2018/1/31.
 */
public class ServerContext {

//    protected static Queue clients = new ConcurrentLinkedQueue();

    private static Queue<ChannelHandlerContext> loginQueue = new ConcurrentLinkedQueue();

    private static Map<String,ChannelHandlerContext> clientMap = new ConcurrentHashMap<>();


    protected static void addLoginQueue(ChannelHandlerContext ctx){
        loginQueue.add(ctx);
    }

    protected static boolean checkInLoginQueue(ChannelHandlerContext ctx){
        return loginQueue.contains(ctx);
    }

    protected static void removeFromLoginQueue(ChannelHandlerContext ctx){
        loginQueue.remove(ctx);
    }

    protected static void addClientMap(String key,ChannelHandlerContext ctx){
        clientMap.put(key,ctx);
    }

    protected static void removeFromClientMap(ChannelHandlerContext ctx){
        boolean isExits = clientMap.containsValue(ctx);
        if(isExits){
            for(String key : clientMap.keySet()){
                ChannelHandlerContext client = clientMap.get(key);
                if(ctx.equals(client)){
                    clientMap.remove(client);
                    break;
                }
            }
        }
    }

    protected static boolean checkInClientMap(ChannelHandlerContext ctx){
        return clientMap.containsValue(ctx);
    }

    protected static ChannelHandlerContext getClientByName(String key){
        return clientMap.get(key);
    }

    public static int getOnlineClientSize(){
        return clientMap.size();
    }

    public static void sendToAll(String msg){
        for(String key : clientMap.keySet()){
            ChannelHandlerContext ctx = clientMap.get(key);
            sendMsg(ctx,msg);
        }
    }

    public static void sendMsg(String key,String msg){
        ByteBuf buf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        ChannelHandlerContext ctx = getClientByName(key);
        sendMsg(ctx,buf);
    }

    public static void sendMsg(ChannelHandlerContext ctx,String msg){
        final ByteBuf buf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        sendMsg(ctx,buf);
    }

    public static void sendMsg(ChannelHandlerContext ctx, ByteBuf buf){
        if(ctx == null){
            return;
        }
        if(ctx.channel().isWritable()){
            ctx.pipeline().writeAndFlush(buf).addListener(future -> {
                if (!future.isSuccess()) {
//                    System.out.println("unexpected push. msg:{} fail:{}"+ msg+ future.cause().getMessage());
                }
            });
        }else{
            try {
                ctx.pipeline().writeAndFlush(buf).sync().addListener(future -> {
                    if (!future.isSuccess()) {
//                        System.out.println("unexpected push. msg:{} fail:{}"+ msg+ future.cause().getMessage());
                    }else{
                        //logger.info("publish macdonaldMsg,sended. remoteAddress:[{}], msg:[{}]", channelHandlerContext.channel().remoteAddress(), msg);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
