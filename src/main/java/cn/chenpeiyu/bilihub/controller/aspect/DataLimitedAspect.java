package cn.chenpeiyu.bilihub.controller.aspect;

import cn.chenpeiyu.bilihub.controller.support.UserSupport;
import cn.chenpeiyu.bilihub.domain.UserMoment;
import cn.chenpeiyu.bilihub.domain.auth.UserRole;
import cn.chenpeiyu.bilihub.domain.constant.AuthRoleConstant;
import cn.chenpeiyu.bilihub.domain.exception.ConditionException;
import cn.chenpeiyu.bilihub.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)
@Component
@Aspect
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(cn.chenpeiyu.bilihub.domain.annotation.DataLimited)")
    public void check() {
    }

    /**
     * 限制等级低的用户发布动态类型
     *
     * @param joinPoint
     */
    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();
//        获取当前的角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
//        获取当前的角色code
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
//        获取切入点方法的参数 -- public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) 也就是UserMoment参数
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoment) {
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
//              等级低的用户不能发type==0的动态
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_LV1) && !"0".equals(type)) {
                    throw new ConditionException("参数异常");
                }
            }
        }
    }
}
