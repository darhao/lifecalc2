package cc.darhao.lifecalc.interceptor;

import cc.darhao.lifecalc.annotation.Log;
import cc.darhao.lifecalc.dao.ActionLogMapper;
import cc.darhao.lifecalc.dao.UserMapper;
import cc.darhao.lifecalc.entity.ActionLog;
import cc.darhao.lifecalc.entity.User;
import cc.darhao.lifecalc.util.ResultFactory;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 操作日志拦截器，本拦截器会对带有@Log注解的方法进行日志记录，记录的详细信息为Log中的值，其中变量<br>
 * 使用<b>{变量}</b>表示，变量名和被注解的方法参数名必须一致，值为参数值。举个例子：<br><br>
 * @Log("获取了用户名为{userId}的用户")<br>
 * public void getUser(String userId){ ... }<br>
 * <br>
 * 如果传入参数值为"bobo"，则数据库的详细信息为"获取了用户名为bobo的用户"<br>
 * <br>
 * <b>2018年6月14日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@ControllerAdvice
public class ActionLogInterceptor implements HandlerInterceptor, ResponseBodyAdvice {

    private static final String REGEX = "\\{[a-zA-Z0-9]+\\}";

    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ActionLogMapper actionLogMapper;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Log log = ((HandlerMethod)handler).getMethod().getAnnotation(Log.class);
        ActionLog actionLog = new ActionLog();
        //如果存在@Log注解，则进行日志记录
        if(log != null) {
            //匹配参数并替换值
            Matcher matcher = Pattern.compile(REGEX).matcher(log.value());
            StringBuffer sb = new StringBuffer();
            while(matcher.find()) {
                String a = matcher.group();
                a = a.substring(1, a.length() - 1);
                String value = request.getParameter(a);
                if(value == null) {
                    value = "null";
                }
                //美元符转义
                if(value.contains("$")) {
                    value = value.replaceAll("\\$", "\\\\\\$");
                }
                matcher.appendReplacement(sb, value);
            }
            matcher.appendTail(sb);
            //日志生成
            String url = request.getRequestURL().toString();
            String method = url.substring(url.indexOf("/",url.indexOf("/", url.indexOf("/")+1)+1));
            String parameters = JSON.toJSONString(request.getParameterMap());
            actionLog.setParameters(parameters);
            actionLog.setTime(new Date());
            actionLog.setMethod(method);
            actionLog.setAction(sb.toString());
            String token = request.getParameter("token");
            String userId = redisTemplate.opsForValue().get(AccessInterceptor.LIFECALC_USER_TOKEN + token);
            if (userId != null) {
                User user = userMapper.selectById(userId);
                if (user != null) {
                    actionLog.setUser(user.getName());
                }
            }
            request.setAttribute("startTime", System.currentTimeMillis());
            request.setAttribute("actionLog", actionLog);
        }
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute("startTime");
        ActionLog actionLog = (ActionLog) request.getAttribute("actionLog");
        ResultFactory.Result result = (ResultFactory.Result) request.getAttribute("response");
        if(startTime != null && actionLog != null && result != null){
            long consumeTime = System.currentTimeMillis() - startTime.longValue();
            actionLog.setConsumeTime((int) consumeTime);
            actionLog.setResultCode(result.getCode());
            actionLog.setResponse(JSON.toJSONString(result.getData()));
            //插入
            actionLogMapper.insert(actionLog);
        }
    }


    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body instanceof ResultFactory.Result){
            ServletServerHttpRequest req = (ServletServerHttpRequest)request;
            HttpServletRequest servletRequest = req.getServletRequest();
            servletRequest.setAttribute("response", body);
        }
        return body;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

}
