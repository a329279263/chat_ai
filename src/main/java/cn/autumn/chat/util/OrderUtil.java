package cn.autumn.chat.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.Date;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/11 17:39
 * @version: 1.0
 */
public class OrderUtil {

    /**
     * 获取订单号
     */
    public static String getNo() {
        String newDate = DateUtil.format(new Date(), "yyMMddHHmmssSSS");
        return newDate + RandomUtil.randomNumbers(8);
    }


    /**
     * 获取退款订单号
     */
    public static String getRefundNo() {
        return "REFUND_" + getNo();
    }
}
