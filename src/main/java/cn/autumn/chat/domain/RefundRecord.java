package cn.autumn.chat.domain;


import cn.autumn.chat.constant.Constant;
import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import io.ebean.annotation.DbComment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * 退款记录表
 */
@EqualsAndHashCode(callSuper = true)
@DbComment(value = "退款记录表")
@Entity
@Table(name = Constant.DB_PREFIX + "refund_record")
@Data
@NoArgsConstructor
public class RefundRecord extends BaseEntity {

    @DbComment("关联的订单")
    @ManyToOne
    private Order order;

    @DbComment("退款单号")
    private String refundNo;

    @DbComment("退款金额")
    @Column(scale = 2)
    private Integer refundAmount;

    @DbComment("退款状态")
    @Column(length = 20)
    private String status;

    @DbComment("最后一次退款返回结果")
    @Lob
    private String lastResult;


    public RefundRecord(Order order, String refundNo, Integer refundAmount, WxPayRefundResult refund) {
        this.order = order;
        this.refundNo = refundNo;
        this.refundAmount = refundAmount;
        this.lastResult = JSONUtil.toJsonStr(refund);
    }
}
