package dz.algerietelecom.webservice.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "TEST_RECHARGE.RECHARGES_NOK")
public class RechargeNokModel {

	private String ACCOUNT_NUM;
	@Id
	private String BATCH_SERIAL_NO;
	private Date DATE_RECH;
	private String NUM_TEL_B;
	private String CANAL;
	private String ETAT;
	private String MONTANT;
	private String PRODUCT;
	private String NUM_TEL_IP;
	private String SOURCE;
	private String COMMENTAIRE;
	private String TYPE;
	private String ID_RECHARGE;
	private String CAUSE;
	private String COID;
	public String getACCOUNT_NUM() {
		return ACCOUNT_NUM;
	}
	public void setACCOUNT_NUM(String aCCOUNT_NUM) {
		ACCOUNT_NUM = aCCOUNT_NUM;
	}
	public String getBATCH_SERIAL_NO() {
		return BATCH_SERIAL_NO;
	}
	public void setBATCH_SERIAL_NO(String bATCH_SERIAL_NO) {
		BATCH_SERIAL_NO = bATCH_SERIAL_NO;
	}
	public Date getDATE_RECH() {
		return DATE_RECH;
	}
	public void setDATE_RECH(Date dATE_RECH) {
		DATE_RECH = dATE_RECH;
	}
	public String getNUM_TEL_B() {
		return NUM_TEL_B;
	}
	public void setNUM_TEL_B(String nUM_TEL_B) {
		NUM_TEL_B = nUM_TEL_B;
	}
	public String getCANAL() {
		return CANAL;
	}
	public void setCANAL(String cANAL) {
		CANAL = cANAL;
	}
	public String getETAT() {
		return ETAT;
	}
	public void setETAT(String eTAT) {
		ETAT = eTAT;
	}
	public String getMONTANT() {
		return MONTANT;
	}
	public void setMONTANT(String mONTANT) {
		MONTANT = mONTANT;
	}
	public String getPRODUCT() {
		return PRODUCT;
	}
	public void setPRODUCT(String pRODUCT) {
		PRODUCT = pRODUCT;
	}
	public String getNUM_TEL_IP() {
		return NUM_TEL_IP;
	}
	public void setNUM_TEL_IP(String nUM_TEL_IP) {
		NUM_TEL_IP = nUM_TEL_IP;
	}
	public String getSOURCE() {
		return SOURCE;
	}
	public void setSOURCE(String sOURCE) {
		SOURCE = sOURCE;
	}
	public String getCOMMENTAIRE() {
		return COMMENTAIRE;
	}
	public void setCOMMENTAIRE(String cOMMENTAIRE) {
		COMMENTAIRE = cOMMENTAIRE;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getID_RECHARGE() {
		return ID_RECHARGE;
	}
	public void setID_RECHARGE(String iD_RECHARGE) {
		ID_RECHARGE = iD_RECHARGE;
	}
	public String getCAUSE() {
		return CAUSE;
	}
	public void setCAUSE(String cAUSE) {
		CAUSE = cAUSE;
	}
	public String getCOID() {
		return COID;
	}
	public void setCOID(String cOID) {
		COID = cOID;
	}
	
	
}
