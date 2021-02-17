package cc.darhao.lifecalc.controller;


import cc.darhao.lifecalc.annotation.Log;
import cc.darhao.lifecalc.dao.UserMapper;
import cc.darhao.lifecalc.entity.User;
import cc.darhao.lifecalc.entity.vo.SignInVo;
import cc.darhao.lifecalc.interceptor.AccessInterceptor;
import cc.darhao.lifecalc.util.ResultFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  用户前端控制器
 * </p>
 *
 * @author 鲁智深
 * @since 2021-01-19
 */
@Api(tags = "用户相关")
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @ApiOperation(value = "注册", notes = "注册一个新用户，成功返回一个token字符串")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名，不超过16个字符", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "密码，6-16个字符", paramType = "query", required = true)
    })
    @PostMapping("/signUp")
    @Log("注册")
    public ResultFactory.Result<String> signUp(
            @NotBlank(message = "用户名不能为空")
            @Length(max = 16, message = "用户名长度不能超过16个字符")
            String name,
            @NotBlank(message = "密码不能为空")
            @Length(min = 6, max = 16, message = "密码长度不能小于6个字符或超过16个字符")
            String password) {
        Integer count = userMapper.selectCount(new QueryWrapper<User>().eq("name", name));
        if(count.intValue() != 0){
            return ResultFactory.failed(ResultFactory.OPERATION_EXCEPTION_CODE,"用户名已存在");
        }
        User user = new User().setName(name).setPassword(password).setSignUpTime(new Date());
        userMapper.insert(user);
        return signIn(name, password);
    }


    @ApiOperation(value = "登录", notes = "通过用户密码登录，成功返回一个token字符串，7天失效")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名，不超过16个字符", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "密码，6-16个字符", paramType = "query", required = true)
    })
    @PostMapping("/signIn")
    @Log("登录")
    public ResultFactory.Result<String> signIn(
            @NotBlank(message = "用户名不能为空")
            @Length(max = 16, message = "用户名长度不能超过16个字符")
            String name,
            @NotBlank(message = "密码不能为空")
            @Length(min = 6, max = 16, message = "密码长度不能小于6个字符或超过16个字符")
            String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("name", name).eq("password",password));
        if(user == null){
            return ResultFactory.failed(ResultFactory.OPERATION_EXCEPTION_CODE,"用户名密码错误");
        }
        user.setSignInTime(new Date());
        userMapper.updateById(user);
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(AccessInterceptor.LIFECALC_USER_TOKEN + uuid, user.getId().toString(), 7, TimeUnit.DAYS);
        SignInVo signInVo = new SignInVo().setToken(uuid).setName(name);
        return ResultFactory.succeed(signInVo);
    }


    @ApiOperation(value = "登出", notes = "登出用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", paramType = "query", required = true),
    })
    @PostMapping("/signOut")
    @Log("登出")
    public ResultFactory.Result<String> signOut(String token) {
        redisTemplate.delete(AccessInterceptor.LIFECALC_USER_TOKEN + token);
        return ResultFactory.succeed();
    }

}

