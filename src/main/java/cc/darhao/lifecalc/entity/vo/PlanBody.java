package cc.darhao.lifecalc.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
@ApiModel(value="PlanBody对象")
public class PlanBody implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前年龄")
    private Integer age;

    @ApiModelProperty(value = "cpi")
    private Float cpi;

    @ApiModelProperty(value = "计划阶段条目")
    private List<PlanBodyItem> items;

}
