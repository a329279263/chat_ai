

$.fn.notify = function(message, timeout) {
    timeout = timeout || 3000; // 默认显示3秒

    // 创建通知元素
    var $notify = $('<div>').addClass('notify').text(message);

    // 将通知元素添加到页面中
    $('body').append($notify);

    // 设置通知的显示位置和动画
    $notify.css({
        'position': 'fixed',
        'top': '50px',
        'left': '47%',
        'z-index': '1000',
        'background-color': '#333',
        'color': '#fff',
        'padding': '10px',
        'border-radius': '5px',
        'opacity': '0'
    }).animate({ opacity: 1 }, 500, function() {
        // 设置定时器，在指定时间后移除通知元素
        setTimeout(function() {
            $notify.animate({ opacity: 0 }, 500, function() {
                $notify.remove();
            });
        }, timeout);
    });
};
$.notify=function(message, timeout) {
    $('body').notify(message, timeout);
};
// 平滑滚动到底部
function chatScrollToBottom(chat) {
    // 如果需要，在这里添加平滑滚动逻辑
    chat.scrollTo({top: chat.scrollHeight, behavior: "smooth"});
}
function copyTextToClipboard(text) {
    // 创建一个临时的input元素
    const tempInput = document.createElement('textarea');
    tempInput.value = text;
    tempInput.style.display = 'nonoe';
    document.body.appendChild(tempInput);
    // 选择input中的文本
    tempInput.select();
    document.execCommand('copy')
    $('body').notify('复制成功');
    // 移除临时的input元素
    document.body.removeChild(tempInput);
}
// 检查code标签是否是代码块的一部分
function isCodeBlock(codeElement) {
    // 检查code标签是否有换行符，如果是，则认为是代码块
    return codeElement.textContent.includes('\n');
}


function generateUniqueID() {
    const timestamp = Date.now();
    const randomString = Math.random().toString(36).substring(2, 15);
    return `${timestamp}-${randomString}`;
}
