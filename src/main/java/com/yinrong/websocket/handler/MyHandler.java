package com.yinrong.websocket.handler;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

public class MyHandler extends TextWebSocketHandler {
   protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage textMessage)throws Exception{
       String playLoad =textMessage.getPayload();
       System.out.println("接收到的数据："+playLoad);
       Map<String,Object> map =JSONObject.parseObject(playLoad);
       webSocketSession.sendMessage(new TextMessage("服务器返回的数据"+playLoad));

   }
}
