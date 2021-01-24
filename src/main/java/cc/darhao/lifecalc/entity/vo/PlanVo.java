package cc.darhao.lifecalc.entity.vo;

import cc.darhao.lifecalc.entity.Plan;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 鲁智深
 * @since 2021-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="PlanVo对象")
public class PlanVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "计划名")
    private String name;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "计划内容")
    private PlanBody body;


    public PlanVo(Plan plan){
        this.id = plan.getId();
        this.name = plan.getName();
        this.createTime = plan.getCreateTime();
        this.updateTime = plan.getUpdateTime();
        this.body = JSON.parseObject(plan.getBody(), PlanBody.class);
    }

}
