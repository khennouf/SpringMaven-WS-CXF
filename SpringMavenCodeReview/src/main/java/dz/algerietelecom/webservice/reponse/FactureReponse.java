package dz.algerietelecom.webservice.reponse;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import dz.algerietelecom.webservice.domain.FactureDetailModel;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "facture")
public class FactureReponse implements Serializable {

private String code;
private String message;
private List <FactureDetailModel> facture;


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

public List<FactureDetailModel> getFacture() {
	return facture;
}

public void setFacture(List<FactureDetailModel> facture) {
	this.facture = facture;
}


}
