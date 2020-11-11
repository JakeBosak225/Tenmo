package com.techelevator.tenmo.transferTypes;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferTypeController {

	private TransferTypeDAO transferTypeDao;

	public TransferTypeController(TransferTypeDAO transferTypeDao) {
		this.transferTypeDao = transferTypeDao;
	}

	@RequestMapping(path = "/transferType/{id}", method = RequestMethod.GET)
	public TransferType getTransferTypeById(@PathVariable int id) {
		return transferTypeDao.getTranferTypeById(id);
	}

}
