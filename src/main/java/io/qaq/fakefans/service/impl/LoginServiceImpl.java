package io.qaq.fakefans.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import io.qaq.fakefans.handler.CustomHandler;
import io.qaq.fakefans.service.CleanService;
import io.qaq.fakefans.service.LoginService;
import io.qaq.fakefans.thread.DeadlineThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author: qiu
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	private final CustomHandler customHandler;
	private final CleanService cleanService;

	public LoginServiceImpl(CustomHandler customHandler,
	                        CleanService cleanService) {
		this.customHandler = customHandler;
		this.cleanService = cleanService;
	}

	@Value("${qrcode.image.path}")
	private String qrCodePath;

	/**
	 * 由内容控制
	 * 保证只有一个用户登录
	 */
	private volatile static boolean hasUserLogin = false;

	/**
	 * 后台线程处理
	 */
	@Override
	public void login(LoginService loginService) {// 判断文件
		String userDir = System.getProperty("user.dir");
		log.info("用户目录, {}", userDir);
		qrCodePath = userDir+ "//" +qrCodePath;
		if(FileUtil.isFile(qrCodePath)) {
			throw new RuntimeException("qrcode.image.path 应该是一个目录地址");
		}
		boolean exist = FileUtil.exist(qrCodePath);
		if(!exist) {
			//不存在, 则创建
			FileUtil.mkdir(qrCodePath);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 保存登陆二维码图片的路径，这里需要在本地新建目录
				String qrPath = qrCodePath;
				hasUserLogin = true;
				// 设置30秒内未登录, 解锁登录
				DeadlineThread dead = new DeadlineThread(loginService);
				dead.start();
				// 【注入】
				// 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，
				Wechat wechat = new Wechat(customHandler, qrPath);
				if(checkLoginType()) {
					// 停止线程
					dead.stopDeadLine();
				}
				// 注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片
				wechat.start();
				// 开始清除粉丝
				cleanService.clean();
				// 清除完毕, 登出
				logout();
			}
		}).start();
	}

	@Override
	public void logout() {
		WechatTools.logout();
		setHasUserLogin(false);
	}

	/**
	 * 查询登录状态
	 * 在线 true
	 * 离线 false
	 * @return
	 */
	@Override
	public boolean checkLoginType() {
		boolean status = WechatTools.getWechatStatus();
		return status;
	}

	/**
	 * 外部只能通过此方法获取
	 * @return
	 */
	@Override
	public boolean hasUserLogin() {
		return hasUserLogin;
	}

	@Override
	public void setHasUserLogin(boolean hasLogin) {
		hasUserLogin = hasLogin;
	}
}
