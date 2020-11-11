package com.techelevator.tenmo.transferStatuses;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferStatusesController {
	
	private TransferStatusesDAO transferStatusDao;
	
	public TransferStatusesController (TransferStatusesDAO transferStatusDao) {
		this.transferStatusDao = transferStatusDao;
	}
	
	@RequestMapping(path = "/transferStatus/{id}", method = RequestMethod.GET)
	public TransferStatuses getTransferStatusById (@PathVariable int id) {
		return transferStatusDao.getTransferStatusById(id);
	}
}
