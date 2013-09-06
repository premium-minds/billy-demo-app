package dummyApp.persistence;

import com.google.inject.Injector;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.BillyPortugal;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParamsImpl;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;
import com.premiumminds.billy.portugal.services.entities.PTCreditNote;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;
import com.premiumminds.billy.portugal.util.KeyGenerator;

import dummyApp.app.App;


public class Billy {
	public static final String PRIVATE_KEY_DIR = "src/main/resources/private.pem";
	public Injector injector;
	public BillyPortugal billyPortugal;
	public PTIssuingParams parameters;
	
	public Billy(Injector injector, BillyPortugal billyPortugal){
		this.injector = injector;
		this.billyPortugal = billyPortugal;
		KeyGenerator generator = new KeyGenerator(App.PRIVATE_KEY_DIR);
		parameters = new PTIssuingParamsImpl();
		parameters.setPrivateKey(generator.getPrivateKey());
		parameters.setPublicKey(generator.getPublicKey());
		parameters.setPrivateKeyVersion("1");
		parameters.setEACCode("31400");
	}
	
	public void persistCustomer(PTCustomer.Builder customer){
		billyPortugal.customers().persistence().create(customer);
	}
	
	public void persistBusiness(PTBusiness.Builder business){
		billyPortugal.businesses().persistence().create(business);
	}
	
	public void persistProduct(PTProduct.Builder product){
		billyPortugal.products().persistence().create(product);
	}
	
	public void issueInvoice(PTInvoice.Builder invoice, PTIssuingParams params) throws DocumentIssuingException{
		billyPortugal.invoices().issue(invoice, params);
	}
	
	public void issueSimpleInvoice(PTSimpleInvoice.Builder simpleInvoice, PTIssuingParams params) throws DocumentIssuingException{
		billyPortugal.simpleInvoices().issue(simpleInvoice, params);
	}
	
	public void issueCreditNote(PTCreditNote.Builder creditNote, PTIssuingParams params) throws DocumentIssuingException{
		billyPortugal.creditNotes().issue(creditNote, params);
	}
	
	
}
