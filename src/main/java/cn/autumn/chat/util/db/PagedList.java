package cn.autumn.chat.util.db;


import cn.autumn.chat.domain.BaseEntity;
import cn.autumn.chat.util.JsonHelper;
import io.ebean.DB;
import io.ebean.text.PathProperties;
import io.ebean.text.json.JsonWriteOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PagedList<T> implements Iterable<T> {

    private final List<T> rows;
    private final int size;
    private final int number;
    private final int total;
    private final int totalPages;
    private int code = 0;

    public PagedList(List<T> rows, int size, int number, int total) {
        this.rows = rows;
        this.size = size;
        this.number = number;
        this.total = total;
        this.totalPages = totalPages();
    }

    public static <T> PagedList<T> of(List<T> rows) {
        return new PagedList<>(rows, rows.size(), rows.isEmpty() ? 0 : 1, rows.isEmpty() ? 0 : 1);
    }

    public List<T> getRows() {
        return rows;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }


    private int totalPages() {
        return size == 0 ? 1 : (int) Math.ceil(1.0 * total / size);
    }

    public boolean hasPrevious() {
        return number > 0;
    }

    public boolean isFirst() {
        return !hasPrevious();
    }

    public boolean hasNext() {
        return number + 1 < totalPages;
    }

    public boolean isLast() {
        return !hasNext();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public Iterator<T> iterator() {
        return rows.iterator();
    }

    /**
     * 页码显示的页码列表（不含头尾）
     */
    public List<Integer> numberList(int count) {
        int begin = number + 1 - 2;
        if (begin < 2) begin = 2;
        int end = begin + (count - 1);

        if (end >= totalPages) {
            end = totalPages - 1;
        }

        if (end - begin < (count - 1)) {
            begin = end - (count - 1);
        }
        if (begin < 2) begin = 2;
        if (end >= begin) {
            List<Integer> list = new ArrayList<>(end - begin + 1);
            while (begin <= end) {
                list.add(begin++);
            }
            return list;
        }
        return Collections.emptyList();
    }

    public <R> PagedList<R> mapToPagedList(Function<? super T, ? extends R> mapper) {
        return new PagedList<R>(rows.stream().map(mapper).collect(Collectors.toList()), size, number, total);
    }

    public String toJson(String pathProperties) {
        String cnt = "";
        String template = "{\"code\": %d,\"rows\":%s,\"size\":%d,\"number\":%d,\"total\":%d,\"totalPages\":%d,\"first\":%b,\"last\":%b}";
        if (rows == null || rows.isEmpty()) {
            cnt = "[]";
        } else if (rows.get(0) instanceof BaseEntity) {
            JsonWriteOptions options = new JsonWriteOptions();
/*
            //设置日期输出的格式，未生效！！！
            ObjectMapper om  = (ObjectMapper) options.getObjectMapper();
            if(om==null){
                om = new ObjectMapper();
                options.setObjectMapper(om);
            }
            SerializationConfig config = om.getSerializationConfig().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            om.setConfig(config);
*/
            if (pathProperties != null) {
                options.setPathProperties(PathProperties.parse(pathProperties));
            }
            cnt = DB.json().toJson(rows, options);
        } else {
            cnt = JsonHelper.toJson(rows);
        }

        return String.format(template, code, cnt, size, number, total, totalPages, isFirst(), isLast());
    }

    public String toJson() {
        return toJson(null);
    }

}
