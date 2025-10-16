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
import java.awt.event.KeyEvent;
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
        boolean isKeyEvent = false;

        assert GlobalObj.getRobot()!=null;

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

        // 处理键盘事件
        if(message.contains("keyDown")) {
            try {
                String[] arr = message.split(",");
                String key = arr[1];           // 按键字符
                int keyCode = Integer.parseInt(arr[2]);  // 键码
                boolean ctrlKey = Boolean.parseBoolean(arr[3]);   // Ctrl键是否按下
                boolean shiftKey = Boolean.parseBoolean(arr[4]);  // Shift键是否按下
                boolean altKey = Boolean.parseBoolean(arr[5]);    // Alt键是否按下

                // 按下修饰键
                if (ctrlKey) {
                    GlobalObj.getRobot().keyPress(KeyEvent.VK_CONTROL);
                }
                if (shiftKey) {
                    GlobalObj.getRobot().keyPress(KeyEvent.VK_SHIFT);
                }
                if (altKey) {
                    GlobalObj.getRobot().keyPress(KeyEvent.VK_ALT);
                }

                // 按下主键
                GlobalObj.getRobot().keyPress(keyCode);

                // 释放主键
                GlobalObj.getRobot().keyRelease(keyCode);

                // 释放修饰键
                if (ctrlKey) {
                    GlobalObj.getRobot().keyRelease(KeyEvent.VK_CONTROL);
                }
                if (shiftKey) {
                    GlobalObj.getRobot().keyRelease(KeyEvent.VK_SHIFT);
                }
                if (altKey) {
                    GlobalObj.getRobot().keyRelease(KeyEvent.VK_ALT);
                }

                isKeyEvent = true;
            } catch (Exception e) {
                log.error("Error processing keyDown: {}", e.getMessage());
            }
        }

        // 如果是鼠标事件或键盘事件，处理完后继续执行发送任务逻辑
        if (isMouseEvent || isKeyEvent) {
            log.info("Event processed, continuing with image sending,{}",sending);
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
