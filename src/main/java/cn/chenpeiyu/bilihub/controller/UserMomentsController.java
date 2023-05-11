package cn.chenpeiyu.bilihub.controller;

import cn.chenpeiyu.bilihub.controller.support.UserSupport;
import cn.chenpeiyu.bilihub.domain.JsonResponse;
import cn.chenpeiyu.bilihub.domain.UserMoment;
import cn.chenpeiyu.bilihub.domain.annotation.ApiLimitedRole;
import cn.chenpeiyu.bilihub.domain.annotation.DataLimited;
import cn.chenpeiyu.bilihub.domain.constant.AuthRoleConstant;
import cn.chenpeiyu.bilihub.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMomentsController {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;


    /**
     * 发送动态
     * @param userMoment 动态
     * @return
     * @throws Exception
     */
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    /**
     * 获取动态
     * @return 动态的列表
     */
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments(){
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }

}
