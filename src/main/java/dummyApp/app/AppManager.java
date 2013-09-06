package dummyApp.app;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.Date;

import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder.AmountType;
import com.premiumminds.billy.core.services.entities.Product.ProductType;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.BillyPortugal;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTCustomerEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTProductEntity;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParamsImpl;
import com.premiumminds.billy.portugal.services.entities.PTAddress;
import com.premiumminds.billy.portugal.services.entities.PTApplication;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;
import com.premiumminds.billy.portugal.services.entities.PTContact;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTPayment;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
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

	public PTProduct.Builder createProduct() {
		Taxes taxes = new Taxes(injector);
		PTProduct.Builder product = billyPortugal.products().builder();

		product.setDescription("Garrafa de vinho tinto")
				.setNumberCode("123-456").setProductCode("123")
				.setType(ProductType.GOODS).setUnitOfMeasure("L")
				.addTaxUID(taxes.continent().normal().getUID());

		return product;
	}

	public PTInvoiceEntry.Builder createInvoiceEntry(BigDecimal quantity,
			BigDecimal price) {
		PTInvoiceEntry.Builder builder = billyPortugal.invoices()
				.entryBuilder();
		PTProduct.Builder product = createProduct();
		billy.persistProduct(product);
		PTProductEntity productEntity = (PTProductEntity) product.build();
		builder.setAmountType(AmountType.WITH_TAX)
				.setCurrency(Currency.getInstance("EUR"))
				.setDescription(productEntity.getDescription())
				.setProductUID(productEntity.getUID())
				.setContextUID(
						billyPortugal.contexts().portugal().allRegions()
								.getUID()).setQuantity(quantity)
				.setTaxPointDate(new Date())
				.setUnitAmount(AmountType.WITH_TAX, price)
				.setUnitOfMeasure(productEntity.getUnitOfMeasure());

		return builder;
	}

	public PTInvoice.Builder createInvoice(PTInvoiceEntry.Builder entry,
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
		
		return builder;
	}
}
