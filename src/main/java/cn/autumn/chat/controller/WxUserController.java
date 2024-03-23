package cn.autumn.chat.controller;


import cn.autumn.chat.domain.User;
import cn.autumn.chat.domain.vo.R;
import cn.autumn.chat.domain.vo.resp.UserResp;
import cn.autumn.chat.security.ShiroUtils2;
import cn.autumn.chat.service.UserService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/wx/user")
@AllArgsConstructor
@Slf4j
public class WxUserController {

    private final WxMaService wxMaService;
    private final UserService userService;


    /**
     * 获取当前用户信息
     */
    @GetMapping("/getCurrentUserInfo")
    public R<UserResp> getCurrentUserInfo() {
        if (StringUtils.isEmpty(ShiroUtils2.getUser())) {
            return R.fail("请先登录。");
        }
        final UserResp userResp = new UserResp();
        BeanUtil.copyProperties(userService.getByCurrentUser(), userResp);
        return R.ok(userResp);
    }


    /**
     * 检查当前用户是否可以问答
     */
    @GetMapping("/checkQaCount")
    public R<Integer> checkQaCount() {
        return R.ok(userService.checkQaCount());
    }

    /**
     * 假装充值
     */
    @GetMapping("/addCount")
    public R<Integer> addCount() {
        userService.addCount();
        return R.ok("ok");
    }

    /**
     * 登录接口
     */
    @GetMapping("/login")
    public R<WxMaJscode2SessionResult> login(String code) {
        if (StringUtils.isBlank(code)) {
            return R.fail("code不能为空！");
        }
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            log.info(session.getSessionKey());
            log.info(session.getOpenid());
            //todo 完成内部登录计入缓存，生成token
            return R.ok(session);
        } catch (WxErrorException e) {
            log.error("登录失败：", e);
            return R.fail("登录失败：" + e.getMessage());
        } finally {
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }


    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @GetMapping("/info")
    public R<UserResp> info(String sessionKey,
                            String signature, String rawData, String encryptedData, String iv) {

        // 用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            WxMaConfigHolder.remove();//清理ThreadLocal
            return R.fail("身份信息校验失败。");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        final User user = userService.getOrCreateByCurrentUser();
        BeanUtil.copyProperties(userInfo, user);
        user.setOpenid(ShiroUtils2.getUser());
        userService.saveUser(user);
        final UserResp userResp = new UserResp();
        BeanUtil.copyProperties(user, userResp);
        WxMaConfigHolder.remove();//清理ThreadLocal
        return R.ok(userResp);
    }
}
