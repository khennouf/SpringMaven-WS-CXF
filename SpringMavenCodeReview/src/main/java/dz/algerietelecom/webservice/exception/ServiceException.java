package dz.algerietelecom.webservice.exception;

import java.io.Serializable;


public class ServiceException extends Exception implements Serializable {
	
	 private ServiceExceptionDetails faultDetails[];

	  public ServiceException(ServiceExceptionDetails faultDetails[]) {
	    this.faultDetails = faultDetails;
	  }

	  public ServiceException(String message, ServiceExceptionDetails faultDetails[]) {
	    super(message);
	    this.faultDetails = faultDetails;
	  }

	  public ServiceExceptionDetails[] getFaultDetails() {
	    return faultDetails;
	  }


}
