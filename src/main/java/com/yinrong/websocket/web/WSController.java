package com.yinrong.websocket.web;

import com.yinrong.websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WSController {
    static Logger logger = LoggerFactory.getLogger(WSController.class);

    @Autowired
    WebSocketServer webSocketServer;

    /**
     * 群发消息内容
     *
     * @param message
     * @return
     */
    @RequestMapping(value = "/ws/sendAll", method = RequestMethod.GET)
    public String sendAllMessage(@RequestParam(required = true) String message) {
        try {
            webSocketServer.broadCastInfo(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 指定会话ID发消息
     *
     * @param message 消息内容
     * @param userId      连接会话ID
     * @return
     */
    @RequestMapping(value = "/ws/sendOne", method = RequestMethod.GET)
    public String sendOneMessage(@RequestParam(required = true) String message,
                                 @RequestParam(required = true) String userId) {
        try {
            webSocketServer.sendToUser(userId, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
}