package io.qaq.fakefans.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: qiu
 */
@Slf4j
public class CmdUtil {

	/**
	 * 执行sh脚本命令
	 * @param bashPath
	 */
	public static void call(String bashPath){
		try {
			Runtime runtime = Runtime.getRuntime();
			Process pro = runtime.exec(bashPath);
			int status = pro.waitFor();
			if (status != 0){
				log.error("执行sh脚本失败");
				return;
			}
			log.info("执行sh脚本成功");
		}
		catch (Exception e){
			log.error("执行脚本失败", e);
		}
	}
}
