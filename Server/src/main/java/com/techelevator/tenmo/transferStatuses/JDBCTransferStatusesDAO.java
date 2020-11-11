package com.techelevator.tenmo.transferStatuses;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.transferTypes.TransferType;

@Component
public class JDBCTransferStatusesDAO implements TransferStatusesDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCTransferStatusesDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public TransferStatuses getTransferStatusById (int statusId) {
		String query = "SELECT * FROM transfer_statuses WHERE transfer_status_id = ?";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, statusId);
		
		if (rowSet.next()) {
			
			TransferStatuses transferStatus = mapRowToTransferStatuses(rowSet);
			return transferStatus;
		}
		
		return null;
	}
	
	private TransferStatuses mapRowToTransferStatuses(SqlRowSet rowSet) {
		TransferStatuses transferStatus = new TransferStatuses();
		transferStatus.setTransferStatusId(rowSet.getInt("transfer_status_id"));
		transferStatus.setTransferStatusDescription(rowSet.getString("transfer_status_desc"));
		return transferStatus;
	}
}
