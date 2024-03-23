package cn.autumn.chat.util.db;

import cn.autumn.chat.domain.BaseEntity;
import cn.autumn.chat.security.ShiroUtils2;
import cn.hutool.core.util.StrUtil;
import io.ebean.DB;
import io.ebean.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDB {

    public static <T> Query<T> applyUser(Class<T> clz) {
        Query<T> find = DB.find(clz);
        return applyUser(clz, find);
    }

    public static <T> Query<T> applyUser(Class<T> clz, Query<T> find) {
        if (!BaseEntity.class.isAssignableFrom(clz)) {
            return find;
        }
        String user = ShiroUtils2.getUser();
        if (StrUtil.isNotEmpty(user)) {
            find.where().eq("openid", user);
        }
        return find;
    }

    public static <T> Query<T> find(Class<T> clz) {
        return applyUser(clz);
    }

    public static <T> T find(Class<T> clz, Object id) {
        final Query<T> find = applyUser(clz);
        find.where().eq("id", id);
        return find.setMaxRows(1).findOne();
    }


}
