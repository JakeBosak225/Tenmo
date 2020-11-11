package com.techelevator.tenmo.transferTypes;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JDBCTransferTypeDAO implements TransferTypeDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCTransferTypeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public TransferType getTranferTypeById (int transferId) {
		String query = "SELECT * FROM transfer_types WHERE transfer_type_id = ?";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, transferId);
		
		if (rowSet.next()) {
			
			TransferType transferType = mapRowToTransferType(rowSet);
			return transferType;
		} 
		
		return null;
	}
	
	
	private TransferType mapRowToTransferType(SqlRowSet rowSet) {
		TransferType transferType = new TransferType();
		transferType.setTransferTypeId(rowSet.getInt("transfer_type_id"));
		transferType.setTransferTypeDescription(rowSet.getString("transfer_type_desc"));
		return transferType;
	}
}
