package dz.algerietelecom.webservice.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "TEST_RECHARGE.RECHARGE_FACTURE")
public class FactureModel {

	@Id
	private String NUM_TRANS;
	private String NCLI;
	private String ND;
	private Float MONTANT;
	private String DATE_TRANSACTION;
	private String HEURE_TRANSACTION;
	private String STATUT;
	private String NUM_TRANS_S;
	private String DATE_TRAITEMENT_S;
	private String HEURE_TRAITEMENT_S;
	private String CODE_REPONSE_S;
	private String AUTH_CODE_S;
	private String NFACT;
	private String PERIODE;
	private String ANNEE;
	private String CATEG;
	private String DATE_TRAITEMENT;
	private String HEURE_TRAITEMENT;
	private String CODE_REPONSE;
	private String RESULT;
	private String SOURCE;
	
	public String getNCLI() {
		return NCLI;
	}
	public void setNCLI(String nCLI) {
		NCLI = nCLI;
	}
	public String getND() {
		return ND;
	}
	public void setND(String nD) {
		ND = nD;
	}
	public String getNUM_TRANS() {
		return NUM_TRANS;
	}
	public void setNUM_TRANS(String nUM_TRANS) {
		NUM_TRANS = nUM_TRANS;
	}
	public Float getMONTANT() {
		return MONTANT;
	}
	public void setMONTANT(Float mONTANT) {
		MONTANT = mONTANT;
	}
	public String getDATE_TRANSACTION() {
		return DATE_TRANSACTION;
	}
	public void setDATE_TRANSACTION(String dATE_TRANSACTION) {
		DATE_TRANSACTION = dATE_TRANSACTION;
	}
	public String getHEURE_TRANSACTION() {
		return HEURE_TRANSACTION;
	}
	public void setHEURE_TRANSACTION(String hEURE_TRANSACTION) {
		HEURE_TRANSACTION = hEURE_TRANSACTION;
	}
	public String getSTATUT() {
		return STATUT;
	}
	public void setSTATUT(String sTATUT) {
		STATUT = sTATUT;
	}
	public String getNUM_TRANS_S() {
		return NUM_TRANS_S;
	}
	public void setNUM_TRANS_S(String nUM_TRANS_S) {
		NUM_TRANS_S = nUM_TRANS_S;
	}
	public String getDATE_TRAITEMENT_S() {
		return DATE_TRAITEMENT_S;
	}
	public void setDATE_TRAITEMENT_S(String dATE_TRAITEMENT_S) {
		DATE_TRAITEMENT_S = dATE_TRAITEMENT_S;
	}
	public String getHEURE_TRAITEMENT_S() {
		return HEURE_TRAITEMENT_S;
	}
	public void setHEURE_TRAITEMENT_S(String hEURE_TRAITEMENT_S) {
		HEURE_TRAITEMENT_S = hEURE_TRAITEMENT_S;
	}
	public String getCODE_REPONSE_S() {
		return CODE_REPONSE_S;
	}
	public void setCODE_REPONSE_S(String cODE_REPONSE_S) {
		CODE_REPONSE_S = cODE_REPONSE_S;
	}
	public String getAUTH_CODE_S() {
		return AUTH_CODE_S;
	}
	public void setAUTH_CODE_S(String aUTH_CODE_S) {
		AUTH_CODE_S = aUTH_CODE_S;
	}
	public String getNFACT() {
		return NFACT;
	}
	public void setNFACT(String nFACT) {
		NFACT = nFACT;
	}
	public String getPERIODE() {
		return PERIODE;
	}
	public void setPERIODE(String pERIODE) {
		PERIODE = pERIODE;
	}
	public String getANNEE() {
		return ANNEE;
	}
	public void setANNEE(String aNNEE) {
		ANNEE = aNNEE;
	}
	public String getCATEG() {
		return CATEG;
	}
	public void setCATEG(String cATEG) {
		CATEG = cATEG;
	}
	public String getDATE_TRAITEMENT() {
		return DATE_TRAITEMENT;
	}
	public void setDATE_TRAITEMENT(String dATE_TRAITEMENT) {
		DATE_TRAITEMENT = dATE_TRAITEMENT;
	}
	public String getHEURE_TRAITEMENT() {
		return HEURE_TRAITEMENT;
	}
	public void setHEURE_TRAITEMENT(String hEURE_TRAITEMENT) {
		HEURE_TRAITEMENT = hEURE_TRAITEMENT;
	}
	public String getCODE_REPONSE() {
		return CODE_REPONSE;
	}
	public void setCODE_REPONSE(String cODE_REPONSE) {
		CODE_REPONSE = cODE_REPONSE;
	}
	public String getRESULT() {
		return RESULT;
	}
	public void setRESULT(String rESULT) {
		RESULT = rESULT;
	}
	public String getSOURCE() {
		return SOURCE;
	}
	public void setSOURCE(String sOURCE) {
		SOURCE = sOURCE;
	}

	
}
