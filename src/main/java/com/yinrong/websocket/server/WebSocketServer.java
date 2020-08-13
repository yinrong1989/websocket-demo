package com.yinrong.websocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;



@ServerEndpoint("/websocket/{userId}")
@Component
public class WebSocketServer {
  static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

  private static   AtomicInteger  onlineCount = new AtomicInteger(0);

  private static ConcurrentHashMap<String,WebSocketServer> webSocketSet = new ConcurrentHashMap<>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
  private Session session;

  private String userId;

    /**
     * 连接建立成功调用的方法*/
  @OnOpen
  public void onOpen(@PathParam(value = "userId")String param, Session WebSocketsession, EndpointConfig endpointConfig){
      userId = param;
      this.session =WebSocketsession;
      webSocketSet.put(param,this);
      int cnt = onlineCount.incrementAndGet();
      logger.info("有连接加入，当前连接数为：{}",cnt);
      sendMessage(this.session,"连接成功");

  }

    private void sendMessage(Session session, String message) {

        try {
            session.getBasicRemote().sendText(String.format("%s(From Server，Session ID=%s)",message,session.getId()));
        } catch (IOException e) {
            logger.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }


    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (!userId.equals("")){
            webSocketSet.remove(userId);//从set中删除
            int cnt = onlineCount.decrementAndGet();
            logger.info("有连接关闭，当前连接数为：{}", cnt);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("来自客户端的消息：{}",message);
        sendMessage(session, "收到消息，消息内容："+message);
    }
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误：{}，Session ID： {}",error.getMessage(),session.getId());
        error.printStackTrace();
    }

    /**
     * 群发消息
     * @param message
     * @throws IOException
     */
    public void broadCastInfo(String message) {
        for (String key : webSocketSet.keySet()) {
            Session session = webSocketSet.get(key).session;
            if(session != null && session.isOpen() && !key.equals(userId)){
                sendMessage(session, message);
            }
        }
    }
    /**
     * 指定Session发送消息
     * @param message
     * @throws IOException
     */
    public void sendToUser(String userId, String message) {
        WebSocketServer webSocketServer = webSocketSet.get(userId);
        if ( webSocketServer != null && webSocketServer.session.isOpen()){
            sendMessage(webSocketServer.session, message);
        }
        else{
            logger.warn("当前用户不在线：{}",userId);
        }
    }

    public WebSocketServer() {
    }
}
