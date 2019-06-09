package dz.algerietelecom.webservice.reponse;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import dz.algerietelecom.webservice.domain.ClientModel;
import dz.algerietelecom.webservice.domain.ProduitModel;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "client")
public class ClientReponse implements Serializable {

private String code;
private String message;
private ClientModel compte;
private List<ProduitModel> produit;

public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public ClientModel getCompte() {
	return compte;
}
public void setCompte(ClientModel compte) {
	this.compte = compte;
}
public List<ProduitModel> getProduit() {
	return produit;
}
public void setProduit(List<ProduitModel> produit) {
	this.produit = produit;
}






}
