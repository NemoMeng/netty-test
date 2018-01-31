/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/31 19:04
 */
package com.nemo;

import com.nemo.client.Client;
import com.nemo.server.EchoServer;
import com.nemo.server.ServerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by Nemo on 2018/1/31.
 */
@SpringBootApplication
public class StartUp extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(StartUp.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StartUp.class, args);
        openServer();
        testOpenClient();
    }

    private static void testOpenClient(){
        new Thread(new Runnable() {
            public void run() {
                final String host = "127.0.0.1";
                final int port = 4567;;
                try {
                    new Client(host, port).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void openServer() throws Exception {
        int port = 4567;
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    System.out.println("在线设备数量：" + ServerContext.getOnlineClientSize());
                    try {
                        Thread.sleep(2000);;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new EchoServer().bind(port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
