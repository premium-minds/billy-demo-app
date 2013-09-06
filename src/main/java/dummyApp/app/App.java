package dummyApp.app;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder.AmountType;
import com.premiumminds.billy.core.services.entities.Product.ProductType;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.BillyPortugal;
import com.premiumminds.billy.portugal.PortugalBootstrap;
import com.premiumminds.billy.portugal.PortugalDependencyModule;
import com.premiumminds.billy.portugal.PortugalPersistenceDependencyModule;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTPayment;
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
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;
import com.premiumminds.billy.portugal.util.KeyGenerator;
import com.premiumminds.billy.portugal.util.PaymentMechanism;
import com.premiumminds.billy.portugal.util.Taxes;

/**
 * Hello world!
 * 
 */
public class App {
	public static final String PRIVATE_KEY_DIR = "src/main/resources/private.pem";
	public static BillyPortugal billyPortugal;
	public static Injector injector;

	public static void main(String[] args) throws MalformedURLException,
			DocumentIssuingException, IOException, SAFTPTExportException {

		injector = Guice.createInjector(new PortugalDependencyModule(),
				new PortugalPersistenceDependencyModule());
		injector.getInstance(PortugalDependencyModule.Initializer.class);
		injector.getInstance(PortugalPersistenceDependencyModule.Initializer.class);

		PTIssuingParams parameters;
		KeyGenerator generator = new KeyGenerator(App.PRIVATE_KEY_DIR);
		parameters = new PTIssuingParamsImpl();
		parameters.setPrivateKey(generator.getPrivateKey());
		parameters.setPublicKey(generator.getPublicKey());
		parameters.setPrivateKeyVersion("1");
		parameters.setEACCode("31400");
		parameters.setInvoiceSeries("DEFAULT");
		billyPortugal = new BillyPortugal(injector);
		PortugalBootstrap.execute(injector);
		PTAddress.Builder address = createAddress();
		PTContact.Builder contact = createContact();
		PTApplication.Builder application = createApplication(address, contact);
		
		PTCustomer.Builder customer = createCustomer(address, contact);
		customer.build().getUID();
		billyPortugal.customers().persistence().create(customer);

		PTBusiness.Builder business = createBussiness(application, address, contact);
		business.build().getUID();
		billyPortugal.businesses().persistence().create(business);

		PTProduct.Builder product = createProduct();
		product.build().getUID();
		billyPortugal.products().persistence().create(product);
		PTInvoiceEntry.Builder entry = createInvoiceEntry((PTProductEntity) product
				.build());

		PTPayment.Builder payment = createPayment();

		PTInvoice.Builder invoice = createInvoice(entry, payment,
				(PTBusinessEntity) business.build(),
				(PTCustomerEntity) customer.build());
		billyPortugal.invoices().issue(invoice, parameters);
		Calendar calendar = Calendar.getInstance();
		calendar.set(2013, 1, 1);
		
		billyPortugal.saft().export(application.build().getUID(), business.build().getUID(), "123", calendar.getTime(), new Date());

	}

	public static PTAddress.Builder createAddress() {
		PTAddress.Builder addressBuilder = billyPortugal.addresses().builder();
		addressBuilder.setBuilding("10").setCity("Lisboa")
				.setDetails("Rua da Correnteza nº 26 2º esq")
				.setISOCountry("Portugal").setNumber("26")
				.setPostalCode("1400-079").setRegion("Portugal Continental")
				.setStreetName("Rua da Correnteza");
		return addressBuilder;

	}

	public static PTContact.Builder createContact() {
		PTContact.Builder contactBuilder = billyPortugal.contacts().builder();
		contactBuilder.setName("Zé Povinho")
				.setEmail("zepovinho@premiumminds.com").setMobile("91219121")
				.setFax("21912191").setTelephone("21922192")
				.setWebsite("zepovinho.com");
		return contactBuilder;

	}

	public static PTCustomer.Builder createCustomer(PTAddress.Builder address,
			PTContact.Builder contact) {
		PTCustomer.Builder customerBuilder = billyPortugal.customers()
				.builder();
		customerBuilder.setBillingAddress(address).addAddress(address, true)
				.setName("Zé Povinho").setReferralName("Zé Povinho")
				.setShippingAddress(address)
				.setTaxRegistrationNumber("123456789", "PT")
				.setHasSelfBillingAgreement(false).addContact(contact);

		return customerBuilder;
	}

	public static PTApplication.Builder createApplication(
			PTAddress.Builder address, PTContact.Builder contact)
			throws MalformedURLException {
		PTApplication.Builder application = billyPortugal.applications()
				.builder();
		application.setDeveloperCompanyName("Premium Minds")
				.setDeveloperCompanyTaxIdentifier("12345789")
				.addContact(contact).setMainContact(contact)
				.setSoftwareCertificationNumber(123).setName("Billy")
				.setVersion("1.0")
				.setApplicationKeysPath(new URL("http://www.keys.path"));

		return application;
	}

	public static PTBusiness.Builder createBussiness(
			PTApplication.Builder application, PTAddress.Builder address,
			PTContact.Builder contact) {
		PTBusiness.Builder business = billyPortugal.businesses().builder();
		business.setAddress(address).setBillingAddress(address)
				.addApplication(application).addContact(contact, true)
				.setCommercialName("Pingo Doce")
				.setFinancialID("123456789", "PT").setName("Pingo Doce")
				.setShippingAddress(address).setWebsite("pingodoce.pt");
		return business;
	}

	public static PTProduct.Builder createProduct() {
		Taxes taxes = new Taxes(injector);
		PTProduct.Builder product = billyPortugal.products().builder();

		product.setDescription("Garrafa de vinho tinto")
				.setNumberCode("123-456").setProductCode("123")
				.setType(ProductType.GOODS).setUnitOfMeasure("L")
				.addTaxUID(taxes.continent().normal().getUID());

		return product;
	}

	public static PTInvoiceEntry.Builder createInvoiceEntry(
			PTProductEntity product) {
		PTInvoiceEntry.Builder entry = billyPortugal.invoices().entryBuilder();

		entry.setAmountType(AmountType.WITH_TAX)
				.setCurrency(Currency.getInstance("EUR"))
				.setDescription("Descrição da garrafa de vinho")
				.setProductUID(product.getUID())
				.setContextUID(
						billyPortugal.contexts().portugal().allRegions()
								.getUID()).setQuantity(new BigDecimal("3"))
				.setTaxPointDate(new Date())
				.setUnitAmount(AmountType.WITH_TAX, new BigDecimal("5"))
				.setUnitOfMeasure("L");
		return entry;
	}

	public static PTPayment.Builder createPayment() {
		PTPayment.Builder payment = new PTPayment.Builder(
				injector.getInstance(DAOPTPayment.class));
		payment.setPaymentAmount(new BigDecimal("15"))
				.setPaymentDate(new Date())
				.setPaymentMethod(PaymentMechanism.CASH);

		return payment;
	}

	public static PTInvoice.Builder createInvoice(PTInvoiceEntry.Builder entry,
			PTPayment.Builder payment, PTBusinessEntity business,
			PTCustomerEntity customer) {
		PTInvoice.Builder invoice = billyPortugal.invoices().builder();
		invoice.addEntry(entry).addPayment(payment).setSelfBilled(false)
				.setCancelled(false).setBilled(false)
				.setBusinessUID(business.getUID())
				.setCustomerUID(customer.getUID()).setDate(new Date())
				.setSourceId("Source").setSourceBilling(SourceBilling.P);

		return invoice;
	}
}
