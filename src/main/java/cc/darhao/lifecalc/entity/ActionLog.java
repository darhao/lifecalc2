package cc.darhao.lifecalc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@ApiModel(value="ActionLog对象", description="操作日志")
public class ActionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "操作时间")
    private Date time;

    @ApiModelProperty(value = "操作者")
    private String user;

    @ApiModelProperty(value = "请求方法")
    private String method;

    @ApiModelProperty(value = "请求参数")
    private String parameters;

    @ApiModelProperty(value = "返回码")
    private Integer resultCode;

    @ApiModelProperty(value = "返回值")
    private String response;

    @ApiModelProperty(value = "操作说明")
    private String action;

    @ApiModelProperty(value = "耗时，毫秒")
    private Integer consumeTime;


}
