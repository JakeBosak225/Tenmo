package com.techelevator.tenmo.accounts;

import java.util.List;

public interface AccountDAO {
	
	public List<Account> getAllAccounts();
	
	public Account getAccountById(int accountId);
	
	public Account getAccountByUserId(int userId);
	
	public void updateAccountBalance(Account account);

}
