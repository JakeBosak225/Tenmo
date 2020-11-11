package com.techelevator.tenmo.models;

public class TransferType {

	private int transferTypeId;
	private String transferTypeDescription;

	public TransferType() {
	}

	public TransferType(int transferTypeId, String transferTypeDescription) {
		this.transferTypeId = transferTypeId;
		this.transferTypeDescription = transferTypeDescription;
	}

	public int getTransferTypeId() {
		return transferTypeId;
	}

	public void setTransferTypeId(int transferTypeId) {
		this.transferTypeId = transferTypeId;
	}

	public String getTransferTypeDescription() {
		return transferTypeDescription;
	}

	public void setTransferTypeDescription(String transferTypeDescription) {
		this.transferTypeDescription = transferTypeDescription;
	}
}
