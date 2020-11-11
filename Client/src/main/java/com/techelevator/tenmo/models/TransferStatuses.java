package com.techelevator.tenmo.models;

public class TransferStatuses {
	
	private int transferStatusId;
	private String transferStatusDescription;

	public TransferStatuses() {
	}

	public TransferStatuses(int transferStatusId, String transferStatusDescription) {
		this.transferStatusId = transferStatusId;
		this.transferStatusDescription = transferStatusDescription;
	}

	public int getTransferStatusId() {
		return transferStatusId;
	}

	public void setTransferStatusId(int transferStatusId) {
		this.transferStatusId = transferStatusId;
	}

	public String getTransferStatusDescription() {
		return transferStatusDescription;
	}

	public void setTransferStatusDescription(String transferStatusDescription) {
		this.transferStatusDescription = transferStatusDescription;
	}


}
