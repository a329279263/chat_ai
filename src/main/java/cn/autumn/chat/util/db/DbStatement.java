package cn.autumn.chat.util.db;

import cn.autumn.chat.domain.BaseEntity;
import cn.autumn.chat.security.ShiroUtils2;
import io.ebean.DB;
import io.ebean.Expression;
import io.ebean.Query;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
class DbStatement<T> {
    PageParam page = new PageParam();

    List<Expression> conditions = new ArrayList<>();

    SortParam sort = null;

    List<String> fields = new ArrayList<>();

    int skip = 0;

    int limit = 10;

    private Query<T> query;

    public DbStatement(Class<T> cls) {
        query = DB.find(cls);

        if (!BaseEntity.class.isAssignableFrom(cls)) {
            return;
        }

        String user = ShiroUtils2.getUser();
        if (user != null) {
            query.where().eq("openid", user);
        } else {
            query.where().isNull("openid");
        }
    }

    /**
     * 应用分页参数
     */
    public DbStatement<T> apply(PageParam pageParam) {
        limit = pageParam.getPageSize();
        skip = pageParam.getPageSize() * pageParam.getPageNum();
        page = pageParam;
        return this;
    }

    String fetchPath = null;

    public void fetch(String fetchPath) {
        this.fetchPath = fetchPath;
    }

    /**
     * 执行查询
     */
    public PagedList<T> query(boolean withCount) {
        conditions.forEach(c -> query.where().add(c));
        int total = 0;
        if (withCount) {
            total = query.findCount();
        }
        if (sort != null) {
            query.orderBy(sort.getName() + " " + sort.getDirection());
        }
        if (skip != 0) {
            query.where().setFirstRow(skip);
        }
        if (limit != 0) {
            query.where().setMaxRows(limit);
        }
        if (fetchPath != null) {
            query.select(fetchPath);
        }
        List<T> list = query.findList();
        return new PagedList<>(list, page.getPageSize(), page.getPageNum(), total);
    }

}
