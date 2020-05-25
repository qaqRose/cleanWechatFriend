package io.qaq.fakefans.model.enums;

import lombok.Getter;

/**
 * @author: qiu
 */
@Getter
public enum ResultType {

	OK(1, "成功")
	,ERROR(404, "失败")
	,NET_ERROR(500, "网络超时")
	;
	private Integer value;
	private String desc;

	ResultType(Integer value, String desc) {
		this.value = value;
		this.desc = desc;
	}
}
