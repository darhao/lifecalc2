package cc.darhao.lifecalc.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @Auther 鲁智深
* @Date 2021/1/19 1:00
*/
public class ResponseUtil {

    public static void sendResult(HttpServletResponse response, ResultFactory.Result result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().append(JSON.toJSONString(result));
    }

}
