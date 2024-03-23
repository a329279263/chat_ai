package cn.autumn.chat.domain;

import cn.autumn.chat.security.ShiroUtils2;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.ebean.DB;
import io.ebean.annotation.*;
import io.ebean.text.PathProperties;
import io.ebean.text.json.JsonContext;
import io.ebean.text.json.JsonWriteOptions;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entity基类
 */
@MappedSuperclass
public class BaseEntity implements Serializable, ToJson {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @GeneratedValue()
    @DbComment("ID")
    protected Long id;

    @DbComment("用户ID")
    private String openid;

    /**
     * 创建时间
     */
    @DbComment("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @WhenCreated
    protected Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @WhenModified
    @DbComment("更新时间")
    protected Date updateTime;

    /**
     * 逻辑删除
     */
    @SoftDelete
    @DbComment("逻辑删除")
    protected boolean deleted;

    /**
     * 备注
     */
    @DbComment("备注")
    @Column(length = 500)
    protected String remark;

    @Override
    public String toJson(String pathProperties) {
        JsonContext jsonContext = DB.json();
        JsonWriteOptions options = new JsonWriteOptions();
        if (pathProperties != null) {
            options.setPathProperties(PathProperties.parse(pathProperties));
        }
        String jsonStr = "";
        try {
            jsonStr = jsonContext.toJson(this, options);
        } catch (Exception exp) {
            exp.printStackTrace();
            jsonStr = "输出Json格式有误,请检查属性列表, error message: " + exp.getMessage();
        }
        return jsonStr;
    }

    @PrePersist
    public void prePersist() {
        String user = ShiroUtils2.getUser();
        if (StrUtil.isNotEmpty(user) && StrUtil.isEmpty(this.getOpenid())) {
            this.setOpenid(user);
        }
    }

    /**
     * 将BaseEntity集合转化为Json
     */
    public static String toJson(Collection<? extends BaseEntity> entities, String pathProperties) {
        List<String> stringList = entities.stream().map(baseEntity -> baseEntity.toJson(pathProperties)).collect(Collectors.toList());
        return "[" + String.join(",", stringList) + "]";
    }

    public int insert() {
        DB.insert(this);
        return 1;
    }

    public int update() {
        DB.update(this);
        return 1;
    }

    public int updateWithChangeTime() {
        this.updateTime = new Date();
        DB.update(this);
        return 1;
    }

    //insert or update
    public void save() {
        if (this.id != null && this.id != 0L) {
            DB.update(this);
        } else {
            DB.insert(this);
        }
    }

    /**
     * 逻辑删除
     */
    public int delete() {
        return DB.delete(this.getClass(), getId());
    }

    /**
     * 物理删除
     */
    public int deletePermanent() {
        return DB.deletePermanent(this.getClass(), getId());
    }

    public static <T extends BaseEntity> T findByUUID(Class<T> cls, String uuid) {
        return DB.find(cls).where().eq("uuid", uuid).findOne();
    }

    @Transactional
    public static <T extends BaseEntity> void insertAll(List<T> entities) {
        DB.insertAll(entities);
    }

    @Transactional
    public static <T extends BaseEntity> int deleteAll(Class<T> cls, List<Long> ids) {
        return ids.stream().mapToInt(id -> DB.delete(cls, id)).reduce(0, Integer::sum);
    }

    public static <T extends BaseEntity> int deleteAll(Class<T> cls, String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return deleteAll(cls, idList);
    }


    /**
     * 取得某个实体类的数据库名称。根据Table 注解查询.
     */
    public static String getTableName(BaseEntity entity) {
        Table annotation = entity.getClass().getAnnotation(Table.class);
        return annotation.name();
    }

    /**
     * 取得某个实体类的数据库名称。根据Table 注解查询.
     *
     * @param cls BaseEntity class
     */
    public static String getTableName(Class<?> cls) {
        Table annotation = cls.getAnnotation(Table.class);
        return annotation.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        if (id == null) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
