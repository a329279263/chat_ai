package cn.autumn.chat.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx.miniapp.config")
public class WxMaProperties {

    /**
     * 设置微信小程序的appid
     */
    private String appid;

    /**
     * 设置微信小程序的Secret
     */
    private String secret;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户密钥/ APIv2密钥
     */
    private String mchKey;

    /**
     * 商户API私钥路径
     */
    private String privateKeyPath;

    /**
     * 商户API证书路径
     */
    private String privateCertPath;

    /**
     * p12 证书路径-退款需要
     */
    private String keyPath;
    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;
    /**
     * 商户APIV3密钥
     */
    private String apiV3Key;


    /**
     * 设置微信小程序消息服务器配置的token
     */
    private String token;

    /**
     * 设置微信小程序消息服务器配置的EncodingAESKey
     */
    private String aesKey;

    /**
     * 消息格式，XML或者JSON
     */
    private String msgDataFormat;

}
