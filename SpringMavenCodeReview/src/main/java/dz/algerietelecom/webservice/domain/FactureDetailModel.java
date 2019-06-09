package dz.algerietelecom.webservice.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class FactureDetailModel {
	
	private String nd;
	private String numFacture;
	private String montant;
	private String dateDebutFacture;
	private String dateFinFacture;
	
	
	public String getNd() {
		return nd;
	}
	public void setNd(String nd) {
		this.nd = nd;
	}
	public String getNumFacture() {
		return numFacture;
	}
	public void setNumFacture(String numFacture) {
		this.numFacture = numFacture;
	}
	public String getMontant() {
		return montant;
	}
	public void setMontant(String montant) {
		this.montant = montant;
	}
	public String getDateDebutFacture() {
		return dateDebutFacture;
	}
	public void setDateDebutFacture(String dateDebutFacture) {
		this.dateDebutFacture = dateDebutFacture;
	}
	public String getDateFinFacture() {
		return dateFinFacture;
	}
	public void setDateFinFacture(String dateFinFacture) {
		this.dateFinFacture = dateFinFacture;
	}


	
}
