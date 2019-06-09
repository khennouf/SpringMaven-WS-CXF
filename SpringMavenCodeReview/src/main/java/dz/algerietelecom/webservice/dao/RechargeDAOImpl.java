package dz.algerietelecom.webservice.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.mysql.jdbc.Connection;

import dz.algerietelecom.webservice.domain.RechargeModel;
import dz.algerietelecom.webservice.domain.RechargeNokModel;
import dz.algerietelecom.webservice.domain.ResultRechargeModel;

public class RechargeDAOImpl implements RechargeDao {

	private DataSource dataSource;

	private static final Logger logger = LoggerFactory
			.getLogger(RechargeDAOImpl.class);

	public RechargeDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void insertRecharge(RechargeModel c) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "INSERT INTO TEST_RECHARGE.RECHARGES "
				+ "(BATCH_SERIAL_NO,ACCOUNT_NUM,CANAL,COMMENTAIRE,DATE_RECH,ETAT,ID_RECHARGE,MONTANT,NUM_TEL_B,NUM_TEL_IP,PRODUCT,SOURCE,TYPE,COID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			jdbcTemplate.update(
					sql,
					new Object[] { c.getBATCH_SERIAL_NO(), c.getACCOUNT_NUM(),
							c.getCANAL(), c.getCOMMENTAIRE(), c.getDATE_RECH(),
							c.getETAT(), c.getID_RECHARGE(), c.getMONTANT(),
							c.getNUM_TEL_B(), c.getNUM_TEL_IP(),
							c.getPRODUCT(), c.getSOURCE(), c.getTYPE(),
							c.getCOID() });

			jdbcTemplate.execute("commit");

		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
	}

	public void insertRechargeNok(RechargeNokModel c) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "INSERT INTO TEST_RECHARGE.RECHARGES_NOK "
				+ "(ACCOUNT_NUM,BATCH_SERIAL_NO,DATE_RECH,NUM_TEL_B,CANAL,ETAT,MONTANT,PRODUCT,NUM_TEL_IP,SOURCE,COMMENTAIRE,TYPE,ID_RECHARGE,CAUSE,COID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			jdbcTemplate.update(
					sql,
					new Object[] { c.getACCOUNT_NUM(), c.getBATCH_SERIAL_NO(),
							c.getDATE_RECH(), c.getNUM_TEL_B(), c.getCANAL(),
							c.getETAT(), c.getMONTANT(), c.getPRODUCT(),
							c.getNUM_TEL_IP(), c.getSOURCE(),
							c.getCOMMENTAIRE(), c.getTYPE(),
							c.getID_RECHARGE(), c.getCAUSE(), c.getCOID() });

			jdbcTemplate.execute("commit");

		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
	}

	public int checkVoucher(String serialNumber) {

		List<RechargeModel> result = new ArrayList<RechargeModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int occurance = 0;

		try {

			String sql = "Select * From TEST_RECHARGE.RECHARGES where RECHARGES.BATCH_SERIAL_NO='"
					+ serialNumber + "'";

			result = jdbcTemplate.query(sql, new RowMapper<RechargeModel>() {

				@Override
				public RechargeModel mapRow(ResultSet rs, int rowNumber)
						throws SQLException {
					RechargeModel recharge = new RechargeModel();

					recharge.setBATCH_SERIAL_NO(rs.getString(1));
					recharge.setACCOUNT_NUM(rs.getString(2));
					recharge.setCANAL(rs.getString(3));
					recharge.setCOMMENTAIRE(rs.getString(4));
					recharge.setDATE_RECH(rs.getTimestamp(5));
					recharge.setETAT(rs.getString(6));
					recharge.setID_RECHARGE(rs.getString(7));
					recharge.setMONTANT(rs.getString(8));
					recharge.setNUM_TEL_B(rs.getString(9));
					recharge.setNUM_TEL_IP(rs.getString(10));
					recharge.setPRODUCT(rs.getString(11));
					recharge.setSOURCE(rs.getString(12));
					recharge.setTYPE(rs.getString(13));
					recharge.setCOID(rs.getString(14));

					return recharge;
				}

			});

			if (result.size() != 0) {

				occurance = occurance + 1;
			}

		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
		return occurance;
	}

	public ResultRechargeModel selectMessage(Number code) {

		List<ResultRechargeModel> result = new ArrayList<ResultRechargeModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		try {

			String sql = "Select * From TEST_RECHARGE.MESSAGE where MESSAGE.CODE='"
					+ code + "'";

			result = jdbcTemplate.query(sql,
					new RowMapper<ResultRechargeModel>() {

						@Override
						public ResultRechargeModel mapRow(ResultSet rs,
								int rowNumber) throws SQLException {
							ResultRechargeModel message = new ResultRechargeModel();

							message.setCode(rs.getString(1));
							message.setMessage(rs.getString(2));
							message.setExp_date(rs.getString(3));

							return message;
						}

					});
		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}

		if (result.size() != 0) {
			return result.get(0);
		} else {
			return new ResultRechargeModel();
		}

	}

	public List<RechargeModel> historyRechargeAdsl(String accountNumber) {
		List<RechargeModel> recharges = new ArrayList<RechargeModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {

			String sql = "Select * From TEST_RECHARGE.RECHARGES where RECHARGES.ACCOUNT_NUM='"
					+ accountNumber + "' and RECHARGES.TYPE='ADSL'";

			recharges = jdbcTemplate.query(sql, new RowMapper<RechargeModel>() {

				@Override
				public RechargeModel mapRow(ResultSet rs, int rowNumber)
						throws SQLException {
					RechargeModel recharge = new RechargeModel();

					recharge.setBATCH_SERIAL_NO(rs.getString(1));
					recharge.setACCOUNT_NUM(rs.getString(2));
					recharge.setCANAL(rs.getString(3));
					recharge.setCOMMENTAIRE(rs.getString(4));
					recharge.setDATE_RECH(rs.getTimestamp(5));
					recharge.setETAT(rs.getString(6));
					recharge.setID_RECHARGE(rs.getString(7));
					recharge.setMONTANT(rs.getString(8));
					recharge.setNUM_TEL_B(rs.getString(9));
					recharge.setNUM_TEL_IP(rs.getString(10));
					recharge.setPRODUCT(rs.getString(11));
					recharge.setSOURCE(rs.getString(12));
					recharge.setTYPE(rs.getString(13));
					recharge.setCOID(rs.getString(14));

					return recharge;
				}

			});
		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}

		return recharges;
	}

	public List<RechargeNokModel> historyRechargeAdslNok(String accountNumber) {
		List<RechargeNokModel> recharges = new ArrayList<RechargeNokModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		try {

			String sql = "Select * From TEST_RECHARGE.RECHARGES_NOK where RECHARGES_NOK.ACCOUNT_NUM='"
					+ accountNumber + "' and RECHARGES_NOK.TYPE='ADSL'";

			recharges = jdbcTemplate.query(sql,
					new RowMapper<RechargeNokModel>() {

						@Override
						public RechargeNokModel mapRow(ResultSet rs,
								int rowNumber) throws SQLException {
							RechargeNokModel recharge = new RechargeNokModel();

							recharge.setACCOUNT_NUM(rs.getString(1));
							recharge.setBATCH_SERIAL_NO(rs.getString(2));
							recharge.setDATE_RECH(rs.getTimestamp(3));
							recharge.setNUM_TEL_B(rs.getString(4));
							recharge.setCANAL(rs.getString(5));
							recharge.setETAT(rs.getString(6));
							recharge.setMONTANT(rs.getString(7));
							recharge.setPRODUCT(rs.getString(8));
							recharge.setNUM_TEL_IP(rs.getString(9));
							recharge.setSOURCE(rs.getString(10));
							recharge.setCOMMENTAIRE(rs.getString(11));
							recharge.setTYPE(rs.getString(12));
							recharge.setID_RECHARGE(rs.getString(13));
							recharge.setCAUSE(rs.getString(14));
							recharge.setCOID(rs.getString(15));

							return recharge;
						}

					});
		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}

		return recharges;
	}

	public List<RechargeModel> historyRechargeVod(String accountNumber) {
		List<RechargeModel> recharges = new ArrayList<RechargeModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "Select * From TEST_RECHARGE.RECHARGES where RECHARGES.ACCOUNT_NUM='"
					+ accountNumber + "' and RECHARGES.TYPE='VOD'";

			recharges = jdbcTemplate.query(sql, new RowMapper<RechargeModel>() {

				@Override
				public RechargeModel mapRow(ResultSet rs, int rowNumber)
						throws SQLException {
					RechargeModel recharge = new RechargeModel();

					recharge.setBATCH_SERIAL_NO(rs.getString(1));
					recharge.setACCOUNT_NUM(rs.getString(2));
					recharge.setCANAL(rs.getString(3));
					recharge.setCOMMENTAIRE(rs.getString(4));
					recharge.setDATE_RECH(rs.getTimestamp(5));
					recharge.setETAT(rs.getString(6));
					recharge.setID_RECHARGE(rs.getString(7));
					recharge.setMONTANT(rs.getString(8));
					recharge.setNUM_TEL_B(rs.getString(9));
					recharge.setNUM_TEL_IP(rs.getString(10));
					recharge.setPRODUCT(rs.getString(11));
					recharge.setSOURCE(rs.getString(12));
					recharge.setTYPE(rs.getString(13));
					recharge.setCOID(rs.getString(14));

					return recharge;
				}

			});
		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
		return recharges;
	}

	public List<RechargeNokModel> historyRechargeVodNok(String accountNumber) {

		List<RechargeNokModel> recharges = new ArrayList<RechargeNokModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			String sql = "Select * From TEST_RECHARGE.RECHARGES_NOK where RECHARGES_NOK.ACCOUNT_NUM='"
					+ accountNumber + "' and RECHARGES_NOK.TYPE='VOD'";

			recharges = jdbcTemplate.query(sql,
					new RowMapper<RechargeNokModel>() {

						@Override
						public RechargeNokModel mapRow(ResultSet rs,
								int rowNumber) throws SQLException {
							RechargeNokModel recharge = new RechargeNokModel();

							recharge.setACCOUNT_NUM(rs.getString(1));
							recharge.setBATCH_SERIAL_NO(rs.getString(2));
							recharge.setDATE_RECH(rs.getTimestamp(3));
							recharge.setNUM_TEL_B(rs.getString(4));
							recharge.setCANAL(rs.getString(5));
							recharge.setETAT(rs.getString(6));
							recharge.setMONTANT(rs.getString(7));
							recharge.setPRODUCT(rs.getString(8));
							recharge.setNUM_TEL_IP(rs.getString(9));
							recharge.setSOURCE(rs.getString(10));
							recharge.setCOMMENTAIRE(rs.getString(11));
							recharge.setTYPE(rs.getString(12));
							recharge.setID_RECHARGE(rs.getString(13));
							recharge.setCAUSE(rs.getString(14));
							recharge.setCOID(rs.getString(15));

							return recharge;
						}

					});
		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
		return recharges;
	}

	@Override
	public RechargeModel VoucherUsed(String batch_serial_no) {

		List<RechargeModel> recharge = new ArrayList<RechargeModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {

			String sql = "Select * From TEST_RECHARGE.RECHARGES where RECHARGES.BATCH_SERIAL_NO='"
					+ batch_serial_no + "'";
			recharge = jdbcTemplate.query(sql, new RowMapper<RechargeModel>() {
				@Override
				public RechargeModel mapRow(ResultSet rs, int rowNumber)
						throws SQLException {
					RechargeModel recharge = new RechargeModel();

					recharge.setBATCH_SERIAL_NO(rs.getString(1));
					recharge.setACCOUNT_NUM(rs.getString(2));
					recharge.setCANAL(rs.getString(3));
					recharge.setCOMMENTAIRE(rs.getString(4));
					recharge.setDATE_RECH(rs.getTimestamp(5));
					recharge.setETAT(rs.getString(6));
					recharge.setID_RECHARGE(rs.getString(7));
					recharge.setMONTANT(rs.getString(8));
					recharge.setNUM_TEL_B(rs.getString(9));
					recharge.setNUM_TEL_IP(rs.getString(10));
					recharge.setPRODUCT(rs.getString(11));
					recharge.setSOURCE(rs.getString(12));
					recharge.setTYPE(rs.getString(13));
					recharge.setCOID(rs.getString(14));

					return recharge;
				}
			});
		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
		if (recharge.size() != 0) {
			return recharge.get(0);
		} else {
			return new RechargeModel();
		}

	}

	@Override
	public int checkRecharge(String accountNumber, Date dateR) {

		List<RechargeModel> result = new ArrayList<RechargeModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int occurance = 0;

		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

		try {

			String sql = "Select * From TEST_RECHARGE.RECHARGES where RECHARGES.ACCOUNT_NUM='"
					+ accountNumber
					+ "' and TO_CHAR(RECHARGES.DATE_RECH,'yyyy-MM-dd') like '"
					+ sd.format(dateR) + "%' and RECHARGES.SOURCE='MOBILIS'";

			result = jdbcTemplate.query(sql, new RowMapper<RechargeModel>() {

				@Override
				public RechargeModel mapRow(ResultSet rs, int rowNumber)
						throws SQLException {
					RechargeModel recharge = new RechargeModel();

					recharge.setBATCH_SERIAL_NO(rs.getString(1));
					recharge.setACCOUNT_NUM(rs.getString(2));
					recharge.setCANAL(rs.getString(3));
					recharge.setCOMMENTAIRE(rs.getString(4));
					recharge.setDATE_RECH(rs.getTimestamp(5));
					recharge.setETAT(rs.getString(6));
					recharge.setID_RECHARGE(rs.getString(7));
					recharge.setMONTANT(rs.getString(8));
					recharge.setNUM_TEL_B(rs.getString(9));
					recharge.setNUM_TEL_IP(rs.getString(10));
					recharge.setPRODUCT(rs.getString(11));
					recharge.setSOURCE(rs.getString(12));
					recharge.setTYPE(rs.getString(13));
					recharge.setCOID(rs.getString(14));

					return recharge;
				}

			});

			if (result.size() != 0) {

				occurance = 1;
			}

		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
		return occurance;

	}

	@Override
	public int checkTransaction(String numTransaction) {

		List<RechargeModel> result = new ArrayList<RechargeModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int occurance = 0;

		try {

			String sql = "Select * From TEST_RECHARGE.RECHARGES where RECHARGES.BATCH_SERIAL_NO='"
					+ numTransaction + "' ";

			result = jdbcTemplate.query(sql, new RowMapper<RechargeModel>() {

				@Override
				public RechargeModel mapRow(ResultSet rs, int rowNumber)
						throws SQLException {
					RechargeModel recharge = new RechargeModel();

					recharge.setBATCH_SERIAL_NO(rs.getString(1));
					recharge.setACCOUNT_NUM(rs.getString(2));
					recharge.setCANAL(rs.getString(3));
					recharge.setCOMMENTAIRE(rs.getString(4));
					recharge.setDATE_RECH(rs.getTimestamp(5));
					recharge.setETAT(rs.getString(6));
					recharge.setID_RECHARGE(rs.getString(7));
					recharge.setMONTANT(rs.getString(8));
					recharge.setNUM_TEL_B(rs.getString(9));
					recharge.setNUM_TEL_IP(rs.getString(10));
					recharge.setPRODUCT(rs.getString(11));
					recharge.setSOURCE(rs.getString(12));
					recharge.setTYPE(rs.getString(13));
					recharge.setCOID(rs.getString(14));

					return recharge;
				}

			});

			if (result.size() != 0) {

				occurance = 1;
			}

		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
		
		return occurance;

	}

}
