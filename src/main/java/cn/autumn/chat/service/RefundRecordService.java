package cn.autumn.chat.service;

import cn.autumn.chat.domain.RefundRecord;
import cn.autumn.chat.exception.BusinessException;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/12 10:59
 * @version: 1.0
 */
@Service
@Slf4j
public class RefundRecordService {


    public RefundRecord getByRefundNo(String refundNo) {
        log.info("退款订单查询 refundNo：" + refundNo);
        return DB.find(RefundRecord.class).where()
                .eq("refundNo", refundNo).setMaxRows(1).findOneOrEmpty().orElseThrow(() -> new BusinessException("未查询到对应退款订单。"));
    }

}
