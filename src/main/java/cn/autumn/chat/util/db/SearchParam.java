package cn.autumn.chat.util.db;

import io.ebean.Query;

public interface SearchParam<E> {
    void applyTo(Query<E> query);
}
