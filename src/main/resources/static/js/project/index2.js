"use strict"

class Index2 {

    baseWebsocketServerUrl = "ws://192.168.100.74:80/imageWebsocket";
    websocket = null;

    initUI() {
    }

    initEvent() {
        $("#runStateBt").on("click", () => {
            if ($("#runStateBt").html() === "开始") {
                console.log("hello");
                $("#runStateBt").html("停止");
                this.initWebsocketConnect();
            } else {
                console.log("bye");
                $("#runStateBt").html("开始");
                if (this.websocket) {
                    this.websocket.close();
                    this.websocket = null;
                }
            }
        });
        // 添加图片点击事件监听器
        $("#currentScreenVsk").on("click", (event) => {
            this.handleImageClick(event);
        });
        // 添加双击事件监听器
        $("#currentScreenVsk").on("dblclick", (event) => {
            this.handleImageDoubleClick(event);
        });

        // 添加右键点击事件监听器并阻止默认菜单
        $("#currentScreenVsk").on("contextmenu", (event) => {
            event.preventDefault();
            this.handleImageRightClick(event);
        });
        // 添加键盘事件监听器
        $(document).on("keydown", (event) => {
            this.handleKeyDown(event);
        });
    }

    initWebsocketConnect(){
        // 如果已有连接，先关闭
        if (this.websocket) {
            this.websocket.close();
        }

        // 创建WebSocket连接
        this.websocket = new WebSocket(this.baseWebsocketServerUrl);

        // 监听WebSocket连接打开
        this.websocket.onopen = (event) => {
            console.log("WebSocket连接已建立");
            // 连接建立后发送消息以启动服务端的数据发送
            this.websocket.send("start monitor");
        };

        // 监听WebSocket消息
        this.websocket.onmessage = (event) => {
            // 将接收到的数据更新到图片元素
            $("#currentScreenVsk").attr("src", event.data);
        };

        // 监听WebSocket连接关闭
        this.websocket.onclose = (event) => {
            console.log("WebSocket连接已关闭");
        };

        // 监听WebSocket错误
        this.websocket.onerror = (error) => {
            console.error("WebSocket错误:", error);
        };
    }

    handleImageClick(event) {
        const img = $("#currentScreenVsk")[0];
        const offsetX = event.offsetX;
        const offsetY = event.offsetY;
        const imgWidth = img.width;
        const imgHeight = img.height;

        // 计算相对于图片的坐标比例
        const xRatio = offsetX / imgWidth;
        const yRatio = offsetY / imgHeight;

        // 构造坐标数据对象
        const clickData = {
            type: "mouseClick",
            x: xRatio,
            y: yRatio,
            offsetX: offsetX,
            offsetY: offsetY,
            imgWidth: imgWidth,
            imgHeight: imgHeight
        };

        // 通过WebSocket发送坐标数据到后台
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(clickData.type+","+clickData.x+","+clickData.y);
        } else {
            console.warn("WebSocket未连接，无法发送点击坐标");
        }
    }

    handleImageDoubleClick(event) {
        const img = $("#currentScreenVsk")[0];
        const offsetX = event.offsetX;
        const offsetY = event.offsetY;
        const imgWidth = img.width;
        const imgHeight = img.height;

        // 计算相对于图片的坐标比例
        const xRatio = offsetX / imgWidth;
        const yRatio = offsetY / imgHeight;

        // 构造双击事件数据对象
        const clickData = {
            type: "mouseDoubleClick",
            x: xRatio,
            y: yRatio,
            offsetX: offsetX,
            offsetY: offsetY,
            imgWidth: imgWidth,
            imgHeight: imgHeight
        };

        // 通过WebSocket发送坐标数据到后台
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(clickData.type+","+clickData.x+","+clickData.y);
        } else {
            console.warn("WebSocket未连接，无法发送双击坐标");
        }
    }

    handleImageRightClick(event) {
        const img = $("#currentScreenVsk")[0];
        const offsetX = event.offsetX;
        const offsetY = event.offsetY;
        const imgWidth = img.width;
        const imgHeight = img.height;

        // 计算相对于图片的坐标比例
        const xRatio = offsetX / imgWidth;
        const yRatio = offsetY / imgHeight;

        // 构造右键点击事件数据对象
        const clickData = {
            type: "mouseRightClick",
            x: xRatio,
            y: yRatio,
            offsetX: offsetX,
            offsetY: offsetY,
            imgWidth: imgWidth,
            imgHeight: imgHeight
        };

        // 通过WebSocket发送坐标数据到后台
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(clickData.type+","+clickData.x+","+clickData.y);
        } else {
            console.warn("WebSocket未连接，无法发送右键点击坐标");
        }
    }

    // 处理键盘按下事件
    handleKeyDown(event) {
        // 只阻止特定按键的默认行为，防止与浏览器快捷键冲突
        // 这些按键通常在远程桌面应用中有特殊用途
        const preventDefaultKeys = [
            'Alt',
            'Control',
            'Meta',
            'Shift',
            'F1', 'F2', 'F3', 'F4', 'F5', 'F6', 'F7', 'F8', 'F9', 'F10', 'F11', 'F12'
        ];

        // 如果按下的键在需要阻止默认行为的列表中，则阻止默认行为
        if (preventDefaultKeys.includes(event.key) ||
            (event.ctrlKey && event.key !== 'F5') ||  // 阻止除F5以外的Ctrl组合键
            (event.altKey && event.key.length === 1) || // 阻止Alt+字母组合键
            event.key === 'ContextMenu') { // 阻止上下文菜单键
            event.preventDefault();
        }
        // 构造键盘事件数据对象
        const keyData = {
            type: "keyDown",
            key: event.key,
            keyCode: event.keyCode,
            ctrlKey: event.ctrlKey,
            shiftKey: event.shiftKey,
            altKey: event.altKey
        };

        // 通过WebSocket发送键盘数据到后台
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(keyData.type+","+keyData.key+","+keyData.keyCode+","+keyData.ctrlKey+","+keyData.shiftKey+","+keyData.altKey);
        } else {
            console.warn("WebSocket未连接，无法发送键盘按键");
        }
    }

}

$(document).ready(function () {
    const index = new Index2();
    index.initUI();
    index.initEvent();
});
