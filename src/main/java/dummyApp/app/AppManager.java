package dummyApp.app;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.Date;

import com.google.inject.Injector;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder.AmountType;
import com.premiumminds.billy.core.services.entities.Product.ProductType;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.BillyPortugal;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTCreditNoteEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTCustomerEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTInvoiceEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTProductEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTSimpleInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParamsImpl;
import com.premiumminds.billy.portugal.services.entities.PTAddress;
import com.premiumminds.billy.portugal.services.entities.PTApplication;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;
import com.premiumminds.billy.portugal.services.entities.PTContact;
import com.premiumminds.billy.portugal.services.entities.PTCreditNote;
import com.premiumminds.billy.portugal.services.entities.PTCreditNoteEntry;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTPayment;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice.CLIENTTYPE;
import com.premiumminds.billy.portugal.util.KeyGenerator;
import com.premiumminds.billy.portugal.util.Taxes;

import dummyApp.persistence.Billy;

public class AppManager {

	public static final String COUNTRY_CODE = "PT";
	public static final String COUNTRY = "Portugal";
	private Injector injector;
	private BillyPortugal billyPortugal;
	private Billy billy;
	private PTApplication.Builder application;
	private PTIssuingParams parameters;
	
	public PTApplication.Builder getApp() {
		return application;
	}

	public AppManager(Injector injector) {
		this.injector = injector;
		billyPortugal = new BillyPortugal(this.injector);
		billy = new Billy(this.injector, billyPortugal);
		application = createApplication();

		KeyGenerator generator = new KeyGenerator(App.PRIVATE_KEY_DIR);
		parameters = new PTIssuingParamsImpl();
		parameters.setPrivateKey(generator.getPrivateKey());
		parameters.setPublicKey(generator.getPublicKey());
		parameters.setPrivateKeyVersion("1");
		parameters.setEACCode("31400");
		parameters.setInvoiceSeries("DEFAULT");
	}

	public PTCustomerEntity createCustomer(String name,
			String taxRegistrationNumber, String street, String number,
			String postalCode, String city, String telephone) {
		PTCustomer.Builder builder = billyPortugal.customers().builder();
		PTAddress.Builder address = createAddress(street, number, postalCode,
				city);
		PTContact.Builder contact = createContact(name, telephone);
		builder.setName(name)
				.setTaxRegistrationNumber(taxRegistrationNumber, COUNTRY_CODE)
				.addAddress(address, true).setBillingAddress(address)
				.setHasSelfBillingAgreement(false).setShippingAddress(address)
				.addContact(contact);
		billy.persistCustomer(builder);

		return (PTCustomerEntity) builder.build();
	}

	public PTBusinessEntity createBusiness(String name,
			String taxRegistrationNumber, String street, String number,
			String postalCode, String city, String telephone) {
		return createBusiness(application, name, taxRegistrationNumber, street,
				number, postalCode, city, telephone);
	}

	public PTBusinessEntity createBusiness(PTApplication.Builder application,
			String name, String taxRegistrationNumber, String street,
			String number, String postalCode, String city, String telephone) {
		PTBusiness.Builder builder = billyPortugal.businesses().builder();
		PTContact.Builder contact = createContact(name, telephone);
		PTAddress.Builder address = createAddress(street, number, postalCode,
				city);
		builder.addApplication(application).addContact(contact, true)
				.setAddress(address).setBillingAddress(address)
				.setMainContactUID(contact.build().getUID()).setName(name)
				.setCommercialName(name)
				.setFinancialID(taxRegistrationNumber, COUNTRY_CODE);

		billy.persistBusiness(builder);
		return (PTBusinessEntity) builder.build();
	}

	public PTAddress.Builder createAddress(String street, String number,
			String postalCode, String city) {
		PTAddress.Builder builder = billyPortugal.addresses().builder();
		builder.setStreetName(street)
				.setNumber(number)
				.setPostalCode(postalCode)
				.setCity(city)
				.setISOCountry(COUNTRY)
				.setDetails(
						street + " nr " + number + " " + postalCode + " "
								+ city);

		return builder;
	}

	public PTContact.Builder createContact(String name, String telephone) {
		PTContact.Builder builder = billyPortugal.contacts().builder();
		builder.setName(name).setTelephone(telephone);

		return builder;
	}

