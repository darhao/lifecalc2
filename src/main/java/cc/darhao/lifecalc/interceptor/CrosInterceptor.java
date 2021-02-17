package cc.darhao.lifecalc.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @Auther 鲁智深
* @Date 2021/1/18 21:56
*/
@Component
public class CrosInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		return true;
    }

}
