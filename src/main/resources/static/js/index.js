
const md = markdownit();
const pendingMessages = {};
let stompClient;


userInputField.addEventListener('keydown', function (e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        sendMessage();
        e.preventDefault();
    }
});
userInputField.addEventListener('input', function () {
    // 动态调整行数，但不能超过最大行数
    var currentRows = this.rows;
    var scrollHeight = this.scrollHeight;
    var lineHeight = this.clientHeight / currentRows; // 计算行高
    // 根据内容滚动高度计算需要的行数
    var newRows = Math.ceil(scrollHeight / lineHeight);
    // 限制行数在最大行数范围内
    newRows = Math.min(newRows, 8);
    // 更新rows属性
    if (newRows !== currentRows) {
        this.rows = newRows;
        chatScrollToBottom(chat);
    }
});

function connect() {
    var socket = new SockJS("/chat_ai/pc/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({'Authorization': token}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/' + groupId, function (message) {
            let msgObj = JSON.parse(message.body);
            console.info(msgObj.content)
            if (msgObj.role === "user") {
                userQuestion(msgObj);
            } else {
                aiAnswer(msgObj);
            }
        });
    });

    // 监听连接断开事件
    socket.onclose = function () {
        console.log('WebSocket连接已关闭。正在尝试在5秒内重新连接...');
        setTimeout(connect, 5000); // 5秒后尝试重新连接
    };
}

connect();

function userQuestion({ content, msgId }) {
    let div = createMessageElement(content, 'user-message', msgId);
    chat.appendChild(div);
    chatScrollToBottom(chat);
}

function aiAnswer({ content, answerMsgId, initialized, completed }) {
    let id = "ai" + answerMsgId;
    if (initialized) {
        let fromDiv = document.getElementById(answerMsgId);
        if (!fromDiv) return; // 避免无效操作
        let div = createMessageElement(content, 'bot-message', id);
        pendingMessages[id] = content;
        fromDiv.insertAdjacentElement("afterend", div);
    } else {
        pendingMessages[id] += content;
        let div = document.getElementById(id);
        if (!div) return
        div.innerHTML = md.render(pendingMessages[id]);
        if (completed) {
            aiAnswerComplete(div, id);
        }
    }
    chatScrollToBottom(chat);
}






// 在渲染消息后调用此函数
function renderMessages(messages) {
    let chatContent = document.createDocumentFragment(); // 用于批量插入DOM
    for (const item of messages) {
        item.initialized = true;
        let messageElement = createMessageElement(item.content,item.role === "user" ? 'user-message' : 'bot-message',item.id);
        chatContent.appendChild(messageElement);
    }
    // 批量插入所有消息元素
    chat.appendChild(chatContent);
    // 为每个消息的code块添加复制按钮
    const messageDivs = document.querySelectorAll('.message');
    messageDivs.forEach(div => createCopyBtnForCodeBlocks(div));
    chatScrollToBottom(chat);
}

function createMessageElement(content, clazz, id) {
    let div = document.createElement("div");
    div.id = id;
    div.innerHTML = md.render(content);
    div.classList.add('message', clazz);
    return div;
}


function createCopyBtnForCodeBlocks(div) {
    // 获取所有code标签
    const codeElements = div.getElementsByTagName('code');
    // 遍历每个code标签并添加复制按钮
    Array.from(codeElements).forEach(codeElement => {
        // 检查code标签是否是代码块的一部分
        if (isCodeBlock(codeElement)) {
            let copyBtn = document.createElement('button');
            copyBtn.textContent = '复制该代码';
            copyBtn.classList.add('copy-btn');
            copyBtn.onclick = function () {
                // 复制code标签内的内容
                copyTextToClipboard(codeElement.textContent);
            };
            // 将复制按钮添加到code标签旁边
            codeElement.insertAdjacentElement('afterend', copyBtn);
        }
    });
}


function answerComplete() {
    $('#send-button').show();
    $('#stop-button').hide();
}

function answering() {
    $('#send-button').hide();
    $('#stop-button').show();
}


//停止消息需要拿到 消息id，发送到后端停止回答
function stopAnswering() {
    answerComplete();
    Object.keys(pendingMessages).forEach((key) => {
        stompClient.send(sendStopPath + key.split("ai")[1], {});
    });
}
