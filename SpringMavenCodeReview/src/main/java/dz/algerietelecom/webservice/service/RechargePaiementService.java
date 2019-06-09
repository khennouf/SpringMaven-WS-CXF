package dz.algerietelecom.webservice.service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import com.sun.istack.NotNull;

import dz.algerietelecom.webservice.reponse.ClientReponse;
import dz.algerietelecom.webservice.reponse.FactureReponse;
import dz.algerietelecom.webservice.reponse.RechargePaiementReponse;

@WebService()
public interface RechargePaiementService {

	@WebMethod(operationName = "setRechargePaiement")
	@WebResult(name = "setRechargePaiement")
	public RechargePaiementReponse setRechargePaiement(
			@WebParam(name = "nd") final String nd,
			@WebParam(name = "numTransaction") final String num_transaction,
			@WebParam(name = "dateTransaction") final Date dateTransaction,
			@WebParam(name = "heureTransaction") final String heureTransaction,
			@WebParam(name = "statut") final String statut,
			@WebParam(name = "numTransactionPoste") final String numTransactionPoste,
			@WebParam(name = "dateTraitementPoste") final String dateTraitementPoste,
			@WebParam(name = "heureTraitementPoste") final String heureTraitementPoste,
			@WebParam(name = "codeReponsePoste") final String codeReponsePoste,
			@WebParam(name = "authCodePoste") final String authCodePoste,
			@WebParam(name = "resultat") final String resultat,
			@WebParam(name = "montant") final String montant,
			@WebParam(name = "source") final String source)
			throws RemoteException, ServiceException, SOAPException,
			IOException, ParseException;

	@WebMethod(operationName = "setRechargeVoucher")
	@WebResult(name = "setRechargeVoucher")
	public RechargePaiementReponse setRechargeVoucher(
			@WebParam(name = "nd") final String nd,
			@WebParam(name = "numTransaction") final String num_transaction,
			@WebParam(name = "dateTransaction") final Date dateTransaction,
			@WebParam(name = "heureTransaction") final String heureTransaction,
			@WebParam(name = "voucher") final String voucher)
			throws RemoteException, ServiceException, SOAPException,
			IOException, ParseException;

	@WebMethod(operationName = "setRechargePaiementFTTH")
	@WebResult(name = "setRechargePaiementFTTH")
	public RechargePaiementReponse setRechargePaiementFTTH(
			@WebParam(name = "nd") final String nd,
			@WebParam(name = "numTransaction") final String num_transaction,
			@WebParam(name = "dateTransaction") final Date dateTransaction,
			@WebParam(name = "heureTransaction") final String heureTransaction,
			@WebParam(name = "statut") final String statut,
			@WebParam(name = "numTransactionPoste") final String numTransactionPoste,
			@WebParam(name = "dateTraitementPoste") final String dateTraitementPoste,
			@WebParam(name = "heureTraitementPoste") final String heureTraitementPoste,
			@WebParam(name = "codeReponsePoste") final String codeReponsePoste,
			@WebParam(name = "authCodePoste") final String authCodePoste,
			@WebParam(name = "resultat") final String resultat,
			@WebParam(name = "montant") final String montant,
			@WebParam(name = "source") final String source)
			throws RemoteException, ServiceException, SOAPException,
			IOException, ParseException;

	@WebMethod(operationName = "setRechargeVoucherFTTH")
	@WebResult(name = "setRechargeVoucherFTTH")
	public RechargePaiementReponse setRechargeVoucherFTTH(
			@WebParam(name = "nd") final String nd,
			@WebParam(name = "numTransaction") final String num_transaction,
			@WebParam(name = "dateTransaction") final Date dateTransaction,
			@WebParam(name = "heureTransaction") final String heureTransaction,
			@WebParam(name = "voucher") final String voucher)
			throws RemoteException, ServiceException, SOAPException,
			IOException, ParseException;

	@WebMethod(operationName = "setRechargePaiementLTE")
	@WebResult(name = "setRechargePaiementLTE")
	public RechargePaiementReponse setRechargePaiementLTE(
			@WebParam(name = "msisdn") final String msisdn,
			@WebParam(name = "numTransaction") final String num_transaction,
			@WebParam(name = "dateTransaction") final Date dateTransaction,
			@WebParam(name = "heureTransaction") final String heureTransaction,
			@WebParam(name = "statut") final String statut,
			@WebParam(name = "numTransactionPoste") final String numTransactionPoste,
			@WebParam(name = "dateTraitementPoste") final String dateTraitementPoste,
			@WebParam(name = "heureTraitementPoste") final String heureTraitementPoste,
			@WebParam(name = "codeReponsePoste") final String codeReponsePoste,
			@WebParam(name = "authCodePoste") final String authCodePoste,
			@WebParam(name = "resultat") final String resultat,
			@WebParam(name = "montant") final String montant,
			@WebParam(name = "source") final String source)
			throws RemoteException, ServiceException, SOAPException,
			IOException, ParseException;

	@WebMethod(operationName = "setRechargeVoucherLTE")
	@WebResult(name = "setRechargeVoucherLTE")
	public RechargePaiementReponse setRechargeVoucherLTE(
			@WebParam(name = "msisdn") final String msisdn,
			@WebParam(name = "numTransaction") final String num_transaction,
			@WebParam(name = "dateTransaction") final Date dateTransaction,
			@WebParam(name = "heureTransaction") final String heureTransaction,
			@WebParam(name = "voucher") final String voucher)
			throws RemoteException, ServiceException, SOAPException,
			IOException, ParseException;

	@WebMethod(operationName = "setPaiementInvoice")
	@WebResult(name = "setPaiementInvoice")
	public RechargePaiementReponse setPaiementInvoice(
			@WebParam(name = "ncli") final String ncli,
			@WebParam(name = "nd") final String nd,
			@WebParam(name = "numTransaction") final String num_transaction,
			@WebParam(name = "dateTransaction") final String dateTransaction,
			@WebParam(name = "heureTransaction") final String heureTransaction,
			@WebParam(name = "statut") final String statut,
			@WebParam(name = "numTransactionPoste") final String numTransactionPoste,
			@WebParam(name = "dateTraitementPoste") final String dateTraitementPoste,
			@WebParam(name = "heureTraitementPoste") final String heureTraitementPoste,
			@WebParam(name = "codeReponsePoste") final String codeReponsePoste,
			@WebParam(name = "authCodePoste") final String authCodePoste,
			@WebParam(name = "numeroFacture") final String numeroFacture,
			@WebParam(name = "resultat") final String resultat,
			@WebParam(name = "montant") final Float montant,
			@WebParam(name = "source") final String source)
			throws RemoteException, ServiceException, SOAPException,
			IOException;
	
	@WebMethod(operationName = "getInvoices")
	@WebResult(name = "invoices")
	public FactureReponse invoices(@WebParam(name = "nd") final String nd)
			throws RemoteException, ServiceException, SOAPException,
			IOException;
	
	@WebMethod(operationName = "consultClient")
	@WebResult(name = "client")
	public ClientReponse client(
			@WebParam(name = "nd") @XmlElement(name="nd", required=true) final String nd)
			throws RemoteException, ServiceException, SOAPException,
			IOException;

}
