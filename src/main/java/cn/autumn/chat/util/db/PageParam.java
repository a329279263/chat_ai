package cn.autumn.chat.util.db;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import io.ebean.Query;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public class PageParam<E> implements SearchParam<E> {
    // private static final Logger log = LoggerFactory.getLogger(PageParam.class);

    //客户要求：50、100、500
    private int pageSize = 50;
    private int pageNum = 0;

    public PageParam() {
    }

    public PageParam(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public <T> DbStatement<T> applyTo(DbStatement<T> stmt) {
        return stmt.apply(this);
    }

    public static PageParam parse(HttpServletRequest request) {
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Optional<Cookie> optional = Arrays.stream(cookies).filter(c -> "page_size".equalsIgnoreCase(c.getName())).findFirst();
                if (optional.isPresent()) {
                    pageSize = optional.get().getValue();
                }
            }
        }
        if (pageSize == null || "NaN".equalsIgnoreCase(pageSize)) {
            pageSize = "10";
        }
        String pageIndex = request.getParameter("pageNum");
        if (pageIndex == null || "NaN".equalsIgnoreCase(pageIndex)) {
            pageIndex = "1";
        }
        return new PageParam(Integer.parseInt(pageSize), Integer.parseInt(pageIndex) - 1);
    }

    public void applyTo(Query<E> query) {
    }
}

