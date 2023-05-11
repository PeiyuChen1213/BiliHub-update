package cn.chenpeiyu.bilihub.controller.support;

import cn.chenpeiyu.bilihub.domain.exception.ConditionException;
import cn.chenpeiyu.bilihub.service.UserService;
import cn.chenpeiyu.bilihub.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserSupport {

    @Autowired
    private UserService userService;

    /**
     * 获得当前登录的UserId
     *
     * @return
     */
    public Long getCurrentUserId() {
        //获取请求相关信息的类
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
//       从请求头当中获取当前的token
        String token = request.getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
//       userId一般都是大于0的
        if (userId < 0) {
            throw new ConditionException("非法用户");
        }
//        验证是否是假的refreshToken
        this.verifyRefreshToken(userId);
        return userId;
    }

    //验证刷新令牌
    private void verifyRefreshToken(Long userId) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String refreshToken = requestAttributes.getRequest().getHeader("refreshToken");
        String dbRefreshToken = userService.getRefreshTokenByUserId(userId);
        if (!dbRefreshToken.equals(refreshToken)) {
            throw new ConditionException("非法用户！");
        }
    }


}
