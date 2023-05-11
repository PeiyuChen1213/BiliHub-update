package cn.chenpeiyu.bilihub.controller;

import cn.chenpeiyu.bilihub.controller.support.UserSupport;
import cn.chenpeiyu.bilihub.domain.JsonResponse;
import cn.chenpeiyu.bilihub.domain.auth.UserAuthorities;
import cn.chenpeiyu.bilihub.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthController {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthService userAuthService;

    /**
     * 获取当前的权限
     * @return
     */
    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities(){
//        获取当前的用户id
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }
}
