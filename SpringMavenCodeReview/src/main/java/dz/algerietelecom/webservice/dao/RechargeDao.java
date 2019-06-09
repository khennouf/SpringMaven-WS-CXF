package dz.algerietelecom.webservice.dao;


import java.util.Date;
import java.util.List;

import dz.algerietelecom.webservice.domain.RechargeModel;
import dz.algerietelecom.webservice.domain.RechargeNokModel;
import dz.algerietelecom.webservice.domain.ResultRechargeModel;

public interface RechargeDao  {

	public void insertRecharge(RechargeModel c);
	
	public void insertRechargeNok(RechargeNokModel c);
	
	public int checkVoucher(String serialNumber);
	
	public List <RechargeModel> historyRechargeAdsl(String accountNumber);
	
	public List <RechargeNokModel> historyRechargeAdslNok(String accountNumber);
	
	public List <RechargeModel> historyRechargeVod(String accountNumber);
	
	public List <RechargeNokModel> historyRechargeVodNok(String accountNumber);
	
	public ResultRechargeModel selectMessage (Number code);
	
	public RechargeModel VoucherUsed(String batch_serial_no);
	
	public int checkRecharge(String accountNumber,Date dateR);
	
	public int checkTransaction(String numTransaction);
   


}
