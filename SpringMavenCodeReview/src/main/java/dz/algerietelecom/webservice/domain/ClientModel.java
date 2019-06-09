package dz.algerietelecom.webservice.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ClientModel {

	@Id
	private String ncli;
	private String nd;
	private String nom;
	private String prenom;
	private String adresse;
	private String type;
	private String status;
	private String dette;
	private String nbFact;
	public String getNcli() {
		return ncli;
	}
	public void setNcli(String ncli) {
		this.ncli = ncli;
	}
	public String getNd() {
		return nd;
	}
	public void setNd(String nd) {
		this.nd = nd;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDette() {
		return dette;
	}
	public void setDette(String dette) {
		this.dette = dette;
	}
	public String getNbFact() {
		return nbFact;
	}
	public void setNbFact(String nbFact) {
		this.nbFact = nbFact;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adresse == null) ? 0 : adresse.hashCode());
		result = prime * result + ((dette == null) ? 0 : dette.hashCode());
		result = prime * result + ((nbFact == null) ? 0 : nbFact.hashCode());
		result = prime * result + ((ncli == null) ? 0 : ncli.hashCode());
		result = prime * result + ((nd == null) ? 0 : nd.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientModel other = (ClientModel) obj;
		if (adresse == null) {
			if (other.adresse != null)
				return false;
		} else if (!adresse.equals(other.adresse))
			return false;
		if (dette == null) {
			if (other.dette != null)
				return false;
		} else if (!dette.equals(other.dette))
			return false;
		if (nbFact == null) {
			if (other.nbFact != null)
				return false;
		} else if (!nbFact.equals(other.nbFact))
			return false;
		if (ncli == null) {
			if (other.ncli != null)
				return false;
		} else if (!ncli.equals(other.ncli))
			return false;
		if (nd == null) {
			if (other.nd != null)
				return false;
		} else if (!nd.equals(other.nd))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (prenom == null) {
			if (other.prenom != null)
				return false;
		} else if (!prenom.equals(other.prenom))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ClientModel [ncli=" + ncli + ", nd=" + nd + ", nom=" + nom
				+ ", prenom=" + prenom + ", adresse=" + adresse + ", type="
				+ type + ", status=" + status + ", dette=" + dette
				+ ", nbFact=" + nbFact + "]";
	}	
	



}
