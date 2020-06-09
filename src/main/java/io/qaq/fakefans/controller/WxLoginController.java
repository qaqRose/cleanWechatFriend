package io.qaq.fakefans.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import io.qaq.fakefans.model.dto.QrCodeImgDto;
import io.qaq.fakefans.model.entity.Result;
import io.qaq.fakefans.model.enums.ResultType;
import io.qaq.fakefans.service.LoginService;
import io.qaq.fakefans.thread.DeadlineThread;
import io.qaq.fakefans.util.CmdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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

	@Value("${scan.waitTime}")
	private Integer waitTime;

	@Value("${qrcode.image.path}")
	private String qrCodePath;

	@Value("${restart.cmd}")
	private String restartSh;

	private Integer loginTime = 0;

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
		String file = qrCodePath +File.separator+ DateUtil.today()+File.separator+fileName;
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
		QrCodeImgDto img = new QrCodeImgDto();
		lock.lock();
		try {
			if(loginService.hasUserLogin()) {
				return Result.build(ResultType.ERROR);
			}
			// 没人登录
			if(loginTime >= 2) {
				log.info("登录次数为 {}, 正在重新启动", loginTime);
				// 应用重启
				CmdUtil.call(restartSh);
			}
			loginTime += 1;
			try {
				// 获取二维码地址
				String qrImageFilePath = qrCodePath+ File.separator +"QR.jpg";
				if(FileUtil.exist(qrImageFilePath)) {
					FileUtil.del(qrImageFilePath);
				}
				// 启动程序(后台线程)
				loginService.login();
				// 30 秒超时
				for (int i = 1; i <= waitTime; i++) {
					if (!FileUtil.exist(qrImageFilePath)) {
						// 获取不到, 休眠1秒
						Thread.sleep(1000);
						log.warn("{} 文件不存在, 时间: {}s", qrImageFilePath, i);
						if(i == waitTime) {
							// 获取图片失败, 关闭此次登录请求
							loginService.cancelLogin();
							return Result.build(ResultType.NET_ERROR);
						}
					} else  {
						log.info("{} 找到文件", qrImageFilePath);
						// 启动 deadline 线程, 30秒内不扫码, 取消登录
						DeadlineThread dead = new DeadlineThread(loginService, waitTime);
						dead.start();
						break;
					}
				}
				//随机
				String imgName = UUID.randomUUID().toString() + ".jpg";
				String newFile = qrCodePath +File.separator+ DateUtil.today() +File.separator+ imgName;

				log.info("newFile, {}", newFile);
				// 复制
				FileUtil.copy(qrImageFilePath, newFile, true);
				// 地址
				img.setUrl("/wx/img/" + imgName);
				img.setWaitTime(waitTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}
		return Result.build(ResultType.OK, img);
	}

}
