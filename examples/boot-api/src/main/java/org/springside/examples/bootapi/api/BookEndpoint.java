package org.springside.examples.bootapi.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.domain.Book;
import org.springside.examples.bootapi.dto.BookDto;
import org.springside.examples.bootapi.service.AccountService;
import org.springside.examples.bootapi.service.BookAdminService;
import org.springside.examples.bootapi.service.BookBorrowService;
import org.springside.examples.bootapi.service.exception.ErrorCode;
import org.springside.examples.bootapi.service.exception.ServiceException;
import org.springside.modules.utils.mapper.BeanMapper;
import org.springside.modules.web.MediaTypes;

// Spring Restful MVC Controller的标识, 直接输出内容，不调用template引擎.
@RestController
public class BookEndpoint {

	private static Logger logger = LoggerFactory.getLogger(BookEndpoint.class);

	/**
	 * 自动注入，是 Spring的功能，找接口的实现类，见：https://www.jianshu.com/p/9062a92fbf9a
	 */
	@Autowired
	private AccountService accountService;

	@Autowired
	private BookAdminService adminService;

	@Autowired
	private BookBorrowService borrowService;

	@RequestMapping(value = "/api/books", produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listAllBook(Pageable pageable) {
		Iterable<Book> books = adminService.findAll(pageable);
		logger.debug("books", books);

		return BeanMapper.mapList(books, BookDto.class);
	}

	/**
	 * 像 {id} 这种，在 restcontroller 和 feign 中，可能会有出入
	 * 在微服务的实践过程中，Spring Cloud Ribbon 和 Spring Cloud Hystrix 通常一起使用。
	 * Spring Cloud Feign 是对这两个基础工具的更高层次封装，在 Netflix Feign 的基础上扩展了对 Spring MVC 的注解支持，提供了一种声明式的 Web 服务客户端定义方式。
	 *
	 * 作者：JiangYue
	 * 链接：https://juejin.im/post/5adee93551882567137dd71f
	 * 来源：掘金
	 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/api/books/{id}", produces = MediaTypes.JSON_UTF_8)
	public BookDto listOneBook(@PathVariable("id") Long id) {
		Book book = adminService.findOne(id);

		return BeanMapper.map(book, BookDto.class);
	}

	@RequestMapping(value = "/api/books", method = RequestMethod.POST, consumes = MediaTypes.JSON_UTF_8)
	public void createBook(@RequestBody BookDto bookDto,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		// 使用Header中的Token，查找登录用户
		Account currentUser = accountService.getLoginUser(token);

		// 使用BeanMapper, 将与外部交互的BookDto对象复制为应用内部的Book对象
		Book book = BeanMapper.map(bookDto, Book.class);

		// 保存Book对象
		adminService.saveBook(book, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/modify", method = RequestMethod.POST, consumes = MediaTypes.JSON_UTF_8)
	public void modifyBook(@RequestBody BookDto bookDto,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		Book book = BeanMapper.map(bookDto, Book.class);
		adminService.modifyBook(book, currentUser.id);
	}

	@RequestMapping(value = "/api/books/{id}/delete")
	public void deleteBook(@PathVariable("id") Long id, @RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		adminService.deleteBook(id, currentUser.id);
	}

	@RequestMapping(value = "/api/books/{id}/request")
	public void applyBorrowRequest(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.applyBorrowRequest(id, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/cancel")
	public void cancelBorrowRequest(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.cancelBorrowRequest(id, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/confirm")
	public void markBookBorrowed(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.markBookBorrowed(id, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/reject")
	public void rejectBorrowRequest(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.rejectBorrowRequest(id, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/return")
	public void markBookReturned(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.markBookReturned(id, currentUser);
	}

	@RequestMapping(value = "/api/mybook", produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listMyBook(@RequestParam(value = "token", required = false) String token, Pageable pageable) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		List<Book> books = adminService.listMyBook(currentUser.id, pageable);
		return BeanMapper.mapList(books, BookDto.class);
	}

	@RequestMapping(value = "/api/myborrowedbook", produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listMyBorrowedBook(@RequestParam(value = "token", required = false) String token,
			Pageable pageable) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		List<Book> books = borrowService.listMyBorrowedBook(currentUser.id, pageable);
		return BeanMapper.mapList(books, BookDto.class);
	}

	private void checkToken(String token) {
		if (token == null) {
			throw new ServiceException("No token in request", ErrorCode.NO_TOKEN);
		}
	}
}
