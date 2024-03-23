package cn.autumn.chat.controller;


import cn.autumn.chat.domain.vo.R;
import cn.autumn.chat.service.OrderService;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    /**
     * 预支付
     * 获取 prepayId后生成签名给到前端调用支付接口，微信要求
     */
    @PostMapping("/create")
    public R<WxPayUnifiedOrderV3Result.JsapiResult> create(@RequestBody WxPayUnifiedOrderV3Request request) {
        return R.ok(orderService.create(request));
    }

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/queryOrderByNo")
    public R<WxPayOrderQueryV3Result> queryOrderByNo(@RequestParam("orderNo") String orderNo) {
        return R.ok(orderService.queryOrderByNo(orderNo));
    }

    /**
     * 退款
     */
    @GetMapping("/refund")
    public R<WxPayRefundResult> refund(@RequestParam String orderNo) {
        return R.ok(orderService.refund(orderNo));
    }

    /**
     * 根据退款订单号查询退款进度
     */
    @GetMapping("/queryRefundByNo")
    public R<WxPayRefundQueryV3Result> queryRefundByNo(@RequestParam("refundNo") String refundNo) {
        return R.ok(orderService.queryOrderRefund(refundNo));
    }


}
