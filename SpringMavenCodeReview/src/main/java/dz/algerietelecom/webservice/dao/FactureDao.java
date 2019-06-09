package dz.algerietelecom.webservice.dao;

import dz.algerietelecom.webservice.domain.FactureModel;


public interface FactureDao  {

	public void insertFacture(FactureModel fact);
	
	public int checkNumTransaction(String numTransaction);
	
	public int checkIdTransaction(Integer idTransaction);
	
}
