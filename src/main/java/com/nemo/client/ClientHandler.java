/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/29 19:03
 */
package com.nemo.client;

import com.alibaba.fastjson.JSONObject;
import com.nemo.bean.MsgBean;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nemo on 2018/1/29.
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx){

        MsgBean msgBean = new MsgBean();
        Map<String,String> map = new HashMap<>();
        map.put("name","Nemo");
        map.put("password","123456");
        msgBean.setData(map);
        msgBean.setType("login");

        ctx.writeAndFlush(Unpooled.copiedBuffer(JSONObject.toJSONString(msgBean), CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        System.out.println("Client received: "+in.toString(CharsetUtil.UTF_8));
        Thread.sleep(100);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("Are you OK?", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}
