package com.techelevator.tenmo.accounts;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

	private AccountDAO accountDao;

	public AccountController(AccountDAO accountDao) {
		this.accountDao = accountDao;
	}

	@RequestMapping(path = "/accounts", method = RequestMethod.GET)
	public List<Account> getAllAccounts() {
		return accountDao.getAllAccounts();
	}

	@RequestMapping(path = "/accounts/{id}", method = RequestMethod.GET)
	public Account getAccountById(@PathVariable int id) {
		return accountDao.getAccountById(id);
	}

	@RequestMapping(path = "/accounts/searchUserId", method = RequestMethod.GET)
	public Account getAccountByUserID(@RequestParam(value = "userId") int userId) {
		return accountDao.getAccountByUserId(userId);
	}

	@RequestMapping(path = "/accounts/{id}", method = RequestMethod.PUT)
	public void updateAccountBalance(@PathVariable int id, @RequestBody Account account) {
		accountDao.updateAccountBalance(account);
	}
}
