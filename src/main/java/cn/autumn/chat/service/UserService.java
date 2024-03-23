package cn.autumn.chat.service;

import cn.autumn.chat.domain.User;
import cn.autumn.chat.domain.UserDomainService;
import cn.autumn.chat.exception.BusinessException;
import cn.hutool.core.util.RandomUtil;
import io.ebean.DB;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cn.autumn.chat.constant.Constant.INIT_QA_COUNT;

/**
 * @descriptions: 用户服务
 * @author: LZH
 * @date: 2024/3/20 10:38
 * @version: 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserService extends UserDomainService<User> {

    public User getByCurrentUser() {
        return this.query().setMaxRows(1).findOne();
    }


    public User getOrCreateByCurrentUser() {
        final User user = getByCurrentUser();
        if (user == null) {
            return new User();
        }
        return user;
    }

    public void saveUser(User user) {
        final String openid = user.getOpenid();
        final Optional<User> userOptional = DB.find(User.class).where().eq("openid", openid).setMaxRows(1).findOneOrEmpty();
        userOptional.ifPresentOrElse(found -> user.updateWithChangeTime(), () -> {
            user.setRemainingQACount(INIT_QA_COUNT);
            user.setUsedQACount(0);
            user.setUsername("用户-" + RandomUtil.randomString(5));
            DB.save(user);
        });
    }

    /**
     * 校验当前登录人是否足够问答次数
     */
    public int checkQaCount() {
        final User user = getByCurrentUser();
        if (user.getRemainingQACount() <= 0) {
            throw new BusinessException("剩余问答次数不足，请充值。");
        }
        return user.getRemainingQACount() - 1;
    }

    /**
     * 问答后扣减次数
     */
    public void qaCountSub(String openid) {
        final User user = DB.find(User.class).where().eq("openid", openid).setMaxRows(1).findOne();
        if (user == null) return;
        user.qa();
    }


    /**
     * 固定次数充值
     */
    public void addfixQACount(Integer count) {
        final User user = getByCurrentUser();
        user.setRemainingQACount(user.getRemainingQACount() + count);
        user.updateWithChangeTime();
    }


    /**
     * 假装充值
     */
    public void addCount() {
        final User user = getByCurrentUser();
        user.setRemainingQACount(user.getRemainingQACount() + 10);
        user.updateWithChangeTime();
    }
}
