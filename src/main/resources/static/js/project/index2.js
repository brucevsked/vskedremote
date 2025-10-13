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

}

$(document).ready(function () {
    const index = new Index2();
    index.initUI();
    index.initEvent();
});
