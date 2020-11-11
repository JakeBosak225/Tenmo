package com.techelevator.tenmo.transfers;

import java.util.List;

public interface TransferDAO {

	public Transfer makeTransfer (Transfer transfer);
	
	public Transfer getTransferById (int id);

	public List<Transfer> getAllTransfersByAccountId(int id);
	
	public void updateTransferStatus(Transfer transfer);
}
