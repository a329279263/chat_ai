package cn.autumn.chat.domain;

import cn.autumn.chat.domain.vo.resp.PageResp;
import cn.autumn.chat.util.db.PageParam;
import cn.autumn.chat.util.db.SearchParam;
import cn.autumn.chat.util.db.UserDB;
import io.ebean.DB;
import io.ebean.PagedList;
import io.ebean.Query;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

public class UserDomainService<T extends BaseEntity> {

    protected Class<T> clazz;

    public UserDomainService() {
        getRealType();
    }

    // 使用反射技术得到T的真实类型
    protected Class<T> getRealType() {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        this.clazz = (Class<T>) pt.getActualTypeArguments()[0];
        return clazz;
    }

    protected Query<T> query() {
        return UserDB.find(clazz);
    }

    public T findOne(Long id) {
        return query().where().idEq(id).setMaxRows(1).findOne();
    }

    public List<T> findAll() {
        return query().findList();
    }

    public int findCount() {
        return query().findCount();
    }

    public int countAll() {
        return query().findCount();
    }

    public List<T> findBatch(Collection<Long> ids) {
        return query().where().idIn(ids).findList();
    }

    public void save(T t) {
        DB.save(t);
    }

    public void saveAll(Collection<T> collection) {
        DB.saveAll(collection);
    }

    public void update(T t) {
        DB.update(t);
    }

    public void updateAll(Collection<T> collection) {
        DB.updateAll(collection);
    }

    public void delete(T t) {
        DB.delete(t);
    }

    public void deleteAll(Collection<T> collection) {
        DB.deleteAll(collection);
    }

    public void deleteOne(Long id) {
        query().where().idEq(id).delete();
    }

    public void deleteByIds(Collection<?> ids) {
        query().where().idIn(ids).delete();
    }

    public List<T> search(SearchParam<T> searchParam) {
        Query<T> query = query();
        searchParam.applyTo(query);
        return query.findList();
    }


    public List<T> searchAll(PageParam<T> searchParam) {
        Query<T> query = query();
        searchParam.applyTo(query);
        return query.findList();
    }

    public PagedList<T> searchPage(PageParam<T> pageParam) {
        Query<T> query = query();
        query.order("id desc");
        pageParam.applyTo(query);

        PagedList<T> pagedList = query.setMaxRows(pageParam.getPageSize()).setFirstRow((pageParam.getPageNum() - 1) * pageParam.getPageSize()).findPagedList();
        return pagedList;
    }

    public PageResp<T> getPageResp(PageParam<T> pageParam) {
        final PagedList<T> pagedList = searchPage(pageParam);
        return new PageResp<>(pagedList.getTotalCount(), pagedList.getList());
    }

}

/*

public class NotifyMessageMyPageReqVO implement SearchParam<DNotifyMessage> {
public class NotifyMessageMyPageReqVO extends PageParam<DNotifyMessage> {

    @Schema(description = "是否已读", example = "true")
    private Boolean readStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Override
    public void applyTo(Query<DNotifyMessage> query) {
        query.where().eqIfPresent("readStatus", getReadStatus())
                .eq("user.id", SecurityFrameworkUtils.getLoginUserId())
                .betweenIfPresent("createTime", getCreateTime())
                .orderBy("id desc");
    }

}
*/
