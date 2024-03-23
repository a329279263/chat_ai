package cn.autumn.chat.domain.vo.resp;

import cn.hutool.http.HttpStatus;
import lombok.Data;

import java.util.List;

/**
 * 分页返回
 */

@Data
public class PageResp<T> {


    /**
     * 总记录数
     */
    private int total;


    /**
     * 记录
     */
    private List<T> rows;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;


    public PageResp() {
        this.total = 0;
        this.code = HttpStatus.HTTP_OK;
        this.msg = "ok";
    }

    public PageResp(int total, List<T> rows) {
        this.total = total;
        this.rows = rows;
        this.code = HttpStatus.HTTP_OK;
        this.msg = "ok";
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
