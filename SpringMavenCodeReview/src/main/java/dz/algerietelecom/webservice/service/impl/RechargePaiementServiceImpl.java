package dz.algerietelecom.webservice.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.inject.Named;
import javax.jws.WebService;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.huawei.bme.cbsinterface.arservices.ArServices;
import com.huawei.bme.cbsinterface.arservices.PaymentRequest;
import com.huawei.bme.cbsinterface.arservices.PaymentRequest.PaymentInfo;
import com.huawei.bme.cbsinterface.arservices.PaymentRequest.PaymentInfo.CashPayment;
import com.huawei.bme.cbsinterface.arservices.PaymentRequest.PaymentInfo.CashPayment.ApplyList;
import com.huawei.bme.cbsinterface.arservices.PaymentRequest.PaymentObj;
import com.huawei.bme.cbsinterface.arservices.PaymentRequestMsg;
import com.huawei.bme.cbsinterface.arservices.PaymentResultMsg;
import com.huawei.bme.cbsinterface.arservices.QueryInvoiceRequest;
import com.huawei.bme.cbsinterface.arservices.QueryInvoiceRequest.AcctAccessCode;
import com.huawei.bme.cbsinterface.arservices.QueryInvoiceRequestMsg;
import com.huawei.bme.cbsinterface.arservices.QueryInvoiceResult.InvoiceInfo;
import com.huawei.bme.cbsinterface.arservices.QueryInvoiceResultMsg;
import com.huawei.bme.cbsinterface.arservices.QuerySumBalanceAndCreditRequest;
import com.huawei.bme.cbsinterface.arservices.QuerySumBalanceAndCreditRequest.QueryObj;
import com.huawei.bme.cbsinterface.arservices.QuerySumBalanceAndCreditRequestMsg;
import com.huawei.bme.cbsinterface.arservices.QuerySumBalanceAndCreditResultMsg;
import com.huawei.bme.cbsinterface.arservices.RechargeRequest;
import com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo;
import com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj;
import com.huawei.bme.cbsinterface.arservices.RechargeRequestMsg;
import com.huawei.bme.cbsinterface.arservices.RechargeResultMsg;
import com.huawei.bme.cbsinterface.cbscommon.OperatorInfo;
import com.huawei.bme.cbsinterface.cbscommon.OwnershipInfo;
import com.huawei.bme.cbsinterface.cbscommon.RequestHeader;
import com.huawei.bme.cbsinterface.cbscommon.SecurityInfo;
import com.huawei.bss.soaif._interface.accountservice.AccountInterfaces;
import com.huawei.bss.soaif._interface.accountservice.QueryBalanceAndFreeUnitReqMsg;
import com.huawei.bss.soaif._interface.accountservice.QueryBalanceAndFreeUnitRspMsg;
import com.huawei.bss.soaif._interface.accountservice.QueryBalanceAndFreeUnitRspMsg.Account;
import com.huawei.bss.soaif._interface.common.AcctBalance;
import com.huawei.bss.soaif._interface.common.Address;
import com.huawei.bss.soaif._interface.common.ObjectAccessInfo;
import com.huawei.bss.soaif._interface.common.OutstandingAmt;
import com.huawei.bss.soaif._interface.common.ReqHeader;
import com.huawei.bss.soaif._interface.common.TimeFormat;
import com.huawei.bss.soaif._interface.customerservice.CustomerInterfaces;
import com.huawei.bss.soaif._interface.customerservice.QueryCustInfoReqMsg;
import com.huawei.bss.soaif._interface.customerservice.QueryCustInfoRspMsg;
import com.huawei.cbs.ar.wsservice.arcommon.BankInfo;
import com.huawei.crm.basetype.ExtParameterInfo;
import com.huawei.crm.basetype.GetSubProductInfo;
import com.huawei.crm.query.QuerySubscriberAllInfoIn;
import com.huawei.crm.query.QuerySubscriberAllInfoReqMsg;
import com.huawei.crm.query.QuerySubscriberAllInfoRspMsg;

import dz.algerietelecom.function.Cryptage;
import dz.algerietelecom.webservice.dao.FactureDao;
import dz.algerietelecom.webservice.dao.RechargeDao;
import dz.algerietelecom.webservice.domain.ClientModel;
import dz.algerietelecom.webservice.domain.FactureDetailModel;
import dz.algerietelecom.webservice.domain.FactureModel;
import dz.algerietelecom.webservice.domain.ProduitModel;
import dz.algerietelecom.webservice.domain.RechargeModel;
import dz.algerietelecom.webservice.domain.RechargeNokModel;
import dz.algerietelecom.webservice.domain.ResultRechargeModel;
import dz.algerietelecom.webservice.reponse.ClientReponse;
import dz.algerietelecom.webservice.reponse.FactureReponse;
import dz.algerietelecom.webservice.reponse.RechargePaiementReponse;
import dz.algerietelecom.webservice.service.RechargePaiementService;

import org.apache.commons.validator.DateValidator;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

@WebService(name = "RechargePaiementService", endpointInterface = "dz.algerietelecom.webservice.service.RechargePaiementService", targetNamespace = "http://service.webservice.algerietelecom.dz/")
public class RechargePaiementServiceImpl implements RechargePaiementService {

	@Autowired
	RechargeDao rechargeDao;

	@Autowired
	FactureDao factureDao;

	@Value("${ws.timeout}")
	private String timeout;

	private static int AES_128 = 128;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	private static final Logger logger = LoggerFactory
			.getLogger(RechargePaiementServiceImpl.class);

