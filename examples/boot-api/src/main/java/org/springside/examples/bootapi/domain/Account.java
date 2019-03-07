package org.springside.examples.bootapi.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html
 * Java一整套，javax相当于 java 的补充，其实本身已经非常完备了！牛逼。这就是Java的持久化的东西撒
 * 原来这就是 JPA 啊，Java Persistence API
 * ToStringBuilder 不就是一个 StringUtils 的感觉嘛，不同的API而已嘛，对于Kotlin来说，没必要啊
 */
// JPA实体类的标识
@Entity
public class Account {

	// JPA 主键标识, 策略为由数据库生成主键
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	public String email;
	public String name;
	public String hashPassword;

	public Account() {

	}

	public Account(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
