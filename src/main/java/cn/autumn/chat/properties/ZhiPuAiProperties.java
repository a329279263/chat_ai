package cn.autumn.chat.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/13 16:58
 * @version: 1.0
 */
@ConfigurationProperties(prefix = "zhipuai")
@Configuration
@Data
public class ZhiPuAiProperties {

    private String apiKey;

    private String apiSecret;

}
