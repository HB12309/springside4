package org.springside.examples.bootapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springside.examples.bootapi.domain.Account;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * CrudRepository默认有针对实体对象的CRUD方法.
 * 
 * Spring Data JPA 还会解释新增方法名生成新方法的实现.
 */

/**
 *  DAO：
 * data   access   object数据访问对象
 * 主要用来封装对数据库的访问。通过它可以把POJO持久化为PO，用PO组装出来VO、DTO 
 * DTO   ：
 * Data   Transfer   Object数据传输对象
 * 主要用于远程调用等需要大量传输对象的地方。
 * 比如我们一张表有100个字段，那么对应的PO就有100个属性。
 * 但是我们界面上只要显示10个字段，
 * 客户端用WEB   service来获取数据，没有必要把整个PO对象传递到客户端，
 * 这时我们就可以用只有这10个属性的DTO来传递结果到客户端，这样也不会暴露服务端表结构.到达客户端以后，如果用这个对象来对应界面显示，那此时它的身份就转为VO
 * ---------------------
 * 作者：zcywell
 * 来源：CSDN
 * 原文：https://blog.csdn.net/zcywell/article/details/7204186
 * 版权声明：本文为博主原创文章，转载请附上博文链接！
 */
public interface AccountDao extends CrudRepository<Account, Long> {

	Account findByEmail(String email);
}
