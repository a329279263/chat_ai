package cn.autumn.chat.service;

import cn.autumn.chat.domain.Order;
import cn.autumn.chat.domain.RefundRecord;
import cn.autumn.chat.exception.BusinessException;
import cn.autumn.chat.util.OrderUtil;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.autumn.chat.domain.Order.*;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/11 17:31
 * @version: 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

    private RedissonClient redissonClient;
    private RefundRecordService refundService;
    private final WxPayService wxPayService;
    private final UserService userService;


    /**
     * 创建订单
     */
    @PostMapping("/create")
    public WxPayUnifiedOrderV3Result.JsapiResult create(@RequestBody WxPayUnifiedOrderV3Request request) {
        try {
            final String orderNo = OrderUtil.getNo();
            request.setOutTradeNo(orderNo);
            final WxPayUnifiedOrderV3Result.JsapiResult orderV3 = wxPayService.createOrderV3(TradeTypeEnum.JSAPI, request);
            final String packageValue = orderV3.getPackageValue();
            final String substring = packageValue.substring(packageValue.indexOf("=") + 1);
            final Order order = new Order(orderNo, request.getAmount().getTotal(), substring);
            order.save();
            return orderV3;
        } catch (WxPayException e) {
            throw new BusinessException(e.getErrCodeDes());
        } finally {
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }


    /**
     * 查询订单
     */
    public WxPayOrderQueryV3Result queryOrderByNo(String orderNo) {
        final Order order = getByOrderNo(orderNo);
        return queryAndUpdateOrder(order);
    }


    /**
     * 调用微信查询订单状态并更新
     */
    @Transactional
    public WxPayOrderQueryV3Result queryAndUpdateOrder(Order order) {
        final String orderNo = order.getOrderNo();
        RLock rLock = redissonClient.getLock(orderNo);
        try {
            //确保定时或者前端发起的查询不会造成状态乱入
            boolean res = rLock.tryLock(3, 10, TimeUnit.SECONDS);
            if (res) {
                final WxPayOrderQueryV3Result wxPayOrderQueryV3Result = wxPayService.queryOrderV3(null, orderNo);
                final String tradeState = wxPayOrderQueryV3Result.getTradeState();
                order.updateStatus(tradeState);
                //如果状态不一致，并且状态是成功则增加次数
                if (!order.getStatus().equals(tradeState) && tradeState.equals(STATUS_SUCCESS)) {
                    userService.addfixQACount(20);
                }
                return wxPayOrderQueryV3Result;
            }
            throw new BusinessException("请勿重复操作。");
        } catch (WxPayException e) {
            throw new BusinessException(e.getErrCodeDes());
        } catch (InterruptedException e) {
            throw new RuntimeException("业务繁忙，请稍后再试：", e);
        } finally {
            rLock.unlock();
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }


    /**
     * 申请退款
     */
    @Transactional
    public WxPayRefundResult refund(String orderNo) {
        RLock rLock = redissonClient.getLock(orderNo);
        try {
            //尝试5秒内获取锁，如果获取到了，最长60秒自动释放
            boolean res = rLock.tryLock(5, 30, TimeUnit.SECONDS);
            if (res) {
                final Order order = getByOrderNo(orderNo);
                final Integer amount = order.getAmount();
                final String refundNo = OrderUtil.getNo();
                WxPayRefundRequest request = new WxPayRefundRequest();
                request.setOutTradeNo(orderNo);
                request.setOutRefundNo(refundNo);
                request.setTotalFee(amount);
                request.setRefundFee(amount);
                log.info(JSONUtil.toJsonStr(request));
                final WxPayRefundResult refund = wxPayService.refund(request);
                RefundRecord refundRecord = new RefundRecord(order, refundNo, amount, refund);
                refundRecord.save();
                return refund;
            }
            throw new BusinessException("请勿重复操作。");
        } catch (WxPayException e) {
            throw new BusinessException(e.getErrCodeDes());
        } catch (InterruptedException e) {
            throw new RuntimeException("业务繁忙，请稍后再试：", e);
        } finally {
            //无论如何, 最后都要解锁
            rLock.unlock();
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }


    /**
     * 查询退款
     */
    public WxPayRefundQueryV3Result queryOrderRefund(String refundNo) {

        try {
            final RefundRecord refundRecord = refundService.getByRefundNo(refundNo);
            final WxPayRefundQueryV3Result result = wxPayService.refundQueryV3(refundNo);
            refundRecord.setStatus(result.getStatus());
            refundRecord.updateWithChangeTime();
            return result;
        } catch (WxPayException e) {
            throw new BusinessException(e.getErrCodeDes());
        } finally {
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }


    private Order getByOrderNo(String orderNo) {
        log.info("订单号查询 orderNo：" + orderNo);
        return DB.find(Order.class).where().ne("status", "REFUND")
                .eq("orderNo", orderNo).setMaxRows(1).findOneOrEmpty().orElseThrow(() -> new BusinessException("未查询到对应订单或已经退款。"));
    }


    public List<Order> listByNotPay() {
        return DB.find(Order.class).where()
                .in("status", STATUS_ACCEPT, STATUS_USER_PAYING, STATUS_NOT_PAY)
                .lt("retryCount", MAC_RETRY_COUNT).findList();
    }
}
