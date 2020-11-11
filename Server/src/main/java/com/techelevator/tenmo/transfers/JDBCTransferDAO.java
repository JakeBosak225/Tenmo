package com.techelevator.tenmo.transfers;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JDBCTransferDAO implements TransferDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCTransferDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public Transfer makeTransfer (Transfer transfer) {
		String query = "INSERT INTO transfers VALUES (?, ?, ?, ?, ?, ?)";
		int nextId = getNextTransferId();
		
		jdbcTemplate.update(query, nextId, transfer.getTransferTypeId(), transfer.getTransferStatusId(), 
				transfer.getAccountFromId(),transfer.getAccountToId(), transfer.getAmount());
		
		transfer.setTransferId(nextId);
		
		return transfer;
	}
	
	@Override
	public void updateTransferStatus(Transfer transfer) {
		String query = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
		jdbcTemplate.update(query, transfer.getTransferStatusId(), transfer.getTransferId());
	}
	
	@Override
	public Transfer getTransferById (int id) {
		String query = "SELECT * FROM transfers WHERE transfer_id = ?";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
		
		if (rowSet.next()) {
			
			Transfer transfer = mapRowToTransfer(rowSet);
			return transfer;
		}
		
		return null;
	}
	
	@Override 
	public List<Transfer> getAllTransfersByAccountId(int id) {
		List<Transfer> transferList = new ArrayList<>();
		String query = "SELECT * FROM transfers WHERE account_from = ? OR account_to = ?";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id, id);
		
		while (rowSet.next()) {
			
			Transfer transfer = mapRowToTransfer(rowSet);
			transferList.add(transfer);
		}
		
		return transferList;
	}
	
	
	private Transfer mapRowToTransfer (SqlRowSet rowSet) {
		Transfer transfer = new Transfer();
		transfer.setTransferId(rowSet.getInt("transfer_id"));
		transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
		transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
		transfer.setAccountFromId(rowSet.getInt("account_from"));
		transfer.setAccountToId(rowSet.getInt("account_to"));
		transfer.setAmount(rowSet.getDouble("amount"));
		return transfer;
	}
	
	private int getNextTransferId() {
		String query = "SELECT nextval ('seq_transfer_id')";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query);
		
		if (rowSet.next()) {
			
			return rowSet.getInt(1);
		} else {
			
			return 0;
		}
	}
}