	public PTApplication.Builder createApplication() {
		PTApplication.Builder application = billyPortugal.applications()
				.builder();
		PTContact.Builder contact = createContact("Premium Minds", "217817555");
		try {
			application.setDeveloperCompanyName("Premium Minds")
					.setDeveloperCompanyTaxIdentifier("12345789")
					.addContact(contact).setMainContact(contact)
					.setSoftwareCertificationNumber(123).setName("Billy")
					.setVersion("1.0")
					.setApplicationKeysPath(new URL("http://www.keys.path"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return application;
	}

	public PTProductEntity createProduct(String productCode, String description, String unitOfMeasure) {
		Taxes taxes = new Taxes(injector);
		PTProduct.Builder builder = billyPortugal.products().builder();

		builder.setDescription(description)
				.setNumberCode(productCode).setProductCode(productCode)
				.setType(ProductType.GOODS).setUnitOfMeasure(unitOfMeasure)
				.addTaxUID(taxes.continent().normal().getUID());
		
		billy.persistProduct(builder);

		return (PTProductEntity)builder.build();
	}

	public PTInvoiceEntry.Builder createInvoiceEntry(BigDecimal quantity,
			BigDecimal price, PTProductEntity product) {
		PTInvoiceEntry.Builder builder = billyPortugal.invoices()
				.entryBuilder();
		
		builder.setAmountType(AmountType.WITH_TAX)
				.setCurrency(Currency.getInstance("EUR"))
				.setDescription(product.getDescription())
				.setProductUID(product.getUID())
				.setContextUID(
						billyPortugal.contexts().portugal().allRegions()
								.getUID()).setQuantity(quantity)
				.setTaxPointDate(new Date())
				.setUnitAmount(AmountType.WITH_TAX, price)
				.setUnitOfMeasure(product.getUnitOfMeasure());

		return builder;
	}

	public PTInvoiceEntity createInvoice(PTInvoiceEntry.Builder entry,
			PTPayment.Builder payment, PTBusinessEntity business,
			PTCustomerEntity customer) {
		PTInvoice.Builder builder = billyPortugal.invoices().builder();
		builder.addEntry(entry).addPayment(payment).setSelfBilled(false)
				.setCancelled(false).setBilled(false)
				.setBusinessUID(business.getUID())
				.setCustomerUID(customer.getUID()).setDate(new Date())
				.setSourceId("Source").setSourceBilling(SourceBilling.P);
		try{
			billy.issueInvoice(builder, parameters);
		}
		catch(DocumentIssuingException e){
			e.printStackTrace();
		}
		
		return (PTInvoiceEntity) builder.build();
	}
	
	public PTSimpleInvoiceEntity createSimpleInvoice(PTInvoiceEntry.Builder entry,
			PTPayment.Builder payment, PTBusinessEntity business,
			PTCustomerEntity customer, CLIENTTYPE clientType) {
		PTSimpleInvoice.Builder builder = billyPortugal.simpleInvoices().builder();
		
		builder.addEntry(entry).addPayment(payment).setSelfBilled(false)
				.setCancelled(false).setBilled(false)
				.setBusinessUID(business.getUID())
				.setCustomerUID(customer.getUID()).setDate(new Date())
				.setSourceId("Source").setSourceBilling(SourceBilling.P)
				.setClientType(clientType);
		try{
			billy.issueSimpleInvoice(builder, parameters);
		}
		catch(DocumentIssuingException e){
			e.printStackTrace();
		}
		
		return (PTSimpleInvoiceEntity)builder.build();
	}
	
	public PTCreditNoteEntry.Builder createCreditNoteEntry(PTProductEntity product, String documentUID, BigDecimal quantity, BigDecimal unitAmount, String reason){
		PTCreditNoteEntry.Builder builder = billyPortugal.creditNotes().entryBuilder();
		
		builder
		.setAmountType(AmountType.WITH_TAX)
		.setContextUID(billyPortugal.contexts().portugal().allRegions()
								.getUID())
		.setCurrency(Currency.getInstance("EUR"))
		.setQuantity(quantity)
		.setDescription(product.getDescription())
		.setProductUID(product.getUID())
		.setReason(reason)
		.setReferenceUID(new UID(documentUID))
		.setUnitAmount(AmountType.WITH_TAX, unitAmount)
		.setUnitOfMeasure(product.getUnitOfMeasure());
		
		return builder;
	}
	
	public PTCreditNoteEntity createCreditNote(PTCreditNoteEntry.Builder entry, PTPayment.Builder payment, PTBusinessEntity business, PTCustomerEntity customer){
		PTCreditNote.Builder builder = billyPortugal.creditNotes().builder();
		
		builder.addEntry(entry)
		.addPayment(payment)
		.setBilled(false)
		.setBusinessUID(business.getUID())
		.setCancelled(false)
		.setCurrency(Currency.getInstance("EUR"))
		.setCustomerUID(customer.getUID())
		.setDate(new Date())
		.setSelfBilled(false)
		.setSourceBilling(SourceBilling.P)
		.setSourceId("SOURCE");
		try{
		billy.issueCreditNote(builder, parameters);
		}
		catch(DocumentIssuingException e){
			e.printStackTrace();
		}
		return (PTCreditNoteEntity) builder.build();
	}
	
	
}
