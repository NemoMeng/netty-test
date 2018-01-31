/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/31 19:02
 */
package com.nemo.controller;

import com.nemo.server.ServerContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Nemo on 2018/1/31.
 */
@Controller
@RequestMapping("")
public class IndexController {

    @RequestMapping
    public Object index(Model model){
        return "index";
    }

    @ResponseBody
    @PostMapping("push")
    public String push(String name, String msg){
        ServerContext.sendMsg(name,msg);
        return "0";
    }

}
