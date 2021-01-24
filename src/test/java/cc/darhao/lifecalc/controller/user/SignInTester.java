package cc.darhao.lifecalc.controller.user;

import cc.darhao.lifecalc.dao.UserMapper;
import cc.darhao.lifecalc.entity.User;
import cc.darhao.lifecalc.interceptor.AccessInterceptor;
import cc.darhao.lifecalc.util.ResultFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
class SignInTester {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private String name;
    private String password;


    @Test
    public void 冒烟测试() throws Exception {
        name = "test";
        password = "123456";
        User user = new User().setName(name).setPassword(password).setSignUpTime(new Date()).setSignInTime(new Date());
        userMapper.insert(user);
        ResultFactory.Result result = request();
        Assertions.assertEquals(ResultFactory.SUCCESS_CODE ,result.getCode());
        String userId = redisTemplate.opsForValue().get(AccessInterceptor.LIFECALC_USER_TOKEN + result.getData());
        Assertions.assertEquals(
                Integer.valueOf(userId),
                userMapper.selectOne(new QueryWrapper<User>().eq("name", name).eq("password", password)).getId());
    }


    @Test
    public void 错误测试() throws Exception {
        name = "test";
        password = "123456";
        ResultFactory.Result result = request();
        Assertions.assertEquals(ResultFactory.OPERATION_EXCEPTION_CODE ,result.getCode());
    }


    private ResultFactory.Result request() throws Exception {
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/user/signIn")
                .param("name", name)
                .param("password", password)
                .header("accept", "application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        //泛型反序列化
        return JSON.parseObject(json, new TypeReference<ResultFactory.Result<String>>(){});
    }


    @AfterEach
    public void clear() {
        userMapper.delete(new QueryWrapper<User>().eq("name", name).eq("password", password));
    }


}
