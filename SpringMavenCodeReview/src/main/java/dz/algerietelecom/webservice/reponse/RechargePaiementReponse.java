package dz.algerietelecom.webservice.reponse;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jws.WebResult;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.validation.annotation.Validated;
import dz.algerietelecom.webservice.domain.ResultRechargeModel;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "recharge")
public class RechargePaiementReponse implements Serializable {


private ResultRechargeModel recharge;

public ResultRechargeModel getRecharge() {
	return recharge;
}

public void setRecharge(ResultRechargeModel recharge) {
	this.recharge = recharge;
}


}
