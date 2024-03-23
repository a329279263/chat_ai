package cn.autumn.chat.job;

import cn.autumn.chat.domain.Order;
import cn.autumn.chat.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/21 18:37
 * @version: 1.0
 */
@Component
@AllArgsConstructor
public class OrderJob {


    private final OrderService orderService;


    /**
     * 对于未支付成功的订单进行定时查询并更新订单状态
     */
    @Scheduled(fixedRate = 1000 * 10) // 十秒一次
    public void clearCounters() {
        final List<Order> orderList = orderService.listByNotPay();
        for (Order order : orderList) {
            orderService.queryAndUpdateOrder(order);
        }
    }


}
