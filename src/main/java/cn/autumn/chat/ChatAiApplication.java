package cn.autumn.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.NetworkInterface;

@SpringBootApplication
@EnableScheduling
public class ChatAiApplication {
    public static void main(String[] args) throws Exception {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ChatAiApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        // String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        System.out.println("\n----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path);
        NetworkInterface.networkInterfaces().forEach(networkInterface -> {
            networkInterface.inetAddresses().filter(inetAddress -> !inetAddress.isLoopbackAddress()).forEach(inetAddress -> {
                System.out.println("\tExternal: \thttp://" + inetAddress.getHostAddress() + ":" + port + path);
            });
        });
        System.out.println("----------------------------------------------------------");
        System.out.println("(♥◠‿◠)ﾉﾞ  系统启动成功   ლ(´ڡ`ლ)ﾞ  \n");
    }

}
