package io.qaq.fakefans.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
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
import java.util.Objects;

/**
 * @author: qiu
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	private final CustomHandler customHandler;
	private final CleanService cleanService;

	@Value("${qrcode.image.path}")
	private String qrCodePath;

	@Value("${scan.waitTime}")
	private Integer waitTime;

	/**二维码文件目录 */
	private String qrCodeFileUrl;

	/** 微信类 */
	private Wechat wechat;

	public LoginServiceImpl(CustomHandler customHandler,
	                        CleanService cleanService) {
		this.customHandler = customHandler;
		this.cleanService = cleanService;
	}


	/**
	 * 由内容控制
	 * 保证只有一个用户登录
	 */
	private volatile static boolean hasUserLogin = false;

	private void init() {
		if(StrUtil.isBlank(qrCodeFileUrl)) {
			qrCodeFileUrl = qrCodePath;
			log.info("二维码目录, {}", qrCodePath);

			if(FileUtil.isFile(qrCodeFileUrl)) {
				log.warn("配置qrcode.image.path 应该是一个目录地址, {}", qrCodeFileUrl);
				qrCodeFileUrl = FileUtil.getParent(qrCodeFileUrl, 1);
				log.info("获取父级目录: {}", qrCodeFileUrl);
			}
			boolean exist = FileUtil.exist(qrCodeFileUrl);
			if(!exist) {
				//不存在, 则创建
				FileUtil.mkdir(qrCodeFileUrl);
			}

			if(Objects.isNull(wechat)) {
				// 保存登陆二维码图片的路径，这里需要在本地新建目录
				wechat = new Wechat(customHandler, qrCodeFileUrl);
			}
		}
	}

	/**
	 * 后台线程处理
	 */
	@Override
	public void login() {// 判断文件
		init();
		new Thread(new Runnable() {
			@Override
			public void run() {
				hasUserLogin = true;
				// 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，
				wechat.login();
				if(checkLoginType()) {
					// 停止线程
//					dead.stopDeadLine();
				} else {
					return;
				}
				// 开始处理消息
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
		wechat.stop();
		WechatTools.logout();
		setHasUserLogin(false);
	}

	@Override
	public void cancelLogin() {
		boolean b = wechat.cancelLogin();
		setHasUserLogin(false);
		if(b) {
			log.info("关闭登录请求成功");
		} else {
			log.error("关闭登录请求失败");
		}
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
