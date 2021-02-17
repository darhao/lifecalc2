package cc.darhao.lifecalc.controller;


import cc.darhao.lifecalc.annotation.Log;
import cc.darhao.lifecalc.dao.PlanMapper;
import cc.darhao.lifecalc.entity.Plan;
import cc.darhao.lifecalc.entity.vo.PageVo;
import cc.darhao.lifecalc.entity.vo.PlanBody;
import cc.darhao.lifecalc.entity.vo.PlanVo;
import cc.darhao.lifecalc.interceptor.AccessInterceptor;
import cc.darhao.lifecalc.util.ResultFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 *  计划前端控制器
 * </p>
 *
 * @author 鲁智深
 * @since 2021-01-19
 */
@Api(tags = "计划相关")
@Validated
@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @ApiOperation(value = "列出用户所有计划", notes = "列出指定用户的所有计划，但不返回每个计划的详细内容，即body内的内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageNo", value = "页码，不传表示为第一页，每页10条", paramType = "query"),
    })
    @PostMapping("/list")
    @Log("列出所有计划")
    public ResultFactory.Result<PageVo<PlanVo>> list(String token, Integer pageNo) {
        if(pageNo != null && pageNo < 1){
            throw new ValidationException("页码不能小于1");
        }
        if(pageNo == null){
            pageNo = new Integer(1);
        }
        String userId = redisTemplate.opsForValue().get(AccessInterceptor.LIFECALC_USER_TOKEN + token);
        Page<Plan> page = planMapper.selectPage(new Page<Plan>(pageNo, 10), new QueryWrapper<Plan>().eq("user",userId));
        List<PlanVo> planVos = new LinkedList<>();
        for (Plan record : page.getRecords()) {
            planVos.add(new PlanVo(record));
        }
        PageVo<PlanVo> pageVo = new PageVo<>(page, planVos);
        return ResultFactory.succeed(pageVo);
    }


    @ApiOperation(value = "获取计划内容", notes = "获取一个计划的详细内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", paramType = "query", required = true),
            @ApiImplicitParam(name = "planId", value = "计划ID", paramType = "query", required = true)
    })
    @PostMapping("/get")
    @Log("获取单个计划")
    public ResultFactory.Result<PlanVo> get(
            String token,
            @NotNull(message = "id不能为空")
            Integer planId) {
        String userId = redisTemplate.opsForValue().get(AccessInterceptor.LIFECALC_USER_TOKEN + token);
        Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("id", planId).eq("user", userId));
        if (plan == null) {
            return ResultFactory.failed(ResultFactory.PARAMETER_EXCEPTION_CODE,"该用户没有该计划");
        }
        return ResultFactory.succeed(new PlanVo(plan));
    }


    @ApiOperation(value = "增加计划", notes = "为用户保存计划，成功会返回实体id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", paramType = "query", required = true),
            @ApiImplicitParam(name = "name", value = "计划名，不超过64个字符", paramType = "query", required = true),
            @ApiImplicitParam(name = "planJson", value = "计划内容，格式为json，参考PlanBody模型", paramType = "query", required = true)
    })
    @PostMapping("/add")
    @Log("添加一个计划")
    public ResultFactory.Result<String> add(
            String token,
            String name,
            @NotBlank(message = "计划内容不能为空")
            String planJson) {
        if(name != null && name.length() > 64){
            throw new ValidationException("计划名字太长");
        }
        String userId = redisTemplate.opsForValue().get(AccessInterceptor.LIFECALC_USER_TOKEN + token);
        try {
            PlanBody planBody = JSON.parseObject(planJson, PlanBody.class);
        } catch (JSONException e) {
            throw new ValidationException("Json格式错误");
        }
        Plan plan = new Plan().setUser(Integer.valueOf(userId)).setName(name==null?"未命名计划":name).
                setBody(planJson).setCreateTime(new Date());
        planMapper.insert(plan);
        return ResultFactory.succeed(plan.getId());
    }


    @ApiOperation(value = "修改计划", notes = "为用户修改计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", paramType = "query", required = true),
            @ApiImplicitParam(name = "name", value = "计划名，不超过64个字符", paramType = "query"),
            @ApiImplicitParam(name = "planJson", value = "计划内容，格式为json，参考PlanVo模型", paramType = "query"),
            @ApiImplicitParam(name = "planId", value = "计划ID", paramType = "query", required = true)
    })
    @PostMapping("/update")
    @Log("更新一个计划")
    public ResultFactory.Result<String> update(
            String token,
            String name,
            String planJson,
            @NotNull(message = "id不能为空")
            Integer planId) {
        if(name != null && name.length() > 64){
            throw new ValidationException("计划名字太长");
        }
        String userId = redisTemplate.opsForValue().get(AccessInterceptor.LIFECALC_USER_TOKEN + token);
        try {
            PlanBody planBody = JSON.parseObject(planJson, PlanBody.class);
        } catch (JSONException e) {
            throw new ValidationException("Json格式错误");
        }
        Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("id", planId).eq("user", userId));
        plan.setName(name).setBody(planJson).setUpdateTime(new Date());
        planMapper.updateById(plan);
        return ResultFactory.succeed();
    }


    @ApiOperation(value = "删除计划", notes = "为用户删除计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "token", paramType = "query", required = true),
            @ApiImplicitParam(name = "planId", value = "计划ID", paramType = "query", required = true)
    })
    @PostMapping("/remove")
    @Log("删除一个计划")
    public ResultFactory.Result<String> remove(
            String token,
            @NotNull(message = "id不能为空")
            Integer planId) {
        String userId = redisTemplate.opsForValue().get(AccessInterceptor.LIFECALC_USER_TOKEN + token);
        planMapper.delete(new QueryWrapper<Plan>().eq("id", planId).eq("user", userId));
        return ResultFactory.succeed();
    }

}

