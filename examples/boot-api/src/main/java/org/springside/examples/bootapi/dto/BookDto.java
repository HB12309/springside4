package org.springside.examples.bootapi.dto;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;

public class BookDto {

	public Long id;
	public String bookId;
	public String title;
	public String url;
	public String status;

	public AccountDto owner;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	public Date onboardDate;

	public AccountDto borrower;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	public Date borrowDate;

	/**
	 * 为什么要重写 toString ? 常常很多都 Override，怪了
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
