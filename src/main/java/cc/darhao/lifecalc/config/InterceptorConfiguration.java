package cc.darhao.lifecalc.config;

import cc.darhao.lifecalc.interceptor.AccessInterceptor;
import cc.darhao.lifecalc.interceptor.ActionLogInterceptor;
import cc.darhao.lifecalc.interceptor.CrosInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
* @Auther 鲁智深
* @Date 2021/1/18 22:13
*/
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private CrosInterceptor crosInterceptor;
    @Autowired
    private AccessInterceptor accessInterceptor;
    @Autowired
    private ActionLogInterceptor actionLogInterceptor;

    private static final String[] paths = new String[]{
            "/user/**",
            "/plan/**"
    };


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(crosInterceptor).addPathPatterns(paths);
        registry.addInterceptor(actionLogInterceptor).addPathPatterns(paths);
        registry.addInterceptor(accessInterceptor).addPathPatterns(paths).excludePathPatterns("/user/**");
    }

}
