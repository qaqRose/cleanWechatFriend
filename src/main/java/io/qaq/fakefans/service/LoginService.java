package io.qaq.fakefans.service;

/**
 * @author: qiu
 */
public interface LoginService {

	void login(LoginService loginService);

	void logout();

	boolean checkLoginType();

	boolean hasUserLogin();

	void setHasUserLogin(boolean hasLogin);
}
