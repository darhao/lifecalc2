package cc.darhao.lifecalc.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@ApiModel(value="SignInVo对象")
public class SignInVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "用户名")
    private String name;

}
