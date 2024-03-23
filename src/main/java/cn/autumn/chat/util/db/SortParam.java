package cn.autumn.chat.util.db;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 排序有关的参数
 * 为了Spring能够设置参数，需要给每个参数设置默认值
 */
class SortParam {
    private String name;
    private String direction;

    public SortParam(String name, String direction) {
        this.name = name;
        this.direction = direction == null || direction.isEmpty() ? "asc" : direction.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public <T> DbStatement<T> applyTo(DbStatement<T> stmt) {
        stmt.sort = this;
        return stmt;
    }

    public static SortParam parse(HttpServletRequest request) {
        String name = request.getParameter("orderByColumn");
        if (name == null || name.isEmpty()) {
            return null;
        }
        name = name.trim();
        String direction = request.getParameter("isAsc");

        return new SortParam(name, direction.trim());
    }
}
