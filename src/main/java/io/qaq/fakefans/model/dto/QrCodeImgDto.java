package io.qaq.fakefans.model.dto;

import lombok.Data;

/**
 * @author: qiu
 * 二维码
 */
@Data
public class QrCodeImgDto {
	/**
	 * 二维码地址
	 */
	private String url;

	/**
	 * 等待时长
	 * 单位: 秒
	 */
	private Integer waitTime;
}
