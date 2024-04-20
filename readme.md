
---

# 项目名称

没有项目名称

## 项目背景
本项目是基于Spring Boot、WebSocket的多人聊天系统。

## 技术栈

- 后端框架：Spring Boot
- 开发工具：IntelliJ IDEA
- 版本控制：Git
- 微信小程序支付
- 智普ai


## 项目结构

```
chat_ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── cn/
│   │   │       └── autumn/
│   │   │           └── chat/
│   │   │               ├── controller/
│   │   │               ├── service/
│   │   │               └── config/
│   │   ├── resources/
│   │   │   ├── application.yaml
│   │   │   └── ebean.mf
│   │   └── webapp/
│   │       └── static/
├── pom.xml
├── README.md
└── LICENSE
```

## 快速启动


1. 启动 redis、mysql，并且创建一个空白数据库如：chat_ai。


2. 配置文件`src/main/resources/application.yaml`。

 
3. 需要ebean插件，Intellij idea安装插件后，菜单找到 build -> Ebean Enhancer，即可对实体类进行增强。


4. 运行 `src/test/DBTest.java`，会自动创建数据库和表（如存在会清空数据）。


5. 启动项目


6. 跨域问题通过 nginx 解决，配置如下
```nginx
		location /chatAi {
		   alias		D:/Project/99/chat_ai/src/main/resources/static/;
		   index       index.html index.htm;
		}
		
		location ^~ /chat_ai {
                  proxy_pass              http://127.0.0.1:8765/chat_ai;
                  proxy_set_header        Host 127.0.0.1;
                  proxy_set_header        X-Real-IP $remote_addr;
                  proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
        }
		
		location /chat_ai/pc/ws {
			proxy_pass http://127.0.0.1:8765;  # 后端Spring Boot应用的地址
			proxy_http_version 1.1;
			proxy_set_header Upgrade $http_upgrade;
			proxy_set_header Connection "Upgrade";
			proxy_set_header Host $host;
			proxy_set_header X-Real-IP $remote_addr;
			proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
			proxy_set_header X-Forwarded-Proto $scheme;
		}
		# 小程序用的 WebSocket
		location /chat_ai/miniapp/ws {
			proxy_pass http://127.0.0.1:8765;  # 后端Spring Boot应用的地址
			proxy_http_version 1.1;
			proxy_set_header Upgrade $http_upgrade;
			proxy_set_header Connection "Upgrade";
			proxy_set_header Host $host;
			proxy_set_header X-Real-IP $remote_addr;
			proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
			proxy_set_header X-Forwarded-Proto $scheme;
		}
```

7. 访问项目地址：http://localhost/chatAi/index.html

## 功能模块

【简要介绍项目的主要功能模块】

1. 小程序下单和支付小程序代码可参考Springboot+Websocket+Stomp实现PC兼容,及微信小程序连接 https://blog.csdn.net/xxs5258/article/details/101353133
2. 同一个会话可以多人聊天


## 注意事项
