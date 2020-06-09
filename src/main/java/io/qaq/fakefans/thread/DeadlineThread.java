package io.qaq.fakefans.thread;

import cn.hutool.core.date.DateUtil;
import io.qaq.fakefans.service.LoginService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author: qiu
 * 限制用户在30秒扫码登录
 * 否则退出登录
 */
@Slf4j
public class DeadlineThread extends Thread {

	private volatile boolean isRunning = true;
	private long startTimestamp;
	private LoginService loginService;
	private Integer waitTime;

	/**
	 * @param loginService 用于设置未登录状态
	 * @param waitTime 等待时长
	 */
	public DeadlineThread(LoginService loginService, Integer waitTime) {
		this.startTimestamp = new Date().getTime();
		this.loginService = loginService;
		this.waitTime = waitTime;
	}

	/**
	 * 获取二维码后30秒内未扫码
	 * 设置登录状态为 false
	 */
	@Override
	public void run() {
		log.info("DeadlineThread 启动");
		// 多出3秒钟, 用于登录时间
		long deadline = waitTime * 1000 + 3000;
		log.info("开始时间: {}, 结束时间: {}",
				DateUtil.now(),
				DateUtil.formatDateTime(DateUtil.date(System.currentTimeMillis()+deadline)));
		while (isRunning) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(loginService.checkLoginType()) {
				break;
			} else {
				long nowTime = System.currentTimeMillis();
				if(nowTime - deadline > startTimestamp) {
					log.error("{} 秒未登录, 设置未登录", waitTime);
					loginService.cancelLogin();
					break;
				}
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
