package com.vsked.web;

import com.vsked.remote.GlobalObj;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.awt.event.InputEvent;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/imageWebsocket")
public class ImageWebSocket {

    private static final Logger log = LoggerFactory.getLogger(ImageWebSocket.class);

    private static List<Session> sessions = new CopyOnWriteArrayList<>();
    private static volatile boolean sending = false;
    private static Thread sendingThread = null;

    @OnOpen
    public void onOpen(Session session) {
        log.info("New connection: {}", session.getId());
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Connection closed: {}", session.getId());
        Iterator<Session> it = sessions.iterator();
        Session s;
        while (it.hasNext()) {
            s = it.next();
            if (s.getId().equals(session.getId())) {
                it.remove();
            }
        }

        // 如果没有会话了，停止发送数据
        if (sessions.isEmpty()) {
            sending = false;
            if (sendingThread != null) {
                sendingThread.interrupt();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Error: {}", throwable.getMessage());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("Received message: {}", message);

        double baseWith=GlobalObj.getRe().getWidth();
        double baseHeight=GlobalObj.getRe().getHeight();

        boolean isMouseEvent = false;

        if(message.contains("mouseClick")){
            try {
                String[] arr=message.split(",");
                int myX= (int) (baseWith*Double.parseDouble(arr[1]));
                int myY= (int) (baseHeight*Double.parseDouble(arr[2]));
                GlobalObj.getRobot().mouseMove( myX,myY);
                GlobalObj.getRobot().mousePress(InputEvent.BUTTON1_DOWN_MASK);
                GlobalObj.getRobot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                isMouseEvent = true;
            } catch (Exception e) {
                log.error("Error processing mouseClick: {}", e.getMessage());
            }
        }

        if(message.contains("mouseRightClick")){
            try {
                String[] arr=message.split(",");
                int myX= (int) (baseWith*Double.parseDouble(arr[1]));
                int myY= (int) (baseHeight*Double.parseDouble(arr[2]));
                GlobalObj.getRobot().mouseMove(myX,myY);
                GlobalObj.getRobot().mousePress(InputEvent.BUTTON3_DOWN_MASK);
                GlobalObj.getRobot().mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                isMouseEvent = true;
            } catch (Exception e) {
                log.error("Error processing mouseRightClick: {}", e.getMessage());
            }
        }

        if(message.contains("mouseDoubleClick")){
            try {
                String[] arr=message.split(",");
                int myX= (int) (baseWith*Double.parseDouble(arr[1]));
                int myY= (int) (baseHeight*Double.parseDouble(arr[2]));
                GlobalObj.getRobot().mouseMove(myX,myY);
                GlobalObj.getRobot().mousePress(InputEvent.BUTTON1_DOWN_MASK);
                GlobalObj.getRobot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                GlobalObj.getRobot().delay(50);  // 短暂延迟，模拟真实双击
                GlobalObj.getRobot().mousePress(InputEvent.BUTTON1_DOWN_MASK);
                GlobalObj.getRobot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                isMouseEvent = true;
            } catch (Exception e) {
                log.error("Error processing mouseDoubleClick: {}", e.getMessage());
            }
        }

        // 如果是鼠标事件，处理完后继续执行发送任务逻辑
        if (isMouseEvent) {
            log.info("Mouse event processed, continuing with image sending,{}",sending);
            sending=false;
        }

        // 如果已经在发送数据，则停止之前的发送任务
        if (sending) {
            sending = false;
            if (sendingThread != null) {
                try {
                    sendingThread.join(); // 等待线程结束
                } catch (InterruptedException e) {
                    log.error("Thread join interrupted: {}", e.getMessage());
                }
            }
        }

        // 启动新的发送任务
        sending = true;
        sendingThread = new Thread(() -> {
            while (sending) {
                try {
                    for (Session s : sessions) {
                        try {
                            if(s.isOpen()){
                                s.getBasicRemote().sendText(GlobalObj.getImg());
                            }else {
                                //如果会话已关闭，则从sessions中移除
                                sessions.remove(s);
                            }

                        } catch (Exception e) {
                            log.error("Error sending message: {}", e.getMessage());
                        }
                    }
//                    Thread.sleep(1500); // 每隔1.5秒发送一次
                } catch (Exception e) {
                    log.info("Sending thread interrupted");
                    break;
                }
            }
        });
        sendingThread.start();
    }

}
