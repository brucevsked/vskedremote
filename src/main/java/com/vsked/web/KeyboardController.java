package com.vsked.web;

import java.awt.event.KeyEvent;
import java.awt.Robot;
import com.vsked.remote.GlobalObj;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/press")
@RestController
public class KeyboardController {
    @GetMapping
    public String press(@RequestParam("key") String key) {
        if (key != null && !key.isEmpty()) {
            Robot robot = GlobalObj.getRobot();
            for (int i = 0; i < key.length(); i++) {
                char c = key.charAt(i);
                pressKey(robot, c);
                // 在按键之间添加时间间隔
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return "OK";
    }

    private void pressKey(Robot robot, char key) {
        int keyCode = getKeyCode(key);
        if (keyCode != -1) {
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }
    }

    private int getKeyCode(char c) {
        // 处理字母
        if (c >= 'A' && c <= 'Z') {
            return KeyEvent.VK_A + (c - 'A');
        }
        if (c >= 'a' && c <= 'z') {
            return KeyEvent.VK_A + (c - 'a');
        }
        // 处理数字
        if (c >= '0' && c <= '9') {
            return KeyEvent.VK_0 + (c - '0');
        }
        // 处理特殊字符
        switch (c) {
            case ' ':
                return KeyEvent.VK_SPACE;
            case '\n':
                return KeyEvent.VK_ENTER;
            case '\t':
                return KeyEvent.VK_TAB;
            case '.':
                return KeyEvent.VK_PERIOD;
            case ',':
                return KeyEvent.VK_COMMA;
            case ';':
                return KeyEvent.VK_SEMICOLON;
            case ':':
                return KeyEvent.VK_COLON;
            case '?':
                return KeyEvent.VK_SLASH; // 需要配合Shift键
            case '!':
                return KeyEvent.VK_1; // 需要配合Shift键
            case '@':
                return KeyEvent.VK_2; // 需要配合Shift键
            case '#':
                return KeyEvent.VK_3; // 需要配合Shift键
            case '$':
                return KeyEvent.VK_4; // 需要配合Shift键
            case '%':
                return KeyEvent.VK_5; // 需要配合Shift键
            case '^':
                return KeyEvent.VK_6; // 需要配合Shift键
            case '&':
                return KeyEvent.VK_7; // 需要配合Shift键
            case '*':
                return KeyEvent.VK_8; // 需要配合Shift键
            case '(':
                return KeyEvent.VK_9; // 需要配合Shift键
            case ')':
                return KeyEvent.VK_0; // 需要配合Shift键
            case '-':
                return KeyEvent.VK_MINUS;
            case '_':
                return KeyEvent.VK_MINUS; // 需要配合Shift键
            case '=':
                return KeyEvent.VK_EQUALS;
            case '+':
                return KeyEvent.VK_EQUALS; // 需要配合Shift键
            case '[':
                return KeyEvent.VK_OPEN_BRACKET;
            case ']':
                return KeyEvent.VK_CLOSE_BRACKET;
            case '{':
                return KeyEvent.VK_OPEN_BRACKET; // 需要配合Shift键
            case '}':
                return KeyEvent.VK_CLOSE_BRACKET; // 需要配合Shift键
            case '\\':
                return KeyEvent.VK_BACK_SLASH;
            case '|':
                return KeyEvent.VK_BACK_SLASH; // 需要配合Shift键
            case '\'':
                return KeyEvent.VK_QUOTE;
            case '"':
                return KeyEvent.VK_QUOTE; // 需要配合Shift键
            case '/':
                return KeyEvent.VK_SLASH;
            default:
                return -1; // 未知字符
        }
    }
}
