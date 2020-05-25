package io.qaq.fakefans.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import io.qaq.fakefans.model.dto.QrCodeImg;
import io.qaq.fakefans.model.entity.Result;
import io.qaq.fakefans.model.enums.ResultType;
import io.qaq.fakefans.service.CleanService;
import io.qaq.fakefans.service.FriendService;
import io.qaq.fakefans.service.LoginService;
import io.qaq.fakefans.service.MsgService;
import io.qaq.fakefans.thread.DeadlineThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: qiu
 * 微信登录控制器
 */
@Slf4j
@Controller
@RequestMapping("wx")
public class WxLoginController {

	@Value("${qrcode.image.path}")
	private String qrCodePath;

	private Lock lock = new ReentrantLock();

	private final LoginService loginService;

	public WxLoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	/**
	 * 登录页面
	 * @return
	 */
	@GetMapping
	public String loginPage() {
		return "login";
	}

	/**
	 * 获取二维码图片资源
	 * @param fileName
	 * @return
	 */
	@GetMapping(value = "/img/{fileName}",  produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] img(@PathVariable("fileName") String fileName) {
		String file = qrCodePath +"//"+ DateUtil.today()+"//"+fileName;
		if(FileUtil.exist(file)) {
			return FileUtil.readBytes(file);
		}
		return null;
	}

	/**
	 * 二维码图片地址
	 * @return
	 */
	@PostMapping("login/qr")
	@ResponseBody
	public Result loginQr() {
		if(loginService.hasUserLogin()) {
			return Result.build(ResultType.ERROR);
		}
		lock.lock();
		QrCodeImg img = new QrCodeImg();
		try {
			if(loginService.hasUserLogin()) {
				return Result.build(ResultType.ERROR);
			}
			try {
				// 获取二维码地址
				String qrImageFilePath = qrCodePath+"//QR.jpg";
				if(FileUtil.exist(qrImageFilePath)) {
					FileUtil.del(qrImageFilePath);
				}
				// 启动程序(后台线程)
				loginService.login(loginService);
				for (int i = 1; i <= 10 ; i++) {
					if (!FileUtil.exist(qrImageFilePath)) {
						// 获取不到, 休眠1秒
						Thread.sleep(1000);
						if(i == 10) {
							return Result.build(ResultType.NET_ERROR);
						}
					}
				}
				//随机
				String imgName = UUID.randomUUID().toString() + ".jpg";
				String newFile = qrCodePath +"//"+ DateUtil.today() +"//"+ imgName;

				log.info("newFile, {}", newFile);
				// 复制
				FileUtil.copy(qrImageFilePath, newFile, true);
				// 复制之后把原文件删除, 确保只要一个
				FileUtil.del(qrImageFilePath);
				// 地址
				img.setUrl("/wx/img/" + imgName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}
		return Result.build(ResultType.OK, img);
	}

}
