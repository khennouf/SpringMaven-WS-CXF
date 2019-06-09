package dz.algerietelecom.webservice.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import dz.algerietelecom.webservice.domain.FactureModel;
import dz.algerietelecom.webservice.domain.RechargeModel;


public class FactureDAOImpl implements FactureDao {

	private DataSource dataSource;

	private static final Logger logger = LoggerFactory
			.getLogger(FactureDAOImpl.class);

	public FactureDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void insertFacture(FactureModel fact) {

		  
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "INSERT INTO TEST_RECHARGE.RECHARGE_FACTURE "
				+ "(NCLI,ND,NUM_TRANS,MONTANT,DATE_TRANSACTION,HEURE_TRANSACTION,STATUT,NUM_TRANS_S,DATE_TRAITEMENT_S,HEURE_TRAITEMENT_S,CODE_REPONSE_S,AUTH_CODE_S,NFACT,PERIODE,ANNEE,CATEG,DATE_TRAITEMENT,HEURE_TRAITEMENT,CODE_REPONSE,RESULT,SOURCE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			jdbcTemplate.update(
					sql,
					new Object[] { fact.getNCLI(), fact.getND(),
							fact.getNUM_TRANS(), fact.getMONTANT(),
							fact.getDATE_TRANSACTION(),
							fact.getHEURE_TRANSACTION(), fact.getSTATUT(),
							fact.getNUM_TRANS_S(), fact.getDATE_TRAITEMENT_S(),
							fact.getHEURE_TRAITEMENT_S(),
							fact.getCODE_REPONSE_S(), fact.getAUTH_CODE_S(),
							fact.getNFACT(), fact.getPERIODE(),
							fact.getANNEE(), fact.getCATEG(),
							fact.getDATE_TRAITEMENT(),
							fact.getHEURE_TRAITEMENT(), fact.getCODE_REPONSE(), fact.getRESULT(), fact.getSOURCE() });

			jdbcTemplate.execute("commit");

		} catch (Exception e) {

			logger.error("Error SQL request  :", e);

		}
	}
	
	public int checkNumTransaction(String numTransaction) {

		List<FactureModel> result = new ArrayList<FactureModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int occurance = 0;

		try {

			String sql = "Select * From TEST_RECHARGE.RECHARGE_FACTURE where RECHARGE_FACTURE.NUM_TRANS='"
					+ numTransaction + "'";

			result = jdbcTemplate.query(sql, new RowMapper<FactureModel>() {

				@Override
				public FactureModel mapRow(ResultSet rs, int rowNumber)
						throws SQLException {
					FactureModel facture = new FactureModel();

					facture.setNCLI(rs.getString(1));
					facture.setND(rs.getString(2));
					facture.setNUM_TRANS(rs.getString(3));
					facture.setMONTANT(rs.getFloat(4));
					facture.setDATE_TRANSACTION(rs.getString(5));
					facture.setHEURE_TRANSACTION(rs.getString(6));
					facture.setSTATUT(rs.getString(7));
					facture.setNUM_TRANS_S(rs.getString(8));
					facture.setDATE_TRAITEMENT_S(rs.getString(9));
					facture.setHEURE_TRAITEMENT_S(rs.getString(10));
					facture.setCODE_REPONSE_S(rs.getString(11));
					facture.setAUTH_CODE_S(rs.getString(12));
					facture.setNFACT(rs.getString(13));
					facture.setPERIODE(rs.getString(14));
					facture.setANNEE(rs.getString(15));
					facture.setCATEG(rs.getString(16));
					facture.setDATE_TRAITEMENT(rs.getString(17));
					facture.setHEURE_TRAITEMENT(rs.getString(18));
					facture.setCODE_REPONSE(rs.getString(19));
					facture.setRESULT(rs.getString(20));
					facture.setSOURCE(rs.getString(21));

					return facture;
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
	
	public int checkIdTransaction(Integer idTransaction) {

		List<FactureModel> result = new ArrayList<FactureModel>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int occurance = 0;

		try {

			String sql = "Select * From TEST_RECHARGE.RECHARGE_FACTURE where RECHARGE_FACTURE.ID_TRANS='"
					+ idTransaction + "' ";

			result = jdbcTemplate.query(sql, new RowMapper<FactureModel>() {

				@Override
				public FactureModel mapRow(ResultSet rs, int rowNumber)
						throws SQLException {
					FactureModel facture = new FactureModel();

					facture.setNCLI(rs.getString(1));
					facture.setND(rs.getString(2));
					facture.setNUM_TRANS(rs.getString(3));
					facture.setMONTANT(rs.getFloat(4));
					facture.setDATE_TRANSACTION(rs.getString(5));
					facture.setHEURE_TRANSACTION(rs.getString(6));
					facture.setSTATUT(rs.getString(7));
					facture.setNUM_TRANS_S(rs.getString(8));
					facture.setDATE_TRAITEMENT_S(rs.getString(9));
					facture.setHEURE_TRAITEMENT_S(rs.getString(10));
					facture.setCODE_REPONSE_S(rs.getString(11));
					facture.setAUTH_CODE_S(rs.getString(12));
					facture.setNFACT(rs.getString(13));
					facture.setPERIODE(rs.getString(14));
					facture.setANNEE(rs.getString(15));
					facture.setCATEG(rs.getString(16));
					facture.setDATE_TRAITEMENT(rs.getString(17));
					facture.setHEURE_TRAITEMENT(rs.getString(18));
					facture.setCODE_REPONSE(rs.getString(19));
					facture.setRESULT(rs.getString(20));
					facture.setSOURCE(rs.getString(21));

					return facture;
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
