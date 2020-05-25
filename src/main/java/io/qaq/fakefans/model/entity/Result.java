package io.qaq.fakefans.model.entity;

import io.qaq.fakefans.model.enums.ResultType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: qiu
 * 统一json返回结构
 */
@Data
public class Result {

	private Integer code;
	private String message;
	private Object data;

	private Result(Integer code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static Result build(ResultType type) {
		return build(type, null);
	}

	public static Result build(ResultType type, Object data) {
		return build(type, type.getDesc(), data);
	}

	public static Result build(ResultType type,String message, Object data) {
		return new Result(type.getValue(), message, data);
	}

}
