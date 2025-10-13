

# java remote project by vsked

http://127.0.0.1  
setInterval循环版本  
http://127.0.0.1/index.html  
websocket实时版本  
http://127.0.0.1/index2.html  
http://192.168.100.74/index2.html  
键盘远程控制请访问类似下面地址  
http://192.168.100.74/press?key=abcde1

如果其他机器访问请修改  
resource/static/js/project/index2.js 文件中websocket连接地址，改成你的内网IP地址  
修改这个变量baseWebsocketServerUrl  

特别说明，一台机器不太好测试单击，双击，右击等相关功能，请在另一台电脑上测试。并修改index2.js文件中IP地址

## TODO 
#### 添加语音支持
#### 添加反向控制支持
#### 添加多个分享屏幕







