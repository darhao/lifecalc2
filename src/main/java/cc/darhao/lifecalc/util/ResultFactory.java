package cc.darhao.lifecalc.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 结果工厂<br>
 * 创造带result字段和data字段的结果实体
 */
public class ResultFactory {

	public static final int SUCCESS_CODE = 200;
	public static final int OPERATION_EXCEPTION_CODE = 210;
	public static final int ACCESS_EXCEPTION_CODE = 211;
	public static final int THIRD_PARTY_EXCEPTION_CODE = 300;
	public static final int REQUEST_TIMEOUT_EXCEPTION_CODE = 310;
	public static final int PARAMETER_EXCEPTION_CODE = 400;
	public static final int OTHER_SERVER_EXCEPTION_CODE = 500;
	public static final int UNKNOWN_SERVER_EXCEPTION_CODE = 510;


	@Data
	@ToString
	public static class Result<T> {

		@ApiModelProperty("返回码：200表示成功；210表示用户错误；211表示用户权限错误；300表示第三方错误；" +
				"310表示第三方请求超时；400表示前端参数错误；500表示服务器错误；510表示服务器未知错误")
		private Integer code;
		@ApiModelProperty("返回内容")
		private T data;
		
	}

	
	public static Result succeed() {
		return succeed("操作成功");
	}
	
	
	public static Result succeed(Object data) {
		Result result = new Result();
		result.code = SUCCESS_CODE;
		result.data = data;
		return result;
	}
	
	
	public static Result failed(int code, Object errorMsg) {
		Result result = new Result();
		result.code = code;
		result.data = errorMsg;
		return result;
	}
	
}
