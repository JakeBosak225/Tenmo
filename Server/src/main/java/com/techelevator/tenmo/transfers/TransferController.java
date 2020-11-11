package com.techelevator.tenmo.transfers;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {

	private TransferDAO transferDao;
	
	public TransferController(TransferDAO transferDao) {
		
		this.transferDao = transferDao;
	}
	
	@RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
	public Transfer getTransferById (@PathVariable int id) {
		return transferDao.getTransferById(id);
	}
	
	@RequestMapping(path = "/transfers/byAccount", method = RequestMethod.GET)
	public List<Transfer> getAllTransfersForAccount(@RequestParam(value = "account_from") int fromAccount, @RequestParam(value = "account_to") int toAccount) {
		return transferDao.getAllTransfersByAccountId(fromAccount);
	}
	
	@RequestMapping(path = "/transfers", method = RequestMethod.POST)
	public Transfer makeTransfer (@RequestBody Transfer aTransfer) {
		return transferDao.makeTransfer(aTransfer);
	}
	
	@RequestMapping(path = "/transfers/{id}", method = RequestMethod.PUT)
	public void updateTransferStatus(@PathVariable int id, @RequestBody Transfer transfer) {
		transferDao.updateTransferStatus(transfer);
	}
}
