package cc.darhao.lifecalc.entity.vo;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


/**
* @Auther 鲁智深
* @Date 2021/1/20 1:02
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="PageVo对象")
public class PageVo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前页码")
    private Integer currentPage;

    @ApiModelProperty(value = "总页数")
    private Integer totalPage;

    @ApiModelProperty(value = "内容")
    private List<T> records;

    public PageVo(Page page, List<T> records){
        this.currentPage = Math.toIntExact(page.getCurrent());
        this.totalPage = Math.toIntExact(page.getTotal() / page.getSize() +
                (page.getTotal() % page.getSize() == 0 ? 0 : 1));
        this.records = records;
    }

}
