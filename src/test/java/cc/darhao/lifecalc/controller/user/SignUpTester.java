package cc.darhao.lifecalc.controller.user;

import cc.darhao.lifecalc.dao.UserMapper;
import cc.darhao.lifecalc.entity.User;
import cc.darhao.lifecalc.entity.vo.SignInVo;
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

@SpringBootTest
@AutoConfigureMockMvc
class SignUpTester {

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
        ResultFactory.Result<SignInVo> result = request();
        Assertions.assertEquals(ResultFactory.SUCCESS_CODE ,result.getCode());
        Assertions.assertEquals(
                1,
                userMapper.selectCount(new QueryWrapper<User>().eq("name", name).eq("password", password)));
        String userId = redisTemplate.opsForValue().get(AccessInterceptor.LIFECALC_USER_TOKEN + result.getData().getToken());
        Assertions.assertEquals(
                Integer.valueOf(userId),
                userMapper.selectOne(new QueryWrapper<User>().eq("name", name).eq("password", password)).getId());
    }


    @Test
    public void 用户名过长测试() throws Exception {
        name = "testtesttesttesttest";
        password = "123456";
        Assertions.assertEquals(ResultFactory.PARAMETER_EXCEPTION_CODE , requestString().getCode());
        Assertions.assertEquals(
                0,
                userMapper.selectCount(new QueryWrapper<User>().eq("name", name).eq("password", password)));
    }

    @Test
    public void 密码过短测试() throws Exception {
        name = "test";
        password = "12345";
        Assertions.assertEquals(ResultFactory.PARAMETER_EXCEPTION_CODE , requestString().getCode());
        Assertions.assertEquals(
                0,
                userMapper.selectCount(new QueryWrapper<User>().eq("name", name).eq("password", password)));
    }


    @Test
    public void 密码正常长度测试() throws Exception {
        name = "test";
        password = "1234567890123456";
        Assertions.assertEquals(ResultFactory.SUCCESS_CODE , requestString().getCode());
        Assertions.assertEquals(
                1,
                userMapper.selectCount(new QueryWrapper<User>().eq("name", name).eq("password", password)));
    }


    @Test
    public void 密码过长测试() throws Exception {
        name = "test";
        password = "12345678901234567";
        Assertions.assertEquals(ResultFactory.PARAMETER_EXCEPTION_CODE , requestString().getCode());
        Assertions.assertEquals(
                0,
                userMapper.selectCount(new QueryWrapper<User>().eq("name", name).eq("password", password)));
    }


    @Test
    public void 用户已存在测试() throws Exception {
        name = "test";
        password = "123123";
        Assertions.assertEquals(ResultFactory.SUCCESS_CODE , requestString().getCode());
        Assertions.assertEquals(ResultFactory.OPERATION_EXCEPTION_CODE , requestString().getCode());
        Assertions.assertEquals(
                1,
                userMapper.selectCount(new QueryWrapper<User>().eq("name", name).eq("password", password)));
    }


    @Test
    public void 空白参数测试() throws Exception {
        name = "         ";
        password = "    ";
        Assertions.assertEquals(ResultFactory.PARAMETER_EXCEPTION_CODE , requestString().getCode());
        Assertions.assertEquals(
                0,
                userMapper.selectCount(new QueryWrapper<User>().eq("name", name).eq("password", password)));
    }


    private ResultFactory.Result requestString() throws Exception {
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/user/signUp")
                        .param("name", name)
                        .param("password", password)
                        .header("accept", "application/json;charset=UTF-8"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn().getResponse().getContentAsString();
        //泛型反序列化
        return JSON.parseObject(json, new TypeReference<ResultFactory.Result<String>>(){});
    }


    private ResultFactory.Result request() throws Exception {
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/user/signUp")
                        .param("name", name)
                        .param("password", password)
                        .header("accept", "application/json;charset=UTF-8"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn().getResponse().getContentAsString();
        //泛型反序列化
        return JSON.parseObject(json, new TypeReference<ResultFactory.Result<SignInVo>>(){});
    }


    @AfterEach
    public void clear() {
        userMapper.delete(new QueryWrapper<User>().eq("name", name).eq("password", password));
    }


}