	@Override
	@Named
	public RechargePaiementReponse setRechargePaiement(String nd,
			String num_transaction, Date dateTransaction,
			String heureTransaction, String statut, String numTransactionPoste,
			String dateTraitementPoste, String heureTraitementPoste,
			String codeReponsePoste, String authCodePoste, String resultat,
			String montant, String source) throws ServiceException,
			SOAPException, IOException, ParseException {

		ResultRechargeModel result = new ResultRechargeModel();

		/******************* check the parameters ***************************/
		Boolean check = true;
		String var = "";

		if (nd.length() != 8 || !(nd.matches("[0-9]+"))) {
			check = false;

		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!dateFormat.format(dateTransaction).matches(
				"((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;

		}

		if (!heureTransaction
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;

		}

		if (!dateTraitementPoste
				.matches("((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;

		}

		if (!heureTraitementPoste
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;

		}

		if (!(statut.matches("[0-9]+"))) {
			check = false;

		}

		if (!(numTransactionPoste.matches("[0-9a-zA-Z.-]+"))) {
			check = false;

		}

		if (!(num_transaction.matches("[0-9]+"))) {
			check = false;

		}

		if (!(codeReponsePoste.matches("[0-9a-zA-Z]+"))) {
			check = false;

		}

		if (!(authCodePoste.matches("[0-9a-zA-Z]+"))) {
			check = false;

		}

		if (!(montant.toString().matches("[0-9\\.]+"))) {
			check = false;

		}

		if (!(resultat.matches("[0-9a-zA-Z]+"))) {
			check = false;

		}

		if (!(source.matches("[A-Z]+"))) {
			check = false;

		}

		if (check == true) {

			/******************** check transaction *********************/
			Integer checkNumTrans = rechargeDao
					.checkTransaction(num_transaction);

			if (checkNumTrans == 0) {

				ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
						new String[] { "application-config.xml" });

				com.huawei.crm.service.SubscriberInterfaces subscriber = (com.huawei.crm.service.SubscriberInterfaces) context
						.getBean("subscriberWS");

				ArServices cbs = (ArServices) context.getBean("cbsWS");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String reqTime = sdf.format(System.currentTimeMillis());
				reqTime = reqTime + (int) (Math.random() * 1000);

				try {
					/******************* subscriber request ***************************/
					QuerySubscriberAllInfoReqMsg reqSub = new QuerySubscriberAllInfoReqMsg();

					com.huawei.crm.basetype.RequestHeader header2 = new com.huawei.crm.basetype.RequestHeader();
					header2.setVersion("1");
					header2.setTransactionId(reqTime);
					header2.setChannelId("62");
					header2.setTechnicalChannelId("51");
					header2.setTenantId("101");
					header2.setAccessUser("");
					header2.setAccessPwd("");
					header2.setOperatorId("");

					reqSub.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub = new QuerySubscriberAllInfoIn();
					sub.setIncludeOfferFlag("1");
					sub.setServiceNumber("A" + nd);
					reqSub.setQuerySubscriberAllInfoBody(sub);

					// setting the timeout
					Client c = ClientProxy.getClient(subscriber);
					HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
					httpConduit.getClient().setConnectionTimeout(
							Long.parseLong(timeout));
					httpConduit.getClient().setReceiveTimeout(
							Long.parseLong(timeout));

					QuerySubscriberAllInfoRspMsg respSub = new QuerySubscriberAllInfoRspMsg();
					/************* read subscriber response **********************************/
					respSub = subscriber.querySubscriberAllInfo(reqSub);

					if (respSub.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")) {

						/******************* recharge request *****************************/
						RechargeRequestMsg reqRecharge = new RechargeRequestMsg();

						RequestHeader header4 = new RequestHeader();
						header4.setVersion("1");
						header4.setBusinessCode("Recharge");
						header4.setMessageSeq(reqTime);
						OwnershipInfo own1 = new OwnershipInfo();
						own1.setBEID("101");
						own1.setBRID("101");
						header4.setOwnershipInfo(own1);
						SecurityInfo accSec1 = new SecurityInfo();
						accSec1.setLoginSystemCode("");
						accSec1.setPassword("==");
						header4.setAccessSecurity(accSec1);
						OperatorInfo oprInf1 = new OperatorInfo();
						oprInf1.setChannelID("62");
						oprInf1.setOperatorID("3001");
						header4.setOperatorInfo(oprInf1);
						reqRecharge.setRequestHeader(header4);

						RechargeRequest rechargeWS = new RechargeRequest();
						rechargeWS.setRechargeSerialNo(num_transaction);
						rechargeWS.setRechargeType("ADSL");
						rechargeWS.setRechargeChannelID("62");
						RechargeObj rechargeObj = new RechargeObj();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode acctAcc = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode();
						acctAcc.setPrimaryIdentity("A" + nd);
						rechargeObj.setAcctAccessCode(acctAcc);
						rechargeWS.setRechargeObj(rechargeObj);

						RechargeInfo rechargeInfo = new RechargeInfo();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CashPayment cashP = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CashPayment();
						cashP.setPaymentMethod("2001");
						cashP.setAmount(Long.valueOf(montant + "00"));
						BankInfo bank = new BankInfo();
						if (source.equalsIgnoreCase("CIB")) {
							bank.setBankCode("036");
						}
						if (source.equalsIgnoreCase("EDAHABYA")) {
							bank.setBankCode("007");
						}
						cashP.setBankInfo(bank);
						rechargeInfo.getCashPayment().add(cashP);
						rechargeWS.setRechargeInfo(rechargeInfo);

						BigInteger currency = new BigInteger("1044");
						rechargeWS.setCurrencyID(currency);

						reqRecharge.setRechargeRequest(rechargeWS);
						RechargeResultMsg respRecharge = new RechargeResultMsg();

						// setting the timeout
						Client c1 = ClientProxy.getClient(cbs);
						HTTPConduit httpConduit1 = (HTTPConduit) c1
								.getConduit();
						httpConduit1.getClient().setConnectionTimeout(
								Long.parseLong(timeout));
						httpConduit1.getClient().setReceiveTimeout(
								Long.parseLong(timeout));

						/************* read recharge response **********************************/
						respRecharge = cbs.recharge(reqRecharge);
						String Message_CBS = respRecharge.getResultHeader()
								.getResultDesc();
						String Code_CBS = respRecharge.getResultHeader()
								.getResultCode();

						if (Code_CBS.equalsIgnoreCase("0")
								&& Message_CBS
										.equals("Operation successfully.")) {

							/******************************************
							 * Insertion dans la table RECHARGES
							 ***********************************************/

							RechargeModel recharge = new RechargeModel();

							recharge.setACCOUNT_NUM(nd);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL(authCodePoste);
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("1");
							recharge.setMONTANT(montant);
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE(source);
							recharge.setTYPE("ADSL");

							rechargeDao.insertRecharge(recharge);

							result.setCode("00");
							result.setMessage("Success");

						} else {

							/*************** Lire le fichier des etats ********************/
							ClassLoader classLoader = this.getClass()
									.getClassLoader();
							Properties prop = new Properties();
							FileInputStream in = new FileInputStream(
									classLoader.getResource("error.properties")
											.getFile());
							prop.load(in);
							in.close();
							/*************************************************************/
							result.setCode(Code_CBS);
							result.setMessage(prop.getProperty(Code_CBS));

							/*************** Insertion dans RECHARGE_NOK *********************/

							RechargeNokModel recharge = new RechargeNokModel();

							recharge.setACCOUNT_NUM(nd);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL(authCodePoste);
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("0");
							recharge.setMONTANT(montant);
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE(source);
							recharge.setTYPE("ADSL");
							recharge.setCAUSE(prop.getProperty(Code_CBS));

							rechargeDao.insertRechargeNok(recharge);

						}

					} else {

						/*************** Lire le fichier des etats ********************/
						ClassLoader classLoader = this.getClass()
								.getClassLoader();
						Properties prop = new Properties();
						FileInputStream in = new FileInputStream(classLoader
								.getResource("error.properties").getFile());
						prop.load(in);
						in.close();
						/*************************************************************/
						result.setCode(respSub.getResponseHeader().getRetCode());
						result.setMessage(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						/*************** Insertion dans RECHARGE_NOK *********************/

						RechargeNokModel recharge = new RechargeNokModel();

						recharge.setACCOUNT_NUM(nd);
						recharge.setBATCH_SERIAL_NO(num_transaction);
						recharge.setCANAL(authCodePoste);
						recharge.setCOMMENTAIRE(respSub.getResponseHeader()
								.getRetMsg());
						recharge.setDATE_RECH(dateTransaction);
						recharge.setETAT("0");
						recharge.setMONTANT(montant);
						recharge.setNUM_TEL_B("");
						recharge.setNUM_TEL_IP("");
						recharge.setPRODUCT("");
						recharge.setSOURCE(source);
						recharge.setTYPE("ADSL");
						recharge.setCAUSE(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						rechargeDao.insertRechargeNok(recharge);

					}

				} catch (Exception e) {
					logger.error("Error while refill in cbs for account " + nd,
							e);

					/*************** Lire le fichier des etats ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/
					result.setCode("43");
					result.setMessage(prop.getProperty("43")
							+ e.getCause().getMessage());

				}

				context.close();

			} else {

				/*************** Lire le fichier des etats ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/
				result.setCode("68");
				result.setMessage(prop.getProperty("68"));

			}

		} else {

			/*************** Lire le fichier des etats ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/
			result.setCode("67");
			result.setMessage(prop.getProperty("67"));

		}

		RechargePaiementReponse response = new RechargePaiementReponse();
		response.setRecharge(result);
		return response;

	}

	@Override
	@Named
	public RechargePaiementReponse setRechargeVoucher(String nd,
			String num_transaction, Date dateTransaction,
			String heureTransaction, String voucher) throws ServiceException,
			SOAPException, IOException, ParseException {

		ResultRechargeModel result = new ResultRechargeModel();

		/******************* check the parameters ***************************/
		Boolean check = true;
		String var = "";

		if (nd.length() != 8 || !(nd.matches("[0-9]+"))) {
			check = false;

		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!dateFormat.format(dateTransaction).matches(
				"((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;

		}

		if (!heureTransaction
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;

		}

		if (!(num_transaction.matches("[0-9]+"))) {
			check = false;

		}

		if (!(voucher.toString().matches("[a-zA-Z0-9]+"))) {
			check = false;

		}


		if (check == true) {

			/******************** check transaction *********************/
			Integer checkNumTrans = rechargeDao
					.checkTransaction(num_transaction);

			if (checkNumTrans == 0) {

				ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
						new String[] { "application-config.xml" });

				com.huawei.crm.service.SubscriberInterfaces subscriber = (com.huawei.crm.service.SubscriberInterfaces) context
						.getBean("subscriberWS");

				ArServices cbs = (ArServices) context.getBean("cbsWS");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String reqTime = sdf.format(System.currentTimeMillis());
				reqTime = reqTime + (int) (Math.random() * 1000);

				try {
					/******************* subscriber request ***************************/
					QuerySubscriberAllInfoReqMsg reqSub = new QuerySubscriberAllInfoReqMsg();

					com.huawei.crm.basetype.RequestHeader header2 = new com.huawei.crm.basetype.RequestHeader();
					header2.setVersion("1");
					header2.setTransactionId(reqTime);
					header2.setChannelId("62");
					header2.setTechnicalChannelId("51");
					header2.setTenantId("101");
					header2.setAccessUser("");
					header2.setAccessPwd("==");
					header2.setOperatorId("");

					reqSub.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub = new QuerySubscriberAllInfoIn();
					sub.setIncludeOfferFlag("1");
					sub.setServiceNumber("A"+nd);
					reqSub.setQuerySubscriberAllInfoBody(sub);

					// setting the timeout
					Client c = ClientProxy.getClient(subscriber);
					HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
					httpConduit.getClient().setConnectionTimeout(
							Long.parseLong(timeout));
					httpConduit.getClient().setReceiveTimeout(
							Long.parseLong(timeout));

					QuerySubscriberAllInfoRspMsg respSub = new QuerySubscriberAllInfoRspMsg();
					/************* read subscriber response **********************************/
					respSub = subscriber.querySubscriberAllInfo(reqSub);

					if (respSub.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")) {

						/******************* recharge request *****************************/
						RechargeRequestMsg reqRecharge = new RechargeRequestMsg();

						RequestHeader header4 = new RequestHeader();
						header4.setVersion("1");
						header4.setBusinessCode("Recharge");
						header4.setMessageSeq(reqTime);
						OwnershipInfo own1 = new OwnershipInfo();
						own1.setBEID("101");
						own1.setBRID("101");
						header4.setOwnershipInfo(own1);
						SecurityInfo accSec1 = new SecurityInfo();
						accSec1.setLoginSystemCode("");
						accSec1.setPassword("==");
						header4.setAccessSecurity(accSec1);
						OperatorInfo oprInf1 = new OperatorInfo();
						oprInf1.setChannelID("62");
						oprInf1.setOperatorID("3001");
						header4.setOperatorInfo(oprInf1);
						reqRecharge.setRequestHeader(header4);

						RechargeRequest rechargeWS = new RechargeRequest();
						rechargeWS.setRechargeSerialNo(num_transaction);
						rechargeWS.setRechargeType("ADSL");
						rechargeWS.setRechargeChannelID("62");
						RechargeObj rechargeObj = new RechargeObj();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode acctAcc = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode();
						acctAcc.setPrimaryIdentity("A"+nd);
						rechargeObj.setAcctAccessCode(acctAcc);
						rechargeWS.setRechargeObj(rechargeObj);

						RechargeInfo rechargeInfo = new RechargeInfo();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CardPayment cardP = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CardPayment();		
						/***********encrypt voucher**************/
						String key = "";
						String voucherC = Cryptage.encrypt(voucher, key);
									
						cardP.setCardPinNumber(voucherC);
						rechargeInfo.setCardPayment(cardP);				
						rechargeWS.setRechargeInfo(rechargeInfo);

						BigInteger currency = new BigInteger("1044");
						rechargeWS.setCurrencyID(currency);

						reqRecharge.setRechargeRequest(rechargeWS);
						RechargeResultMsg respRecharge = new RechargeResultMsg();

						// setting the timeout
						Client c1 = ClientProxy.getClient(cbs);
						HTTPConduit httpConduit1 = (HTTPConduit) c1
								.getConduit();
						httpConduit1.getClient().setConnectionTimeout(
								Long.parseLong(timeout));
						httpConduit1.getClient().setReceiveTimeout(
								Long.parseLong(timeout));

						/************* read recharge response **********************************/
						respRecharge = cbs.recharge(reqRecharge);
						String Message_CBS = respRecharge.getResultHeader()
								.getResultDesc();
						String Code_CBS = respRecharge.getResultHeader()
								.getResultCode();

						if (Code_CBS.equalsIgnoreCase("0")
								&& Message_CBS
										.equals("Operation successfully.")) {

							/******************************************
							 * Insertion dans la table RECHARGES
							 ***********************************************/

							RechargeModel recharge = new RechargeModel();

							recharge.setACCOUNT_NUM(nd);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL("");
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("1");
							recharge.setMONTANT("");
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE("WEB");
							recharge.setTYPE("ADSL");

							rechargeDao.insertRecharge(recharge);

							result.setCode("00");
							result.setMessage("Success");

						} else {

							/*************** Lire le fichier des etats ********************/
							ClassLoader classLoader = this.getClass()
									.getClassLoader();
							Properties prop = new Properties();
							FileInputStream in = new FileInputStream(
									classLoader.getResource("error.properties")
											.getFile());
							prop.load(in);
							in.close();
							/*************************************************************/
							result.setCode(Code_CBS);
							result.setMessage(prop.getProperty(Code_CBS));

							/*************** Insertion dans RECHARGE_NOK *********************/

							RechargeNokModel recharge = new RechargeNokModel();

							recharge.setACCOUNT_NUM(nd);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL("");
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("0");
							recharge.setMONTANT("");
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE("WEB");
							recharge.setTYPE("ADSL");
							recharge.setCAUSE(prop.getProperty(Code_CBS));

							rechargeDao.insertRechargeNok(recharge);

						}

					} else {

						/*************** Lire le fichier des etats ********************/
						ClassLoader classLoader = this.getClass()
								.getClassLoader();
						Properties prop = new Properties();
						FileInputStream in = new FileInputStream(classLoader
								.getResource("error.properties").getFile());
						prop.load(in);
						in.close();
						/*************************************************************/
						result.setCode(respSub.getResponseHeader().getRetCode());
						result.setMessage(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						/*************** Insertion dans RECHARGE_NOK *********************/

						RechargeNokModel recharge = new RechargeNokModel();

						recharge.setACCOUNT_NUM(nd);
						recharge.setBATCH_SERIAL_NO(num_transaction);
						recharge.setCANAL("");
						recharge.setCOMMENTAIRE(respSub.getResponseHeader()
								.getRetMsg());
						recharge.setDATE_RECH(dateTransaction);
						recharge.setETAT("0");
						recharge.setMONTANT("");
						recharge.setNUM_TEL_B("");
						recharge.setNUM_TEL_IP("");
						recharge.setPRODUCT("");
						recharge.setSOURCE("WEB");
						recharge.setTYPE("ADSL");
						recharge.setCAUSE(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						rechargeDao.insertRechargeNok(recharge);

					}

				} catch (Exception e) {
					logger.error("Error while refill in cbs for account " + nd,
							e);

					/*************** Lire le fichier des etats ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/
					result.setCode("43");
					result.setMessage(prop.getProperty("43")
							+ e.getCause().getMessage());

				}

				context.close();

			} else {

				/*************** Lire le fichier des etats ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/
				result.setCode("68");
				result.setMessage(prop.getProperty("68"));

			}

		} else {

			/*************** Lire le fichier des etats ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/
			result.setCode("67");
			result.setMessage(prop.getProperty("67"));

		}

		RechargePaiementReponse response = new RechargePaiementReponse();
		response.setRecharge(result);
		return response;

	}

	@Override
	@Named
	public RechargePaiementReponse setRechargePaiementFTTH(String nd,
			String num_transaction, Date dateTransaction,
			String heureTransaction, String statut, String numTransactionPoste,
			String dateTraitementPoste, String heureTraitementPoste,
			String codeReponsePoste, String authCodePoste, String resultat,
			String montant, String source) throws ServiceException,
			SOAPException, IOException, ParseException {

		ResultRechargeModel result = new ResultRechargeModel();

		/******************* check the parameters ***************************/
		Boolean check = true;
		String var = "";

		if (nd.length() != 8 || !(nd.matches("[0-9]+"))) {
			check = false;

		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!dateFormat.format(dateTransaction).matches(
				"((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;

		}

		if (!heureTransaction
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;
			var = var + "heureTransaction";
		}

		if (!dateTraitementPoste
				.matches("((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;
			var = var + "heureTransaction";
		}

		if (!heureTraitementPoste
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;
			var = var + "heureTraitementPoste";
		}

		if (!(statut.matches("[0-9]+"))) {
			check = false;
			var = var + "statut";
		}

		if (!(numTransactionPoste.matches("[0-9a-zA-Z.-]+"))) {
			check = false;
			var = var + "numTransactionPoste";
		}

		if (!(num_transaction.matches("[0-9]+"))) {
			check = false;
			var = var + "num_transaction";
		}

		if (!(codeReponsePoste.matches("[0-9a-zA-Z]+"))) {
			check = false;
			var = var + "codeReponsePoste";
		}

		if (!(authCodePoste.matches("[0-9a-zA-Z]+"))) {
			check = false;
			var = var + "authCodePoste";
		}

		if (!(montant.toString().matches("[0-9\\.]+"))) {
			check = false;
			var = var + "montant";
		}

		if (!(resultat.matches("[0-9a-zA-Z]+"))) {
			check = false;
			var = var + "resultat";
		}

		if (!(source.matches("[A-Z]+"))) {
			check = false;
			var = var + "source";
		}

		if (check == true) {

			/******************** check transaction *********************/
			Integer checkNumTrans = rechargeDao
					.checkTransaction(num_transaction);

			if (checkNumTrans == 0) {

				ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
						new String[] { "application-config.xml" });

				com.huawei.crm.service.SubscriberInterfaces subscriber = (com.huawei.crm.service.SubscriberInterfaces) context
						.getBean("subscriberWS");

				ArServices cbs = (ArServices) context.getBean("cbsWS");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String reqTime = sdf.format(System.currentTimeMillis());
				reqTime = reqTime + (int) (Math.random() * 1000);

				try {
					/******************* subscriber request ***************************/
					QuerySubscriberAllInfoReqMsg reqSub = new QuerySubscriberAllInfoReqMsg();

					com.huawei.crm.basetype.RequestHeader header2 = new com.huawei.crm.basetype.RequestHeader();
					header2.setVersion("1");
					header2.setTransactionId(reqTime);
					header2.setChannelId("62");
					header2.setTechnicalChannelId("51");
					header2.setTenantId("101");
					header2.setAccessUser("");
					header2.setAccessPwd("==");
					header2.setOperatorId("");

					reqSub.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub = new QuerySubscriberAllInfoIn();
					sub.setIncludeOfferFlag("1");
					sub.setServiceNumber("F" + nd);
					reqSub.setQuerySubscriberAllInfoBody(sub);

					// setting the timeout
					Client c = ClientProxy.getClient(subscriber);
					HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
					httpConduit.getClient().setConnectionTimeout(
							Long.parseLong(timeout));
					httpConduit.getClient().setReceiveTimeout(
							Long.parseLong(timeout));

					QuerySubscriberAllInfoRspMsg respSub = new QuerySubscriberAllInfoRspMsg();
					/************* read subscriber response **********************************/
					respSub = subscriber.querySubscriberAllInfo(reqSub);

					if (respSub.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")) {

						/******************* recharge request *****************************/
						RechargeRequestMsg reqRecharge = new RechargeRequestMsg();

						RequestHeader header4 = new RequestHeader();
						header4.setVersion("1");
						header4.setBusinessCode("Recharge");
						header4.setMessageSeq(reqTime);
						OwnershipInfo own1 = new OwnershipInfo();
						own1.setBEID("101");
						own1.setBRID("101");
						header4.setOwnershipInfo(own1);
						SecurityInfo accSec1 = new SecurityInfo();
						accSec1.setLoginSystemCode("");
						accSec1.setPassword("==");
						header4.setAccessSecurity(accSec1);
						OperatorInfo oprInf1 = new OperatorInfo();
						oprInf1.setChannelID("62");
						oprInf1.setOperatorID("3001");
						header4.setOperatorInfo(oprInf1);
						reqRecharge.setRequestHeader(header4);

						RechargeRequest rechargeWS = new RechargeRequest();
						rechargeWS.setRechargeSerialNo(num_transaction);
						rechargeWS.setRechargeType("FTTH");
						rechargeWS.setRechargeChannelID("62");
						RechargeObj rechargeObj = new RechargeObj();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode acctAcc = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode();
						acctAcc.setPrimaryIdentity("F" + nd);
						rechargeObj.setAcctAccessCode(acctAcc);
						rechargeWS.setRechargeObj(rechargeObj);

						RechargeInfo rechargeInfo = new RechargeInfo();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CashPayment cashP = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CashPayment();
						cashP.setPaymentMethod("2001");
						cashP.setAmount(Long.valueOf(montant + "00"));
						BankInfo bank = new BankInfo();
						if (source.equalsIgnoreCase("CIB")) {
							bank.setBankCode("036");
						}
						if (source.equalsIgnoreCase("EDAHABYA")) {
							bank.setBankCode("007");
						}
						cashP.setBankInfo(bank);
						rechargeInfo.getCashPayment().add(cashP);
						rechargeWS.setRechargeInfo(rechargeInfo);

						BigInteger currency = new BigInteger("1044");
						rechargeWS.setCurrencyID(currency);

						reqRecharge.setRechargeRequest(rechargeWS);
						RechargeResultMsg respRecharge = new RechargeResultMsg();

						// setting the timeout
						Client c1 = ClientProxy.getClient(cbs);
						HTTPConduit httpConduit1 = (HTTPConduit) c1
								.getConduit();
						httpConduit1.getClient().setConnectionTimeout(
								Long.parseLong(timeout));
						httpConduit1.getClient().setReceiveTimeout(
								Long.parseLong(timeout));

						/************* read recharge response **********************************/
						respRecharge = cbs.recharge(reqRecharge);
						String Message_CBS = respRecharge.getResultHeader()
								.getResultDesc();
						String Code_CBS = respRecharge.getResultHeader()
								.getResultCode();

						if (Code_CBS.equalsIgnoreCase("0")
								&& Message_CBS
										.equals("Operation successfully.")) {

							/******************************************
							 * Insertion dans la table RECHARGES
							 ***********************************************/

							RechargeModel recharge = new RechargeModel();

							recharge.setACCOUNT_NUM(nd);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL(authCodePoste);
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("1");
							recharge.setMONTANT(montant);
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE(source);
							recharge.setTYPE("FTTH");

							rechargeDao.insertRecharge(recharge);

							result.setCode("00");
							result.setMessage("Success");

						} else {

							/*************** Lire le fichier des etats ********************/
							ClassLoader classLoader = this.getClass()
									.getClassLoader();
							Properties prop = new Properties();
							FileInputStream in = new FileInputStream(
									classLoader.getResource("error.properties")
											.getFile());
							prop.load(in);
							in.close();
							/*************************************************************/
							result.setCode(Code_CBS);
							result.setMessage(prop.getProperty(Code_CBS));

							/*************** Insertion dans RECHARGE_NOK *********************/

							RechargeNokModel recharge = new RechargeNokModel();

							recharge.setACCOUNT_NUM(nd);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL(authCodePoste);
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("0");
							recharge.setMONTANT(montant);
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE(source);
							recharge.setTYPE("FTTH");
							recharge.setCAUSE(prop.getProperty(Code_CBS));

							rechargeDao.insertRechargeNok(recharge);

						}

					} else {

						/*************** Lire le fichier des etats ********************/
						ClassLoader classLoader = this.getClass()
								.getClassLoader();
						Properties prop = new Properties();
						FileInputStream in = new FileInputStream(classLoader
								.getResource("error.properties").getFile());
						prop.load(in);
						in.close();
						/*************************************************************/
						result.setCode(respSub.getResponseHeader().getRetCode());
						result.setMessage(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						/*************** Insertion dans RECHARGE_NOK *********************/

						RechargeNokModel recharge = new RechargeNokModel();

						recharge.setACCOUNT_NUM(nd);
						recharge.setBATCH_SERIAL_NO(num_transaction);
						recharge.setCANAL(authCodePoste);
						recharge.setCOMMENTAIRE(respSub.getResponseHeader()
								.getRetMsg());
						recharge.setDATE_RECH(dateTransaction);
						recharge.setETAT("0");
						recharge.setMONTANT(montant);
						recharge.setNUM_TEL_B("");
						recharge.setNUM_TEL_IP("");
						recharge.setPRODUCT("");
						recharge.setSOURCE(source);
						recharge.setTYPE("FTTH");
						recharge.setCAUSE(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						rechargeDao.insertRechargeNok(recharge);

					}

				} catch (Exception e) {
					logger.error("Error while refill in cbs for account " + nd,
							e);

					/*************** Lire le fichier des etats ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/
					result.setCode("43");
					result.setMessage(prop.getProperty("43")
							+ e.getCause().getMessage());

				}

				context.close();

			} else {

				/*************** Lire le fichier des etats ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/
				result.setCode("68");
				result.setMessage(prop.getProperty("68"));

			}

		} else {

			/*************** Lire le fichier des etats ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/
			result.setCode("67");
			result.setMessage(prop.getProperty("67"));

		}

		RechargePaiementReponse response = new RechargePaiementReponse();
		response.setRecharge(result);
		return response;

	}

	@Override
	@Named
	public RechargePaiementReponse setRechargeVoucherFTTH(String nd,
			String num_transaction, Date dateTransaction,
			String heureTransaction, String voucher) throws ServiceException,
			SOAPException, IOException, ParseException {

		ResultRechargeModel result = new ResultRechargeModel();

		/******************* check the parameters ***************************/
		Boolean check = true;
		String var = "";

		if (nd.length() != 8 || !(nd.matches("[0-9]+"))) {
			check = false;

		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!dateFormat.format(dateTransaction).matches(
				"((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;

		}

		if (!heureTransaction
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;

		}

		if (!(num_transaction.matches("[0-9]+"))) {
			check = false;

		}

		if (!(voucher.toString().matches("[a-zA-Z0-9]+"))) {
			check = false;

		}


		if (check == true) {

			/******************** check transaction *********************/
			Integer checkNumTrans = rechargeDao
					.checkTransaction(num_transaction);

			if (checkNumTrans == 0) {

				ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
						new String[] { "application-config.xml" });

				com.huawei.crm.service.SubscriberInterfaces subscriber = (com.huawei.crm.service.SubscriberInterfaces) context
						.getBean("subscriberWS");

				ArServices cbs = (ArServices) context.getBean("cbsWS");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String reqTime = sdf.format(System.currentTimeMillis());
				reqTime = reqTime + (int) (Math.random() * 1000);

				try {
					/******************* subscriber request ***************************/
					QuerySubscriberAllInfoReqMsg reqSub = new QuerySubscriberAllInfoReqMsg();

					com.huawei.crm.basetype.RequestHeader header2 = new com.huawei.crm.basetype.RequestHeader();
					header2.setVersion("1");
					header2.setTransactionId(reqTime);
					header2.setChannelId("62");
					header2.setTechnicalChannelId("51");
					header2.setTenantId("101");
					header2.setAccessUser("");
					header2.setAccessPwd("==");
					header2.setOperatorId("");

					reqSub.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub = new QuerySubscriberAllInfoIn();
					sub.setIncludeOfferFlag("1");
					sub.setServiceNumber("F"+nd);
					reqSub.setQuerySubscriberAllInfoBody(sub);

					// setting the timeout
					Client c = ClientProxy.getClient(subscriber);
					HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
					httpConduit.getClient().setConnectionTimeout(
							Long.parseLong(timeout));
					httpConduit.getClient().setReceiveTimeout(
							Long.parseLong(timeout));

					QuerySubscriberAllInfoRspMsg respSub = new QuerySubscriberAllInfoRspMsg();
					/************* read subscriber response **********************************/
					respSub = subscriber.querySubscriberAllInfo(reqSub);

					if (respSub.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")) {

						/******************* recharge request *****************************/
						RechargeRequestMsg reqRecharge = new RechargeRequestMsg();

						RequestHeader header4 = new RequestHeader();
						header4.setVersion("1");
						header4.setBusinessCode("Recharge");
						header4.setMessageSeq(reqTime);
						OwnershipInfo own1 = new OwnershipInfo();
						own1.setBEID("101");
						own1.setBRID("101");
						header4.setOwnershipInfo(own1);
						SecurityInfo accSec1 = new SecurityInfo();
						accSec1.setLoginSystemCode("");
						accSec1.setPassword("==");
						header4.setAccessSecurity(accSec1);
						OperatorInfo oprInf1 = new OperatorInfo();
						oprInf1.setChannelID("62");
						oprInf1.setOperatorID("3001");
						header4.setOperatorInfo(oprInf1);
						reqRecharge.setRequestHeader(header4);

						RechargeRequest rechargeWS = new RechargeRequest();
						rechargeWS.setRechargeSerialNo(num_transaction);
						rechargeWS.setRechargeType("FTTH");
						rechargeWS.setRechargeChannelID("62");
						RechargeObj rechargeObj = new RechargeObj();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode acctAcc = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode();
						acctAcc.setPrimaryIdentity("F"+nd);
						rechargeObj.setAcctAccessCode(acctAcc);
						rechargeWS.setRechargeObj(rechargeObj);

						RechargeInfo rechargeInfo = new RechargeInfo();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CardPayment cardP = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CardPayment();		
						/***********encrypt voucher**************/
						String key = "";
						String voucherC = Cryptage.encrypt(voucher, key);
									
						cardP.setCardPinNumber(voucherC);
						rechargeInfo.setCardPayment(cardP);				
						rechargeWS.setRechargeInfo(rechargeInfo);

						BigInteger currency = new BigInteger("1044");
						rechargeWS.setCurrencyID(currency);

						reqRecharge.setRechargeRequest(rechargeWS);
						RechargeResultMsg respRecharge = new RechargeResultMsg();

						// setting the timeout
						Client c1 = ClientProxy.getClient(cbs);
						HTTPConduit httpConduit1 = (HTTPConduit) c1
								.getConduit();
						httpConduit1.getClient().setConnectionTimeout(
								Long.parseLong(timeout));
						httpConduit1.getClient().setReceiveTimeout(
								Long.parseLong(timeout));

						/************* read recharge response **********************************/
						respRecharge = cbs.recharge(reqRecharge);
						String Message_CBS = respRecharge.getResultHeader()
								.getResultDesc();
						String Code_CBS = respRecharge.getResultHeader()
								.getResultCode();

						if (Code_CBS.equalsIgnoreCase("0")
								&& Message_CBS
										.equals("Operation successfully.")) {

							/******************************************
							 * Insertion dans la table RECHARGES
							 ***********************************************/

							RechargeModel recharge = new RechargeModel();

							recharge.setACCOUNT_NUM(nd);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL("");
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("1");
							recharge.setMONTANT("");
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE("WEB");
							recharge.setTYPE("FTTH");

							rechargeDao.insertRecharge(recharge);

							result.setCode("00");
							result.setMessage("Success");

						} else {

							/*************** Lire le fichier des etats ********************/
							ClassLoader classLoader = this.getClass()
									.getClassLoader();
							Properties prop = new Properties();
							FileInputStream in = new FileInputStream(
									classLoader.getResource("error.properties")
											.getFile());
							prop.load(in);
							in.close();
							/*************************************************************/
							result.setCode(Code_CBS);
							result.setMessage(prop.getProperty(Code_CBS));

							/*************** Insertion dans RECHARGE_NOK *********************/

							RechargeNokModel recharge = new RechargeNokModel();

							recharge.setACCOUNT_NUM(nd);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL("");
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("0");
							recharge.setMONTANT("");
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE("WEB");
							recharge.setTYPE("FTTH");
							recharge.setCAUSE(prop.getProperty(Code_CBS));

							rechargeDao.insertRechargeNok(recharge);

						}

					} else {

						/*************** Lire le fichier des etats ********************/
						ClassLoader classLoader = this.getClass()
								.getClassLoader();
						Properties prop = new Properties();
						FileInputStream in = new FileInputStream(classLoader
								.getResource("error.properties").getFile());
						prop.load(in);
						in.close();
						/*************************************************************/
						result.setCode(respSub.getResponseHeader().getRetCode());
						result.setMessage(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						/*************** Insertion dans RECHARGE_NOK *********************/

						RechargeNokModel recharge = new RechargeNokModel();

						recharge.setACCOUNT_NUM(nd);
						recharge.setBATCH_SERIAL_NO(num_transaction);
						recharge.setCANAL("");
						recharge.setCOMMENTAIRE(respSub.getResponseHeader()
								.getRetMsg());
						recharge.setDATE_RECH(dateTransaction);
						recharge.setETAT("0");
						recharge.setMONTANT("");
						recharge.setNUM_TEL_B("");
						recharge.setNUM_TEL_IP("");
						recharge.setPRODUCT("");
						recharge.setSOURCE("WEB");
						recharge.setTYPE("FTTH");
						recharge.setCAUSE(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						rechargeDao.insertRechargeNok(recharge);

					}

				} catch (Exception e) {
					logger.error("Error while refill in cbs for account " + nd,
							e);

					/*************** Lire le fichier des etats ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/
					result.setCode("43");
					result.setMessage(prop.getProperty("43")
							+ e.getCause().getMessage());

				}

				context.close();

			} else {

				/*************** Lire le fichier des etats ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/
				result.setCode("68");
				result.setMessage(prop.getProperty("68"));

			}

		} else {

			/*************** Lire le fichier des etats ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/
			result.setCode("67");
			result.setMessage(prop.getProperty("67"));

		}

		RechargePaiementReponse response = new RechargePaiementReponse();
		response.setRecharge(result);
		return response;

	}

	@Override
	@Named
	public RechargePaiementReponse setRechargePaiementLTE(String msisdn,
			String num_transaction, Date dateTransaction,
			String heureTransaction, String statut, String numTransactionPoste,
			String dateTraitementPoste, String heureTraitementPoste,
			String codeReponsePoste, String authCodePoste, String resultat,
			String montant, String source) throws ServiceException,
			SOAPException, IOException, ParseException {

		ResultRechargeModel result = new ResultRechargeModel();

		/******************* check the parameters ***************************/
		Boolean check = true;

		if (msisdn.length() != 9 || !(msisdn.matches("[0-9]+"))) {
			check = false;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!dateFormat.format(dateTransaction).matches(
				"((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;
		}

		if (!heureTransaction
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;
		}

		if (!dateTraitementPoste
				.matches("((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;
		}

		if (!heureTraitementPoste
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;
		}

		if (!(statut.matches("[0-9]+"))) {
			check = false;
		}

		if (!(numTransactionPoste.matches("[0-9a-zA-Z.-]+"))) {
			check = false;
		}

		if (!(num_transaction.matches("[0-9]+"))) {
			check = false;
		}

		if (!(codeReponsePoste.matches("[0-9a-zA-Z]+"))) {
			check = false;
		}

		if (!(authCodePoste.matches("[0-9a-zA-Z]+"))) {
			check = false;
		}

		if (!(montant.toString().matches("[0-9\\.]+"))) {
			check = false;
		}

		if (!(resultat.matches("[0-9a-zA-Z]+"))) {
			check = false;
		}

		if (!(source.matches("[A-Z]+"))) {
			check = false;
		}

		if (check == true) {

			/******************** check transaction *********************/
			Integer checkNumTrans = rechargeDao
					.checkTransaction(num_transaction);

			if (checkNumTrans == 0) {

				ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
						new String[] { "application-config.xml" });

				com.huawei.crm.service.SubscriberInterfaces subscriber = (com.huawei.crm.service.SubscriberInterfaces) context
						.getBean("subscriberWS");

				ArServices cbs = (ArServices) context.getBean("cbsWS");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String reqTime = sdf.format(System.currentTimeMillis());
				reqTime = reqTime + (int) (Math.random() * 1000);

				try {
					/******************* subscriber request ***************************/
					QuerySubscriberAllInfoReqMsg reqSub = new QuerySubscriberAllInfoReqMsg();

					com.huawei.crm.basetype.RequestHeader header2 = new com.huawei.crm.basetype.RequestHeader();
					header2.setVersion("1");
					header2.setTransactionId(reqTime);
					header2.setChannelId("62");
					header2.setTechnicalChannelId("51");
					header2.setTenantId("101");
					header2.setAccessUser("");
					header2.setAccessPwd("==");
					header2.setOperatorId("");

					reqSub.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub = new QuerySubscriberAllInfoIn();
					sub.setIncludeOfferFlag("1");
					sub.setServiceNumber(msisdn);
					reqSub.setQuerySubscriberAllInfoBody(sub);

					QuerySubscriberAllInfoRspMsg respSub = new QuerySubscriberAllInfoRspMsg();
					/************* read subscriber response **********************************/
					respSub = subscriber.querySubscriberAllInfo(reqSub);

					if (respSub.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")
							&& respSub.getQuerySubscriberAllInfoBody()
									.getGetSubscriberData().getServiceNumber()
									.equalsIgnoreCase(msisdn)) {

						/******************* recharge request *****************************/
						RechargeRequestMsg reqRecharge = new RechargeRequestMsg();

						RequestHeader header4 = new RequestHeader();
						header4.setVersion("1");
						header4.setBusinessCode("Recharge");
						header4.setMessageSeq(reqTime);
						OwnershipInfo own1 = new OwnershipInfo();
						own1.setBEID("101");
						own1.setBRID("101");
						header4.setOwnershipInfo(own1);
						SecurityInfo accSec1 = new SecurityInfo();
						accSec1.setLoginSystemCode("");
						accSec1.setPassword("==");
						header4.setAccessSecurity(accSec1);
						OperatorInfo oprInf1 = new OperatorInfo();
						oprInf1.setChannelID("62");
						oprInf1.setOperatorID("3001");
						header4.setOperatorInfo(oprInf1);
						reqRecharge.setRequestHeader(header4);

						RechargeRequest rechargeWS = new RechargeRequest();
						rechargeWS.setRechargeSerialNo(num_transaction);
						rechargeWS.setRechargeType("4GLTE");
						rechargeWS.setRechargeChannelID("62");
						RechargeObj rechargeObj = new RechargeObj();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode acctAcc = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode();
						acctAcc.setPrimaryIdentity(msisdn);
						rechargeObj.setAcctAccessCode(acctAcc);
						rechargeWS.setRechargeObj(rechargeObj);

						RechargeInfo rechargeInfo = new RechargeInfo();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CashPayment cashP = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CashPayment();
						cashP.setAmount(Long.valueOf(montant + "00"));
						BankInfo bank = new BankInfo();
						if (source.equalsIgnoreCase("CIB")) {
							bank.setBankCode("036");
						}
						if (source.equalsIgnoreCase("EDAHABYA")) {
							bank.setBankCode("007");
						}
						cashP.setBankInfo(bank);
						rechargeInfo.getCashPayment().add(cashP);
						rechargeWS.setRechargeInfo(rechargeInfo);

						BigInteger currency = new BigInteger("1044");
						rechargeWS.setCurrencyID(currency);

						reqRecharge.setRechargeRequest(rechargeWS);
						RechargeResultMsg respRecharge = new RechargeResultMsg();

						// setting the timeout
						Client c = ClientProxy.getClient(cbs);
						HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
						httpConduit.getClient().setConnectionTimeout(
								Long.parseLong(timeout));
						httpConduit.getClient().setReceiveTimeout(
								Long.parseLong(timeout));

						/************* read recharge response **********************************/
						respRecharge = cbs.recharge(reqRecharge);
						String Message_CBS = respRecharge.getResultHeader()
								.getResultDesc();
						String Code_CBS = respRecharge.getResultHeader()
								.getResultCode();

						if (Code_CBS.equalsIgnoreCase("0")
								&& Message_CBS
										.equals("Operation successfully.")) {

							/******************************************
							 * Insertion dans la table RECHARGES
							 ***********************************************/

							RechargeModel recharge = new RechargeModel();

							recharge.setACCOUNT_NUM(msisdn);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL(authCodePoste);
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("1");
							recharge.setMONTANT(montant);
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE(source);
							recharge.setTYPE("4G");

							rechargeDao.insertRecharge(recharge);

							result.setCode("00");
							result.setMessage("Success");

						} else {

							/*************** Lire le fichier des etats ********************/
							ClassLoader classLoader = this.getClass()
									.getClassLoader();
							Properties prop = new Properties();
							FileInputStream in = new FileInputStream(
									classLoader.getResource("error.properties")
											.getFile());
							prop.load(in);
							in.close();
							/*************************************************************/
							result.setCode(Code_CBS);
							result.setMessage(prop.getProperty(Code_CBS));

							/*************** Insertion dans RECHARGE_NOK *********************/

							RechargeNokModel recharge = new RechargeNokModel();

							recharge.setACCOUNT_NUM(msisdn);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL(authCodePoste);
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("0");
							recharge.setMONTANT(montant);
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE("EDAHABYA");
							recharge.setTYPE("4G");
							recharge.setCAUSE(prop.getProperty(Code_CBS));

							rechargeDao.insertRechargeNok(recharge);

						}

					} else {

						/*************** Lire le fichier des etats ********************/
						ClassLoader classLoader = this.getClass()
								.getClassLoader();
						Properties prop = new Properties();
						FileInputStream in = new FileInputStream(classLoader
								.getResource("error.properties").getFile());
						prop.load(in);
						in.close();
						/*************************************************************/
						result.setCode(respSub.getResponseHeader().getRetCode());
						result.setMessage(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						/*************** Insertion dans RECHARGE_NOK *********************/

						RechargeNokModel recharge = new RechargeNokModel();

						recharge.setACCOUNT_NUM(msisdn);
						recharge.setBATCH_SERIAL_NO(num_transaction);
						recharge.setCANAL(authCodePoste);
						recharge.setCOMMENTAIRE(respSub.getResponseHeader()
								.getRetMsg());
						recharge.setDATE_RECH(dateTransaction);
						recharge.setETAT("0");
						recharge.setMONTANT(montant);
						recharge.setNUM_TEL_B("");
						recharge.setNUM_TEL_IP("");
						recharge.setPRODUCT("");
						recharge.setSOURCE(source);
						recharge.setTYPE("4G");
						recharge.setCAUSE(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						rechargeDao.insertRechargeNok(recharge);

					}

				} catch (Exception e) {
					logger.error("Error while refill in cbs for account "
							+ msisdn, e);

					/*************** Lire le fichier des etats ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/
					result.setCode("43");
					result.setMessage(prop.getProperty("43")
							+ e.getCause().getMessage());

				}
				context.close();

			} else {
				/*************** Lire le fichier des erreurs ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/

				result.setCode("68");
				result.setMessage(prop.getProperty("68"));
			}

		} else {

			/*************** Lire le fichier des erreurs ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/

			result.setCode("67");
			result.setMessage(prop.getProperty("67"));
		}

		RechargePaiementReponse response = new RechargePaiementReponse();
		response.setRecharge(result);
		return response;

	}

	@Override
	@Named
	public RechargePaiementReponse setRechargeVoucherLTE(String msisdn,
			String num_transaction, Date dateTransaction,
			String heureTransaction, String voucher) throws ServiceException,
			SOAPException, IOException, ParseException {

		ResultRechargeModel result = new ResultRechargeModel();

		/******************* check the parameters ***************************/
		Boolean check = true;
		String var = "";

		if (msisdn.length() != 9 || !(msisdn.matches("[0-9]+"))) {
			check = false;

		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!dateFormat.format(dateTransaction).matches(
				"((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;

		}

		if (!heureTransaction
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;

		}

		if (!(num_transaction.matches("[0-9]+"))) {
			check = false;

		}

		if (!(voucher.toString().matches("[a-zA-Z0-9]+"))) {
			check = false;

		}


		if (check == true) {

			/******************** check transaction *********************/
			Integer checkNumTrans = rechargeDao
					.checkTransaction(num_transaction);

			if (checkNumTrans == 0) {

				ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
						new String[] { "application-config.xml" });

				com.huawei.crm.service.SubscriberInterfaces subscriber = (com.huawei.crm.service.SubscriberInterfaces) context
						.getBean("subscriberWS");

				ArServices cbs = (ArServices) context.getBean("cbsWS");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String reqTime = sdf.format(System.currentTimeMillis());
				reqTime = reqTime + (int) (Math.random() * 1000);

				try {
					/******************* subscriber request ***************************/
					QuerySubscriberAllInfoReqMsg reqSub = new QuerySubscriberAllInfoReqMsg();

					com.huawei.crm.basetype.RequestHeader header2 = new com.huawei.crm.basetype.RequestHeader();
					header2.setVersion("1");
					header2.setTransactionId(reqTime);
					header2.setChannelId("62");
					header2.setTechnicalChannelId("51");
					header2.setTenantId("101");
					header2.setAccessUser("");
					header2.setAccessPwd("==");
					header2.setOperatorId("");

					reqSub.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub = new QuerySubscriberAllInfoIn();
					sub.setIncludeOfferFlag("1");
					sub.setServiceNumber(msisdn);
					reqSub.setQuerySubscriberAllInfoBody(sub);

					// setting the timeout
					Client c = ClientProxy.getClient(subscriber);
					HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
					httpConduit.getClient().setConnectionTimeout(
							Long.parseLong(timeout));
					httpConduit.getClient().setReceiveTimeout(
							Long.parseLong(timeout));

					QuerySubscriberAllInfoRspMsg respSub = new QuerySubscriberAllInfoRspMsg();
					/************* read subscriber response **********************************/
					respSub = subscriber.querySubscriberAllInfo(reqSub);

					if (respSub.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")) {

						/******************* recharge request *****************************/
						RechargeRequestMsg reqRecharge = new RechargeRequestMsg();

						RequestHeader header4 = new RequestHeader();
						header4.setVersion("1");
						header4.setBusinessCode("Recharge");
						header4.setMessageSeq(reqTime);
						OwnershipInfo own1 = new OwnershipInfo();
						own1.setBEID("101");
						own1.setBRID("101");
						header4.setOwnershipInfo(own1);
						SecurityInfo accSec1 = new SecurityInfo();
						accSec1.setLoginSystemCode("");
						accSec1.setPassword("==");
						header4.setAccessSecurity(accSec1);
						OperatorInfo oprInf1 = new OperatorInfo();
						oprInf1.setChannelID("62");
						oprInf1.setOperatorID("3001");
						header4.setOperatorInfo(oprInf1);
						reqRecharge.setRequestHeader(header4);

						RechargeRequest rechargeWS = new RechargeRequest();
						rechargeWS.setRechargeSerialNo(num_transaction);
						rechargeWS.setRechargeType("4GLTE");
						rechargeWS.setRechargeChannelID("62");
						RechargeObj rechargeObj = new RechargeObj();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode acctAcc = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeObj.AcctAccessCode();
						acctAcc.setPrimaryIdentity(msisdn);
						rechargeObj.setAcctAccessCode(acctAcc);
						rechargeWS.setRechargeObj(rechargeObj);

						RechargeInfo rechargeInfo = new RechargeInfo();
						com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CardPayment cardP = new com.huawei.bme.cbsinterface.arservices.RechargeRequest.RechargeInfo.CardPayment();		
						/***********encrypt voucher**************/
						String key = "";
						String voucherC = Cryptage.encrypt(voucher, key);
									
						cardP.setCardPinNumber(voucherC);
						rechargeInfo.setCardPayment(cardP);				
						rechargeWS.setRechargeInfo(rechargeInfo);

						BigInteger currency = new BigInteger("1044");
						rechargeWS.setCurrencyID(currency);

						reqRecharge.setRechargeRequest(rechargeWS);
						RechargeResultMsg respRecharge = new RechargeResultMsg();

						// setting the timeout
						Client c1 = ClientProxy.getClient(cbs);
						HTTPConduit httpConduit1 = (HTTPConduit) c1
								.getConduit();
						httpConduit1.getClient().setConnectionTimeout(
								Long.parseLong(timeout));
						httpConduit1.getClient().setReceiveTimeout(
								Long.parseLong(timeout));

						/************* read recharge response **********************************/
						respRecharge = cbs.recharge(reqRecharge);
						String Message_CBS = respRecharge.getResultHeader()
								.getResultDesc();
						String Code_CBS = respRecharge.getResultHeader()
								.getResultCode();

						if (Code_CBS.equalsIgnoreCase("0")
								&& Message_CBS
										.equals("Operation successfully.")) {

							/******************************************
							 * Insertion dans la table RECHARGES
							 ***********************************************/

							RechargeModel recharge = new RechargeModel();

							recharge.setACCOUNT_NUM(msisdn);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL("");
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("1");
							recharge.setMONTANT("");
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE("WEB");
							recharge.setTYPE("4G");

							rechargeDao.insertRecharge(recharge);

							result.setCode("00");
							result.setMessage("Success");

						} else {

							/*************** Lire le fichier des etats ********************/
							ClassLoader classLoader = this.getClass()
									.getClassLoader();
							Properties prop = new Properties();
							FileInputStream in = new FileInputStream(
									classLoader.getResource("error.properties")
											.getFile());
							prop.load(in);
							in.close();
							/*************************************************************/
							result.setCode(Code_CBS);
							result.setMessage(prop.getProperty(Code_CBS));

							/*************** Insertion dans RECHARGE_NOK *********************/

							RechargeNokModel recharge = new RechargeNokModel();

							recharge.setACCOUNT_NUM(msisdn);
							recharge.setBATCH_SERIAL_NO(num_transaction);
							recharge.setCANAL("");
							recharge.setCOMMENTAIRE(Message_CBS);
							recharge.setDATE_RECH(dateTransaction);
							recharge.setETAT("0");
							recharge.setMONTANT("");
							recharge.setNUM_TEL_B("");
							recharge.setNUM_TEL_IP("");
							recharge.setPRODUCT("");
							recharge.setSOURCE("WEB");
							recharge.setTYPE("4G");
							recharge.setCAUSE(prop.getProperty(Code_CBS));

							rechargeDao.insertRechargeNok(recharge);

						}

					} else {

						/*************** Lire le fichier des etats ********************/
						ClassLoader classLoader = this.getClass()
								.getClassLoader();
						Properties prop = new Properties();
						FileInputStream in = new FileInputStream(classLoader
								.getResource("error.properties").getFile());
						prop.load(in);
						in.close();
						/*************************************************************/
						result.setCode(respSub.getResponseHeader().getRetCode());
						result.setMessage(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						/*************** Insertion dans RECHARGE_NOK *********************/

						RechargeNokModel recharge = new RechargeNokModel();

						recharge.setACCOUNT_NUM(msisdn);
						recharge.setBATCH_SERIAL_NO(num_transaction);
						recharge.setCANAL("");
						recharge.setCOMMENTAIRE(respSub.getResponseHeader()
								.getRetMsg());
						recharge.setDATE_RECH(dateTransaction);
						recharge.setETAT("0");
						recharge.setMONTANT("");
						recharge.setNUM_TEL_B("");
						recharge.setNUM_TEL_IP("");
						recharge.setPRODUCT("");
						recharge.setSOURCE("WEB");
						recharge.setTYPE("4G");
						recharge.setCAUSE(prop.getProperty(respSub
								.getResponseHeader().getRetCode()));

						rechargeDao.insertRechargeNok(recharge);

					}

				} catch (Exception e) {
					logger.error("Error while refill in cbs for account " + msisdn,
							e);

					/*************** Lire le fichier des etats ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/
					result.setCode("43");
					result.setMessage(prop.getProperty("43")
							+ e.getCause().getMessage());

				}

				context.close();

			} else {

				/*************** Lire le fichier des etats ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/
				result.setCode("68");
				result.setMessage(prop.getProperty("68"));

			}

		} else {

			/*************** Lire le fichier des etats ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/
			result.setCode("67");
			result.setMessage(prop.getProperty("67"));

		}

		RechargePaiementReponse response = new RechargePaiementReponse();
		response.setRecharge(result);
		return response;

	}

	@Override
	@Named
	public RechargePaiementReponse setPaiementInvoice(String ncli, String nd,
			String num_transaction, String dateTransaction,
			String heureTransaction, String statut, String numTransactionPoste,
			String dateTraitementPoste, String heureTraitementPoste,
			String codeReponsePoste, String authCodePoste,
			String numeroFacture, String resultat, Float montant, String source)
			throws IOException {

		ResultRechargeModel result = new ResultRechargeModel();

		/******************* check the parameters ***************************/

		Boolean check = true;

		int year = LocalDateTime.now().getYear();
		int month = LocalDateTime.now().getMonthOfYear();
		int day = LocalDateTime.now().getDayOfMonth();

		int hour = LocalDateTime.now().getHourOfDay();
		int min = LocalDateTime.now().getMinuteOfHour();

		String var = "";

		if (!(ncli.matches("[0-9]+"))) {
			check = false;

		}

		if (nd.length() != 8 || !(nd.matches("[0-9]+"))) {
			check = false;

		}

		if (!dateTransaction
				.matches("((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;

		}

		if (!heureTransaction
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;

		}

		if (!dateTraitementPoste
				.matches("((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])")) {
			check = false;

		}

		if (!heureTraitementPoste
				.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
			check = false;

		}

		if (!(statut.matches("[0-9]+"))) {
			check = false;

		}

		if (!(numTransactionPoste.matches("[0-9a-zA-Z.-]+"))) {
			check = false;

		}

		if (!(num_transaction.matches("[0-9]+"))) {
			check = false;

		}
		if (!(codeReponsePoste.matches("[0-9a-zA-Z]+"))) {
			check = false;

		}

		if (!(authCodePoste.matches("[0-9a-zA-Z]+"))) {
			check = false;

		}

		if (!(numeroFacture.matches("[0-9a-zA-Z.-]+"))) {
			check = false;

		}

		if (!(montant.toString().matches("[0-9\\.]+"))) {
			check = false;

		}

		if (!(resultat.matches("[0-9a-zA-Z]+"))) {
			check = false;

		}

		if (!(source.matches("[A-Z]+"))) {
			check = false;

		}

		if (check == true) {

			Integer checkNumTrans = factureDao
					.checkNumTransaction(num_transaction);

			if (checkNumTrans == 0) {

				ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
						new String[] { "application-config.xml" });

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String reqTime = sdf.format(System.currentTimeMillis());
				reqTime = reqTime + (int) (Math.random() * 1000);

				ArServices cbs = (ArServices) context.getBean("cbsWS");
				/******************* payment invoice request *****************************/
				PaymentRequestMsg reqPayment = new PaymentRequestMsg();

				RequestHeader header4 = new RequestHeader();
				header4.setVersion("1");
				header4.setBusinessCode("Payment");
				header4.setMessageSeq(reqTime);
				OwnershipInfo own1 = new OwnershipInfo();
				own1.setBEID("101");
				own1.setBRID("101");
				header4.setOwnershipInfo(own1);
				SecurityInfo accSec1 = new SecurityInfo();
				accSec1.setLoginSystemCode("");
				accSec1.setPassword("==");
				header4.setAccessSecurity(accSec1);
				OperatorInfo oprInf1 = new OperatorInfo();
				oprInf1.setChannelID("62");
				oprInf1.setOperatorID("3001");
				header4.setOperatorInfo(oprInf1);
				reqPayment.setRequestHeader(header4);

				PaymentRequest paymentR = new PaymentRequest();
				paymentR.setPaymentSerialNo(num_transaction);
				paymentR.setPaymentChannelID("62");
				paymentR.setOpType("1");
				PaymentObj paymObj = new PaymentObj();
				com.huawei.bme.cbsinterface.arservices.PaymentRequest.PaymentObj.AcctAccessCode acctAcc = new com.huawei.bme.cbsinterface.arservices.PaymentRequest.PaymentObj.AcctAccessCode();
				acctAcc.setPrimaryIdentity(nd);
				paymObj.setAcctAccessCode(acctAcc);
				paymentR.setPaymentObj(paymObj);

				PaymentInfo paymInfo = new PaymentInfo();
				CashPayment cashP = new CashPayment();
				cashP.setPaymentMethod("4001");
				cashP.setAmount(montant.longValue());
				ApplyList appList = new ApplyList();
				appList.setInvoiceno(numeroFacture);
				cashP.getApplyList().add(appList);
				BankInfo bank = new BankInfo();
				if (source.equalsIgnoreCase("CIB")) {
					bank.setBankCode("036");
				}
				if (source.equalsIgnoreCase("EDAHABYA")) {
					bank.setBankCode("007");
				}
				cashP.setBankInfo(bank);
				paymInfo.getCashPayment().add(cashP);
				paymentR.setPaymentInfo(paymInfo);

				BigInteger currency = new BigInteger("1044");
				paymentR.setCurrencyID(currency);

				reqPayment.setPaymentRequest(paymentR);
				PaymentResultMsg respPayment = new PaymentResultMsg();

				try {

					// setting the timeout
					Client c = ClientProxy.getClient(cbs);
					HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
					httpConduit.getClient().setConnectionTimeout(
							Long.parseLong(timeout));
					httpConduit.getClient().setReceiveTimeout(
							Long.parseLong(timeout));

					/************* read payment invoice response **********************************/
					respPayment = cbs.payment(reqPayment);

					String Message_CBS = respPayment.getResultHeader()
							.getResultDesc();
					String Code_CBS = respPayment.getResultHeader()
							.getResultCode();

					if (Code_CBS.equalsIgnoreCase("0")
							&& Message_CBS.equals("Operation successfully.")) {

						result.setCode("00");
						result.setMessage("Success");

						FactureModel facture = new FactureModel();

						facture.setAUTH_CODE_S(authCodePoste);
						facture.setCODE_REPONSE(Code_CBS);
						facture.setCODE_REPONSE_S(codeReponsePoste);
						facture.setDATE_TRAITEMENT(year + "-" + month + "-"
								+ day);
						facture.setDATE_TRAITEMENT_S(dateTraitementPoste);
						facture.setDATE_TRANSACTION(dateTransaction);
						facture.setHEURE_TRAITEMENT(hour + ":" + min);
						facture.setHEURE_TRAITEMENT_S(heureTraitementPoste);
						facture.setHEURE_TRANSACTION(heureTransaction);
						facture.setMONTANT(montant);
						facture.setNCLI(ncli);
						facture.setND(nd);
						facture.setNFACT(numeroFacture);
						facture.setNUM_TRANS(num_transaction);
						facture.setNUM_TRANS_S(numTransactionPoste);
						facture.setRESULT(resultat);
						facture.setSTATUT(statut);
						facture.setSOURCE(source);

						factureDao.insertFacture(facture);

					} else {

						/*************** Lire le fichier des erreurs ********************/
						ClassLoader classLoader = this.getClass()
								.getClassLoader();
						Properties prop = new Properties();
						FileInputStream in = new FileInputStream(classLoader
								.getResource("error.properties").getFile());
						prop.load(in);
						in.close();
						/*************************************************************/

						result.setCode(Code_CBS);
						result.setMessage(prop.getProperty(Code_CBS));

						FactureModel facture = new FactureModel();

						facture.setAUTH_CODE_S(authCodePoste);
						facture.setCODE_REPONSE(Code_CBS);
						facture.setCODE_REPONSE_S(codeReponsePoste);
						facture.setDATE_TRAITEMENT(year + "-" + month + "-"
								+ day);
						facture.setDATE_TRAITEMENT_S(dateTraitementPoste);
						facture.setDATE_TRANSACTION(dateTransaction);
						facture.setHEURE_TRAITEMENT(hour + ":" + min);
						facture.setHEURE_TRAITEMENT_S(heureTraitementPoste);
						facture.setHEURE_TRANSACTION(heureTransaction);
						facture.setMONTANT(montant);
						facture.setNCLI(ncli);
						facture.setND(nd);
						facture.setNFACT(numeroFacture);
						facture.setNUM_TRANS(num_transaction);
						facture.setNUM_TRANS_S(numTransactionPoste);
						facture.setRESULT(resultat);
						facture.setSTATUT(statut);
						facture.setSOURCE(source);

						factureDao.insertFacture(facture);

					}

				} catch (Exception e) {
					logger.error("Error while send request to cbs :"
							+ num_transaction, e);

					/*************** Lire le fichier des erreurs ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/

					result.setCode("100");
					result.setMessage(prop.getProperty("100")
							+ e.getCause().getMessage());

				}

				context.close();

			} else {

				/*************** Lire le fichier des erreurs ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/

				result.setCode("68");
				result.setMessage(prop.getProperty("68"));
			}

		} else {

			/*************** Lire le fichier des erreurs ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/

			result.setCode("67");
			result.setMessage(prop.getProperty("67"));

		}

		RechargePaiementReponse response = new RechargePaiementReponse();
		response.setRecharge(result);
		return response;

	}

	@Override
	public FactureReponse invoices(String nd) throws RemoteException,
			ServiceException, SOAPException, IOException {

		List<FactureDetailModel> result = new ArrayList<FactureDetailModel>();

		FactureReponse response = new FactureReponse();

		/******************* check the parameters ***************************/

		Boolean check = true;

		if (nd.length() != 8 || !(nd.matches("[0-9]+"))) {
			check = false;
		}

		if (check == true) {

			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "application-config.xml" });

			ArServices cbs = (ArServices) context.getBean("cbsWS");

			/******************* generate message sequence ********************/
			String seq = String.valueOf(System.currentTimeMillis());
			seq = seq + (int) (Math.random() * 1000);

			try {
				/******************* invoice request *****************************/
				QueryInvoiceRequestMsg reqInv = new QueryInvoiceRequestMsg();

				RequestHeader header3 = new RequestHeader();
				header3.setVersion("1");
				header3.setBusinessCode("QueryInvoice");
				header3.setMessageSeq(seq);
				OwnershipInfo own = new OwnershipInfo();
				own.setBEID("101");
				own.setBRID("101");
				header3.setOwnershipInfo(own);
				SecurityInfo accSec = new SecurityInfo();
				accSec.setLoginSystemCode("");
				accSec.setPassword("==");
				header3.setAccessSecurity(accSec);
				OperatorInfo oprInf = new OperatorInfo();
				oprInf.setChannelID("62");
				oprInf.setOperatorID("3001");
				header3.setOperatorInfo(oprInf);

				reqInv.setRequestHeader(header3);
				QueryInvoiceRequest qrInv = new QueryInvoiceRequest();
				AcctAccessCode accCode = new AcctAccessCode();
				accCode.setPrimaryIdentity(nd);
				qrInv.setAcctAccessCode(accCode);
				qrInv.setOutstandingFlag("Y");
				reqInv.setQueryInvoiceRequest(qrInv);
				QueryInvoiceResultMsg respInv = new QueryInvoiceResultMsg();

				// setting the timeout
				Client c = ClientProxy.getClient(cbs);
				HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
				httpConduit.getClient().setConnectionTimeout(
						Long.parseLong(timeout));
				httpConduit.getClient().setReceiveTimeout(
						Long.parseLong(timeout));

				/************* read invoice response **********************************/
				respInv = cbs.queryInvoice(reqInv);
				String Message_CBS = respInv.getResultHeader().getResultDesc();
				String Code_CBS = respInv.getResultHeader().getResultCode();

				if (Code_CBS.equalsIgnoreCase("0")
						&& Message_CBS.equals("Operation successfully.")) {

					List<InvoiceInfo> invoices = new ArrayList<InvoiceInfo>();
					if (respInv.getQueryInvoiceResult() != null) {
						invoices = respInv.getQueryInvoiceResult()
								.getInvoiceInfo();

						for (int i = 0; i < invoices.size(); i++) {
							FactureDetailModel fact = new FactureDetailModel();
							fact.setNd(invoices.get(i).getPrimaryIdentity());
							fact.setNumFacture(invoices.get(i).getInvoiceNo());
							fact.setDateDebutFacture(invoices.get(i)
									.getBillCycleBeginTime());
							fact.setDateFinFacture(invoices.get(i)
									.getBillCycleEndTime());
							fact.setMontant(String.valueOf(invoices.get(i)
									.getInvoiceAmount() / 100.00));

							result.add(fact);
						}

					}

					response.setCode("00");
					response.setMessage("Success");

				} else {

					/*************** Lire le fichier des erreurs ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/

					response.setCode(Code_CBS);
					response.setMessage(prop.getProperty(Code_CBS));

				}

			} catch (Exception e) {
				logger.error("Error while send request to cbs :" + e);

				/*************** Lire le fichier des erreurs ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/

				response.setCode("100");
				response.setMessage(prop.getProperty("100") + e.getMessage());

			}

			context.close();

		} else {

			/*************** Lire le fichier des erreurs ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/

			response.setCode("67");
			response.setMessage(prop.getProperty("67"));

		}

		response.setFacture(result);
		return response;

	}

	@Override
	public ClientReponse client(String nd) throws RemoteException,
			ServiceException, SOAPException, IOException {

		ClientReponse response = new ClientReponse();

		ClientModel result = new ClientModel();

		List<ProduitModel> produit = new ArrayList<ProduitModel>();

		/******************* check the parameters ***************************/

		Boolean check = true;

		if (nd.length() > 9 || nd.length() < 8 || !(nd.matches("[0-9]+"))) {
			check = false;
		}

		if (check == true) {

			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "application-config.xml" });

			CustomerInterfaces customer = (CustomerInterfaces) context
					.getBean("customerWS");

			com.huawei.crm.service.SubscriberInterfaces subscriber = (com.huawei.crm.service.SubscriberInterfaces) context
					.getBean("subscriberWS");

			AccountInterfaces accountInterfaces = (AccountInterfaces) context
					.getBean("accountWS");

			/******************* generate message sequence ********************/
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String reqTime = sdf.format(System.currentTimeMillis());

			String seq = String.valueOf(System.currentTimeMillis());
			seq = seq + (int) (Math.random() * 1000);

			try {
				/******************* customer request ****************************/
				QueryCustInfoReqMsg req = new QueryCustInfoReqMsg();

				ReqHeader header = new ReqHeader();
				header.setVersion("1");
				header.setBusinessCode("QueryCustInfo");
				header.setTransactionId(seq);
				header.setChannel("62");
				header.setPartnerId("101");
				header.setReqTime(reqTime);
				header.setAccessUser("");
				header.setAccessPassword("==");
				header.setOperatorId("");

				req.setReqHeader(header);
				req.setMSISDN(nd);

				// setting the timeout
				Client c = ClientProxy.getClient(customer);
				HTTPConduit httpConduit = (HTTPConduit) c.getConduit();
				httpConduit.getClient().setConnectionTimeout(
						Long.parseLong(timeout));
				httpConduit.getClient().setReceiveTimeout(
						Long.parseLong(timeout));

				QueryCustInfoRspMsg resp = new QueryCustInfoRspMsg();
				/************* read customer response **********************************/
				resp = customer.queryCustomerInformation(req);

				String code = resp.getRspHeader().getReturnCode();
				String msg = resp.getRspHeader().getReturnMsg();

				if (resp.getRspHeader().getReturnCode()
						.equalsIgnoreCase("0000")
						&& resp.getRspHeader().getReturnMsg()
								.equalsIgnoreCase("Success")) {

					String nom = resp.getCustomer().getName().getFirstName();
					String prenom = resp.getCustomer().getName().getLastName();

					List<Address> address = resp.getCustomer().getAddress();
					String province = "";
					if (address.get(0).getAddress2() != null) {
						province = address.get(0).getAddress2();
					}
					String district = "";
					if (address.get(0).getAddress3() != null) {
						district = address.get(0).getAddress3();
					}
					String city = "";
					if (address.get(0).getAddress4() != null) {
						city = address.get(0).getAddress4();
					}
					String street = "";
					if (address.get(0).getAddress5() != null) {
						street = address.get(0).getAddress5();
					}

					/*************** Lire le fichier des wilaya ********************/
					ClassLoader classLoaderP = this.getClass().getClassLoader();
					Properties propP = new Properties();
					FileInputStream inP = new FileInputStream(classLoaderP
							.getResource("province.properties").getFile());
					propP.load(inP);
					inP.close();
					/*************************************************************/

					/*************** Lire le fichier des commune ********************/
					ClassLoader classLoaderD = this.getClass().getClassLoader();
					Properties propD = new Properties();
					FileInputStream inD = new FileInputStream(classLoaderD
							.getResource("district.properties").getFile());
					propD.load(inD);
					inD.close();
					/*************************************************************/

					/*************** Lire le fichier des cité ********************/
					ClassLoader classLoaderC = this.getClass().getClassLoader();
					Properties propC = new Properties();
					FileInputStream inC = new FileInputStream(classLoaderC
							.getResource("city.properties").getFile());
					propC.load(inC);
					inC.close();
					/*************************************************************/

					result.setNom(nom);
					result.setPrenom(prenom);
					result.setAdresse(street + " " + propC.getProperty(city)
							+ ", " + propD.getProperty(district) + ", "
							+ propP.getProperty(province));

					/******************* subscriber request ****************************/
					QuerySubscriberAllInfoReqMsg reqSub = new QuerySubscriberAllInfoReqMsg();

					com.huawei.crm.basetype.RequestHeader header2 = new com.huawei.crm.basetype.RequestHeader();
					header2.setVersion("1");
					header2.setTransactionId(seq);
					header2.setChannelId("62");
					header2.setTechnicalChannelId("51");
					header2.setTenantId("101");
					header2.setAccessUser("");
					header2.setAccessPwd("==");
					header2.setOperatorId("");

					reqSub.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub = new QuerySubscriberAllInfoIn();
					sub.setIncludeOfferFlag("1");
					sub.setServiceNumber(nd);
					reqSub.setQuerySubscriberAllInfoBody(sub);

					// setting the timeout
					Client c2 = ClientProxy.getClient(subscriber);
					HTTPConduit httpConduit2 = (HTTPConduit) c2.getConduit();
					httpConduit2.getClient().setConnectionTimeout(
							Long.parseLong(timeout));
					httpConduit2.getClient().setReceiveTimeout(
							Long.parseLong(timeout));

					QuerySubscriberAllInfoRspMsg respSub = new QuerySubscriberAllInfoRspMsg();
					/************* read subscriber response **********************************/
					respSub = subscriber.querySubscriberAllInfo(reqSub);

					if (respSub.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")) {

						String etat = respSub.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getStatus();
						String ndR = respSub.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getServiceNumber();
						String ncli = respSub.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getCustomerCode();
						String typeClt = respSub
								.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getNetworkType();

						result.setNcli(ncli);
						result.setNd(ndR);

						/*************** Lire le fichier des etats ********************/
						ClassLoader classLoader = this.getClass()
								.getClassLoader();
						Properties prop = new Properties();
						FileInputStream in = new FileInputStream(classLoader
								.getResource("etat.properties").getFile());
						prop.load(in);
						in.close();
						/*************************************************************/

						/*************** Lire le fichier des types ********************/
						ClassLoader classLoader1 = this.getClass()
								.getClassLoader();
						Properties prop1 = new Properties();
						FileInputStream in1 = new FileInputStream(classLoader1
								.getResource("type.properties").getFile());
						prop1.load(in1);
						in1.close();
						/*************************************************************/

						ProduitModel prod = new ProduitModel();
						prod.setStatus(prop.getProperty("etat." + etat));

						List<GetSubProductInfo> offre = respSub
								.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getPrimaryOffering()
								.getProductList().getGetSubProductInfo();

						for (int i = 0; i < offre.size(); i++) {

							if (offre.get(i).getProductName()
									.equalsIgnoreCase("CLTE_Data")
									|| offre.get(i).getProductName()
											.equalsIgnoreCase("CVoLTE")
									|| offre.get(i).getProductName()
											.equalsIgnoreCase("CLTE_ALL")) {

								prod.setType("4GLTE");

								prod.setOffre(respSub
										.getQuerySubscriberAllInfoBody()
										.getGetSubscriberData()
										.getPrimaryOffering().getOfferingName());

								produit.add(prod);

							}
							/** Fin if **/

							if (offre.get(i).getProductName()
									.equalsIgnoreCase("CPSTN")) {

								prod.setType("PSTN");

								prod.setOffre(respSub
										.getQuerySubscriberAllInfoBody()
										.getGetSubscriberData()
										.getPrimaryOffering().getOfferingName());

								produit.add(prod);

							}
							/** Fin if **/

						}
						/** Fin for **/

					}

					/******************* Balance Free and Unit request ****************************/
					QueryBalanceAndFreeUnitReqMsg queryBalance = new QueryBalanceAndFreeUnitReqMsg();

					ReqHeader header3 = new ReqHeader();
					header3.setVersion("1");
					header3.setTransactionId(seq);
					header3.setChannel("62");
					header3.setPartnerId("101");
					header3.setReqTime(reqTime);
					TimeFormat time = new TimeFormat();
					time.setTimeType("2");
					time.setTimeZoneID(BigInteger.valueOf(2016));
					header3.setTimeFormat(time);
					header3.setAccessUser("");
					header3.setAccessPassword("==");
					queryBalance.setReqHeader(header3);

					ObjectAccessInfo accessInf = new ObjectAccessInfo();
					accessInf.setObjectIdType("4");
					accessInf.setObjectId(nd);
					queryBalance.setAccessInfo(accessInf);

					// setting the timeout
					Client c3 = ClientProxy.getClient(accountInterfaces);
					HTTPConduit httpConduit3 = (HTTPConduit) c3.getConduit();
					httpConduit3.getClient().setConnectionTimeout(
							Long.parseLong(timeout));
					httpConduit3.getClient().setReceiveTimeout(
							Long.parseLong(timeout));

					QueryBalanceAndFreeUnitRspMsg responseB = new QueryBalanceAndFreeUnitRspMsg();
					/************* read balance response **********************************/
					responseB = accountInterfaces
							.queryBalanceAndFreeUnit(queryBalance);

					if (responseB.getRspHeader().getReturnCode()
							.equalsIgnoreCase("0")
							&& responseB
									.getRspHeader()
									.getReturnMsg()
									.equalsIgnoreCase("Operation successfully.")) {

						int nbr = 0;
						nbr = responseB.getAccount().get(0).getOutstandingAmt()
								.size();

						List<OutstandingAmt> dettes = new ArrayList<OutstandingAmt>();
						dettes = responseB.getAccount().get(0)
								.getOutstandingAmt();
						int dette = 0;
						for (int i = 0; i < dettes.size(); i++) {

							dette = dette
									+ Integer.valueOf(dettes.get(i)
											.getOutStandingDetail().get(0)
											.getOutstandingAmt());

						}
						String dt = String.valueOf(dette / 100.00);
						result.setNbFact(String.valueOf(nbr));
						result.setDette(dt);

					}
					/************************** read subscriber product if ADSL **************************************/
					QuerySubscriberAllInfoReqMsg reqSub2 = new QuerySubscriberAllInfoReqMsg();
					String seq2 = String.valueOf(System.currentTimeMillis());
					seq2 = seq2 + (int) (Math.random() * 1000);
					header2.setTransactionId(seq2);
					reqSub2.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub2 = new QuerySubscriberAllInfoIn();
					sub2.setIncludeOfferFlag("1");
					sub2.setServiceNumber("A" + nd);
					reqSub2.setQuerySubscriberAllInfoBody(sub2);
					QuerySubscriberAllInfoRspMsg respSub2 = new QuerySubscriberAllInfoRspMsg();

					/*************** Lire le fichier des etats ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("etat.properties").getFile());
					prop.load(in);
					in.close();

					/************* read subscriber response **********************************/
					respSub2 = subscriber.querySubscriberAllInfo(reqSub2);
					if (respSub2.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub2.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")) {
						String etat2 = respSub2.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getStatus();
						ProduitModel prod = new ProduitModel();
						prod.setType("ADSL");
						prod.setStatus(prop.getProperty("etat." + etat2));

						List<GetSubProductInfo> offre = respSub2
								.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getPrimaryOffering()
								.getProductList().getGetSubProductInfo();

						for (int i = 0; i < offre.size(); i++) {

							if (offre.get(i).getProductName()
									.equalsIgnoreCase("CADSL")) {

								prod.setOffre(respSub2
										.getQuerySubscriberAllInfoBody()
										.getGetSubscriberData()
										.getPrimaryOffering().getOfferingName());

							}
							/** Fin if **/

						}
						/** Fin for **/

						produit.add(prod);

					}

					/************************** read subscriber product if FTTH **************************************/
					QuerySubscriberAllInfoReqMsg reqSub3 = new QuerySubscriberAllInfoReqMsg();
					String seq3 = String.valueOf(System.currentTimeMillis());
					seq3 = seq3 + (int) (Math.random() * 1000);
					header2.setTransactionId(seq3);
					reqSub3.setRequestHeader(header2);
					QuerySubscriberAllInfoIn sub3 = new QuerySubscriberAllInfoIn();
					sub3.setIncludeOfferFlag("1");
					sub3.setServiceNumber("F" + nd);
					reqSub3.setQuerySubscriberAllInfoBody(sub3);
					QuerySubscriberAllInfoRspMsg respSub3 = new QuerySubscriberAllInfoRspMsg();
					/************* read subscriber response **********************************/
					respSub3 = subscriber.querySubscriberAllInfo(reqSub3);
					if (respSub3.getResponseHeader().getRetCode()
							.equalsIgnoreCase("0000")
							&& respSub3.getResponseHeader().getRetMsg()
									.equalsIgnoreCase("Success")) {
						String etat2 = respSub3.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getStatus();
						ProduitModel prod = new ProduitModel();
						prod.setType("FTTH");
						prod.setStatus(prop.getProperty("etat." + etat2));

						List<GetSubProductInfo> offre = respSub3
								.getQuerySubscriberAllInfoBody()
								.getGetSubscriberData().getPrimaryOffering()
								.getProductList().getGetSubProductInfo();

						for (int i = 0; i < offre.size(); i++) {

							if (offre.get(i).getProductName()
									.equalsIgnoreCase("CFTTx")) {

								prod.setOffre(respSub3
										.getQuerySubscriberAllInfoBody()
										.getGetSubscriberData()
										.getPrimaryOffering().getOfferingName());

							}
							/** Fin if **/

						}
						/** Fin for **/

						produit.add(prod);

					}

					response.setCode("00");
					response.setMessage("Success");
					response.setProduit(produit);

				} else {

					/*************** Lire le fichier des erreurs ********************/
					ClassLoader classLoader = this.getClass().getClassLoader();
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(classLoader
							.getResource("error.properties").getFile());
					prop.load(in);
					in.close();
					/*************************************************************/
					response.setCode(code);
					response.setMessage(prop.getProperty(code));

				}

			} catch (Exception e) {
				logger.error("Error while send request to cbs :" + e);

				/*************** Lire le fichier des erreurs ********************/
				ClassLoader classLoader = this.getClass().getClassLoader();
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(classLoader
						.getResource("error.properties").getFile());
				prop.load(in);
				in.close();
				/*************************************************************/

				response.setCode("100");
				response.setMessage(prop.getProperty("100") + e.getMessage());

			}
			
			context.close();

		} else {

			/*************** Lire le fichier des erreurs ********************/
			ClassLoader classLoader = this.getClass().getClassLoader();
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(classLoader.getResource(
					"error.properties").getFile());
			prop.load(in);
			in.close();
			/*************************************************************/
			response.setCode("67");
			response.setMessage(prop.getProperty("67"));

		}

		response.setCompte(result);
		return response;

	}

}
