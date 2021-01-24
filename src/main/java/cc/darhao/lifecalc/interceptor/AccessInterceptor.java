package cc.darhao.lifecalc.interceptor;

import cc.darhao.lifecalc.util.ResponseUtil;
import cc.darhao.lifecalc.util.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @Auther 鲁智深
* @Date 2021/1/18 21:42
*/
@Component
public class AccessInterceptor implements HandlerInterceptor {

    public static final String LIFECALC_USER_TOKEN = "lifecalc_user_token_";

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getParameter("token");
        if (token == null){
            ResponseUtil.sendResult(response,ResultFactory.failed(ResultFactory.PARAMETER_EXCEPTION_CODE,"token不能为空"));
            return false;
        }
        //根据token获取用户id
        String userId = redisTemplate.opsForValue().get(LIFECALC_USER_TOKEN + token);
        if (userId == null){
            ResponseUtil.sendResult(response,ResultFactory.failed(ResultFactory.ACCESS_EXCEPTION_CODE,"请先登录"));
            return false;
        }
        return true;
    }
}
