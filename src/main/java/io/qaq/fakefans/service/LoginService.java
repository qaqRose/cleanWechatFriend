package io.qaq.fakefans.service;

/**
 * @author: qiu
 */
public interface LoginService {

	void login();

	void logout();

	void cancelLogin();

	boolean checkLoginType();

	boolean hasUserLogin();

	void setHasUserLogin(boolean hasLogin);
}
