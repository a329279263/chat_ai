package cn.autumn.chat.domain;


import cn.autumn.chat.constant.Constant;
import io.ebean.annotation.DbComment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 订单表
 * 实际支付是在前端页面发起，没有支付记录表
 */
@EqualsAndHashCode(callSuper = true)
@DbComment(value = "订单表")
@Entity
@Table(name = Constant.DB_PREFIX + "order")
@Data
@NoArgsConstructor
public class Order extends BaseEntity {

    /**
     * 支付成功
     */
    public final static String STATUS_SUCCESS = "SUCCESS";
    /**
     * 支付失败
     */
    public final static String STATUS_FAIL = "FAIL";

    /**
     * 转入退款
     */
    public final static String STATUS_REFUND = "REFUND";

    /**
     * 未支付
     */
    public final static String STATUS_NOT_PAY = "NOTPAY";

    /**
     * 已关闭
     */
    public final static String STATUS_CLOSED = "CLOSED";

    /**
     * 已撤销
     */
    public final static String STATUS_REVOKED = "REVOKED";

    /**
     * 用户支付中
     */
    public final static String STATUS_USER_PAYING = "USERPAYING";

    /**
     * 支付失败
     */
    public final static String STATUS_PAY_ERROR = "PAYERROR";

    /**
     * 已接收，等待扣款
     */
    public final static String STATUS_ACCEPT = "ACCEPT";


    /**
     * 最大重试次数
     */
    public final static Integer MAC_RETRY_COUNT = 20;

    @DbComment("商户订单号")
    private String orderNo;

    @DbComment("订单金额-分-整数")
    @Column(length = 30)
    private Integer amount;

    @DbComment("充值次数")
    private Integer rechargeCount;

    @DbComment("预支付交易会话标识")
    @Column(length = 64)
    private String prepayId;

    @DbComment("重试计数-用于订单查询后更新状态-定时查询")
    private Integer retryCount;

    /**
     * <pre>
     *  交易状态，枚举值：
     *  SUCCESS：支付成功
     *  REFUND：转入退款
     *  NOTPAY：未支付
     *  CLOSED：已关闭
     *  REVOKED：已撤销（付款码支付）
     *  USERPAYING：用户支付中（付款码支付）
     *  PAYERROR：支付失败(其他原因，如银行返回失败)
     *  ACCEPT：已接收，等待扣款
     *  示例值：SUCCESS
     * </pre>
     */
    @DbComment("订单状态")
    @Column(length = 20)
    private String status;

    public Order(String orderNo, Integer amount, String prepayId) {
        this.orderNo = orderNo;
        this.amount = amount;
        this.prepayId = prepayId;
        this.retryCount = 0;
        this.status = STATUS_NOT_PAY;
    }

    public void updateStatus(String tradeState) {
        this.status = tradeState;
        this.retryCount++;
        this.updateWithChangeTime();
    }
}
