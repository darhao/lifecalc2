package cc.darhao.lifecalc.interceptor;

import cc.darhao.lifecalc.util.ExceptionUtil;
import cc.darhao.lifecalc.util.ResultFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ValidationException;

/**
* @Auther 鲁智深
* @Date 2021/1/18 23:40
*/
@Slf4j
@ControllerAdvice
public class ExceptionCatcher {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultFactory.Result exceptionHandler(Exception e){
        ExceptionUtil.logError(log, e);
        return ResultFactory.failed(ResultFactory.OTHER_SERVER_EXCEPTION_CODE, ExceptionUtil.getFullInfo(e));
    }


    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResultFactory.Result handle(ValidationException e) {
        ExceptionUtil.logError(log, e);
        return ResultFactory.failed(ResultFactory.PARAMETER_EXCEPTION_CODE, ExceptionUtil.getFullInfo(e));
    }

}
