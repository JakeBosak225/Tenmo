package com.techelevator.tenmo.accounts;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JDBCAccountDAO implements AccountDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCAccountDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Account> getAllAccounts() {
		String query = "SELECT * FROM accounts";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query);

		List<Account> accounts = new ArrayList<>();

		while (rowSet.next()) {
			Account account = mapRowToAccount(rowSet);
			accounts.add(account);
		}

		return accounts;
	}

	@Override
	public Account getAccountById(int accountId) {
		String query = "SELECT * FROM accounts WHERE account_id=?";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, accountId);

		if (rowSet.next()) {
			Account account = mapRowToAccount(rowSet);
			return account;
		}

		return null;
	}

	@Override
	public Account getAccountByUserId(int userId) {
		String query = "SELECT * FROM users u " + "JOIN accounts a ON a.user_id = u.user_id " + "WHERE a.user_id = ?";
		SqlRowSet rowset = jdbcTemplate.queryForRowSet(query, userId);

		if (rowset.next()) {
			Account account = mapRowToAccount(rowset);
			return account;
		}

		return null;
	}

	@Override
	public void updateAccountBalance(Account account) {
		String query = "UPDATE accounts SET balance = ? WHERE account_id = ?";

		jdbcTemplate.update(query, account.getBalance(), account.getAccountId());
	}

	private Account mapRowToAccount(SqlRowSet rowset) {
		Account account = new Account();
		account.setAccountId(rowset.getInt("account_id"));
		account.setUserId(rowset.getInt("user_id"));
		account.setBalance(rowset.getDouble("balance"));

		return account;
	}

}
