package cc.darhao.lifecalc.controller;


import cc.darhao.lifecalc.util.MobileUaChecker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  首页前端控制器
 * </p>
 *
 * @author 鲁智深
 * @since 2021-01-19
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping("/")
    public String forwardPcOrMoblieHome(HttpServletRequest request) {
        if(MobileUaChecker.isMobile(request.getHeader("user-agent"))){
            return "forward:/html/mobile.html";
        }else{
            return "forward:/html/pc.html";
        }
    }

}

