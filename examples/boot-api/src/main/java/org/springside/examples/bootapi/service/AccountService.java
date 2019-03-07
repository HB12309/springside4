package org.springside.examples.bootapi.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.repository.AccountDao;
import org.springside.examples.bootapi.service.exception.ErrorCode;
import org.springside.examples.bootapi.service.exception.ServiceException;
import org.springside.modules.utils.text.HashUtil;
import org.springside.modules.utils.misc.IdGenerator;
import org.springside.modules.utils.text.EncodeUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

// Spring Bean的标识.
@Service
public class AccountService {

	private static Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private AccountDao accountDao;

	// 注入配置值
	@Value("${app.loginTimeoutSecs:600}")
	private int loginTimeoutSecs;

	// codehale metrics
	@Autowired
	private CounterService counterService;

	/**
	 * 这种 guava 缓存，就是服务启动的时候，作用域在这个 class 中的东西，是内存型的缓存咯？有 JVM 来开辟内存给它用？
	 */
	// guava cache
	private Cache<String, Account> loginUsers;

	/**
	 * https://blog.csdn.net/wo541075754/article/details/52174900
	 * 这种 对于 Bean 生命周期的管理，在后来的 Vue.js 里也有，所谓的钩子函数，原来这 Servlet 也是早期的佼佼者。
	 * 这种链式调用，常见于：lamba，还有 Node.js 中的 mongoose，很好用
	 */
	@PostConstruct
	public void init() {
		loginUsers = CacheBuilder
				.newBuilder()
				.maximumSize(1000)
				.expireAfterAccess(loginTimeoutSecs, TimeUnit.SECONDS)
				.build();
	}

	@Transactional(readOnly = true)
	public String login(String email, String password) {
		Account account = accountDao.findByEmail(email);

		if (account == null) {
			throw new ServiceException("User not exist", ErrorCode.UNAUTHORIZED);
		}

		if (!account.hashPassword.equals(hashPassword(password))) {
			throw new ServiceException("Password wrong", ErrorCode.UNAUTHORIZED);
		}

		String token = IdGenerator.uuid2();
		loginUsers.put(token, account);
		counterService.increment("loginUser");
		return token;
	}

	public void logout(String token) {
		Account account = loginUsers.getIfPresent(token);
		if (account == null) {
			logger.warn("logout an alreay logout token:" + token);
		} else {
			loginUsers.invalidate(token);
			counterService.decrement("loginUser");
		}
	}

	public Account getLoginUser(String token) {

		Account account = loginUsers.getIfPresent(token);

		if (account == null) {
			throw new ServiceException("User doesn't login", ErrorCode.UNAUTHORIZED);
		}

		return account;
	}

	@Transactional
	public void register(String email, String name, String password) {

		if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
			throw new ServiceException("Invalid parameter", ErrorCode.BAD_REQUEST);
		}

		Account account = new Account();
		account.email = email;
		account.name = name;
		account.hashPassword = hashPassword(password);
		accountDao.save(account);
	}

	protected static String hashPassword(String password) {
		return EncodeUtil.encodeBase64(HashUtil.sha1(password));
	}
}
