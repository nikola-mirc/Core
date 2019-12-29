package alfatec.view.wrappers;

import alfatec.model.user.LoginData;
import alfatec.model.user.User;

public class UserLoginConnection {

	private User user;
	private LoginData loginData;

	public UserLoginConnection(User u, LoginData ld) {
		this.user = u;
		this.loginData = ld;
	}

	public LoginData getLoginData() {
		return loginData;
	}

	public User getUser() {
		return user;
	}

	public void setLoginData(LoginData loginData) {
		this.loginData = loginData;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
