package cc.darhao.lifecalc.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@ApiModel(value="PlanBodyItem对象")
public class PlanBodyItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "计划细节名")
    private String name;

    @ApiModelProperty(value = "第几年开始")
    private Integer start;

    @ApiModelProperty(value = "第几年结束")
    private Integer end;

    @ApiModelProperty(value = "每年净收入")
    private Integer net;

    @ApiModelProperty(value = "年净收入增长率")
    private Float netRate;

    @ApiModelProperty(value = "理财年化收益率")
    private Float earnRate;

}
