package io.qaq.fakefans.thread;

import cn.hutool.core.date.DateUtil;
import io.qaq.fakefans.service.LoginService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author: qiu
 * 限制用户在30秒输入token
 * 否则退出登录
 */
@Slf4j
public class DeadlineThread extends Thread {

	private volatile boolean isRunning = true;
	private long startTimestamp;
	private LoginService loginService;

	public DeadlineThread(LoginService loginService) {
		this.startTimestamp = new Date().getTime();
		this.loginService = loginService;
	}

	/**
	 * 登录后30秒内未确认
	 * 自动登录
	 */
	@Override
	public void run() {
		// 多出3秒钟, 用于登录时间
		long deadline = 30 * 1000 + 3000;
		log.info("开始时间: {}, 结束时间: {}",
				DateUtil.now(),
				DateUtil.formatDateTime(DateUtil.date(System.currentTimeMillis()+deadline)));
		while (isRunning) {
			long nowTime = System.currentTimeMillis();
			if(nowTime - deadline > startTimestamp) {
				log.error("30秒未登录, 设置未登录");
				loginService.setHasUserLogin(false);
				break;
			}
		}
	}

	/**
	 * 用户登录之后,
	 * 手动停止线程,
	 * 否则会自动退出
	 */
	public void stopDeadLine() {
		log.info("Deadline 线程停止");
		isRunning = false;
	}
}
