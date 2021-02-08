package cc.darhao.lifecalc.controller.plan;

import cc.darhao.lifecalc.dao.PlanMapper;
import cc.darhao.lifecalc.dao.UserMapper;
import cc.darhao.lifecalc.entity.Plan;
import cc.darhao.lifecalc.entity.User;
import cc.darhao.lifecalc.entity.vo.PlanBody;
import cc.darhao.lifecalc.entity.vo.PlanBodyItem;
import cc.darhao.lifecalc.entity.vo.PlanVo;
import cc.darhao.lifecalc.interceptor.AccessInterceptor;
import cc.darhao.lifecalc.util.ResultFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class GetTester {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private List<Integer> user1PlanIds = new LinkedList<>();
    private List<Integer> user2PlanIds = new LinkedList<>();

    private String planId;
    private String token;

    private Integer user1Id,user2Id;


    @Test
    public void 冒烟测试() throws Exception {
        planId = String.valueOf(user1PlanIds.get(0));
        token = "test1";
        ResultFactory.Result<PlanVo> result = request();
        Assertions.assertEquals(ResultFactory.SUCCESS_CODE ,result.getCode());
        Assertions.assertEquals(Integer.valueOf(planId), result.getData().getId());
    }


    @Test
    public void 非该用户所属计划测试() throws Exception {
        planId = String.valueOf(user2PlanIds.get(0));
        token = "test1";
        ResultFactory.Result<PlanVo> result = requestString();
        Assertions.assertEquals(ResultFactory.PARAMETER_EXCEPTION_CODE ,result.getCode());
    }


    @Test
    public void 不存在的计划ID测试() throws Exception {
        int notExistId = 0;
        for (Integer user1PlanId : user1PlanIds) {
            notExistId += user1PlanId;
        }
        for (Integer user2PlanId : user2PlanIds) {
            notExistId += user2PlanId;
        }
        planId = String.valueOf(notExistId);
        token = "test1";
        ResultFactory.Result<PlanVo> result = requestString();
        Assertions.assertEquals(ResultFactory.PARAMETER_EXCEPTION_CODE ,result.getCode());
    }


    private ResultFactory.Result request() throws Exception {
        String json = getResultJson();
        //泛型反序列化
        return JSON.parseObject(json, new TypeReference<ResultFactory.Result<PlanVo>>(){});
    }


    private ResultFactory.Result requestString() throws Exception {
        String json = getResultJson();
        //泛型反序列化
        return JSON.parseObject(json, new TypeReference<ResultFactory.Result<String>>(){});
    }


    private String getResultJson() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/plan/get")
                        .param("planId", planId)
                        .param("token", token)
                        .header("accept", "application/json;charset=UTF-8"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn().getResponse().getContentAsString();
    }


    @AfterEach
    public void clear() {
        planMapper.delete(new QueryWrapper<Plan>().eq("user", user1Id).or().eq("user", user2Id));
        userMapper.delete(new QueryWrapper<User>().eq("id",user1Id).or().eq("id",user2Id));
        redisTemplate.delete(AccessInterceptor.LIFECALC_USER_TOKEN + "test1");
        redisTemplate.delete(AccessInterceptor.LIFECALC_USER_TOKEN + "test2");
    }


    @BeforeEach
    public void init() {
        User user = new User().setName("test").setPassword("123456").setSignUpTime(new Date()).setSignInTime(new Date());
        userMapper.insert(user);
        user1Id = user.getId();
        redisTemplate.opsForValue().set(AccessInterceptor.LIFECALC_USER_TOKEN + "test1", String.valueOf(user1Id));
        User user2 = new User().setName("test2").setPassword("123456").setSignUpTime(new Date()).setSignInTime(new Date());
        userMapper.insert(user2);
        user2Id = user2.getId();
        redisTemplate.opsForValue().set(AccessInterceptor.LIFECALC_USER_TOKEN + "test2", String.valueOf(user2Id));
        for (int i = 0; i < 5; i++){
            PlanBody body = new PlanBody();
            List<PlanBodyItem> items = new LinkedList<>();
            items.add(new PlanBodyItem().setName("工作阶段").setStart(1).setEnd(10).setNet(100000).setNetRate(0.2f).setEarnRate(0.2f));
            items.add(new PlanBodyItem().setName("自由阶段").setStart(11).setEnd(60).setNet(0).setNetRate(0.1f).setEarnRate(0.2f));
            body.setAge(20).setCpi(0.03f).setItems(items);
            Plan plan = new Plan();
            plan.setCreateTime(new Date()).setUpdateTime(new Date()).setName("test-plan")
                    .setUser(user1Id).setBody(JSON.toJSONString(body));
            planMapper.insert(plan);
            user1PlanIds.add(plan.getId());
        }
        for (int i = 0; i < 5; i++){
            PlanBody body = new PlanBody();
            List<PlanBodyItem> items = new LinkedList<>();
            items.add(new PlanBodyItem().setName("工作阶段").setStart(1).setEnd(10).setNet(100000).setNetRate(0.2f).setEarnRate(0.2f));
            items.add(new PlanBodyItem().setName("自由阶段").setStart(11).setEnd(60).setNet(0).setNetRate(0.1f).setEarnRate(0.2f));
            body.setAge(20).setCpi(0.03f).setItems(items);
            Plan plan = new Plan();
            plan.setCreateTime(new Date()).setUpdateTime(new Date()).setName("test-plan")
                    .setUser(user2Id).setBody(JSON.toJSONString(body));
            planMapper.insert(plan);
            user2PlanIds.add(plan.getId());
        }

    }


}
