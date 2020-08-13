package dummyApp.app;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder;
import com.premiumminds.billy.core.services.entities.Product;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.gin.services.exceptions.ExportServiceException;
import com.premiumminds.billy.portugal.BillyPortugal;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.entities.PTAddress;
import com.premiumminds.billy.portugal.services.entities.PTApplication;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;
import com.premiumminds.billy.portugal.services.entities.PTContact;
import com.premiumminds.billy.portugal.services.entities.PTCreditNote;
import com.premiumminds.billy.portugal.services.entities.PTCreditNoteEntry;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;
import com.premiumminds.billy.portugal.services.export.pdf.creditnote.PTCreditNotePDFExportRequest;
import com.premiumminds.billy.portugal.services.export.pdf.creditnote.PTCreditNoteTemplateBundle;
import com.premiumminds.billy.portugal.services.export.pdf.invoice.PTInvoicePDFExportRequest;
import com.premiumminds.billy.portugal.services.export.pdf.invoice.PTInvoiceTemplateBundle;
import com.premiumminds.billy.portugal.services.export.saftpt.PTSAFTFileGenerator;
import com.premiumminds.billy.portugal.util.KeyGenerator;
import dummyApp.persistence.Billy;

public class DemoApp {
	private final Injector injector;

	public DemoApp(Injector injector) {
		this.injector = injector;
	}

	public void run()
            throws IOException,
            ParseException,
            DocumentIssuingException,
            SAFTPTExportException,
            ExportServiceException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		BillyPortugal billyPortugal = new BillyPortugal(injector);
		PTIssuingParams invoiceParameters = getPtInvoiceIssuingParams();
		PTIssuingParams creditNoteParameters = getPtCreditNoteIssuingParams();

		PTApplication.Builder applicationBuilder = getPTApplicationBuilder(billyPortugal);
		PTApplication application = createPtApplication(billyPortugal, applicationBuilder);
		PTBusiness business = createPtBusiness(billyPortugal, applicationBuilder);
		PTCustomer customer = createPtCustomer(billyPortugal);
		PTProduct product = createPtProduct(billyPortugal);
		PTInvoice invoice = createPtInvoice(dateFormat, billyPortugal, business, invoiceParameters, customer, product);
		PTCreditNote creditNote = createPtCreditNote(dateFormat,
                billyPortugal,
                business,
                creditNoteParameters,
                customer,
                product,
                invoice);

		Date startDate = dateFormat.parse("01-01-2013");
		Date endDate = dateFormat.parse("01-01-2014");

		exportSAFT(billyPortugal, application, business, startDate, endDate);

		exportInvoicePDF(billyPortugal, application, invoice);
		exportCreditNotePDF(billyPortugal, application, creditNote);
	}

	private PTApplication.Builder getPTApplicationBuilder(BillyPortugal billyPortugal) throws MalformedURLException {
		PTContact.Builder contactBuilder = billyPortugal.contacts().builder();
		contactBuilder.setName("Developer 1")
				.setTelephone("2000000001");

		PTApplication.Builder applicationBuilder = billyPortugal.applications().builder();
		applicationBuilder.setDeveloperCompanyName("Developer Company Name")
				.setDeveloperCompanyTaxIdentifier("1000000001")
				.setSoftwareCertificationNumber(Billy.SOFTWARE_CERTIFICATION)
				.setName("Billy")
				.setVersion("1.0")
				.setApplicationKeysPath(new URL("http://www.keys.path"))
				.addContact(contactBuilder)
				.setMainContact(contactBuilder);
		return applicationBuilder;
	}

	private PTApplication createPtApplication(BillyPortugal billyPortugal, PTApplication.Builder applicationBuilder) {
		return billyPortugal.applications().persistence().create(applicationBuilder);
	}

	private PTBusiness createPtBusiness(BillyPortugal billyPortugal, PTApplication.Builder applicationBuilder) {
		PTContact.Builder contactBuilder = billyPortugal.contacts().builder();
		contactBuilder.setName("CEO 1")
				.setTelephone("200000002");

		PTAddress.Builder addressBuilder = billyPortugal.addresses().builder();
		addressBuilder.setStreetName("Street name 2")
				.setNumber("2")
				.setPostalCode("1000-100")
				.setCity("Lisbon")
				.setISOCountry("PT")
				.setDetails("Av. 5 de Outubro Nº 2 1000-100 Lisboa");

		PTBusiness.Builder businessBuilder = billyPortugal.businesses().builder();
		businessBuilder.addApplication(applicationBuilder)
				.addContact(contactBuilder, true)
				.setMainContactUID(contactBuilder.build().getUID())
				.setName("Business 1")
				.setCommercialName("Business, INC")
				.setFinancialID("500003564", "PT")
				.setAddress(addressBuilder)
				.setBillingAddress(addressBuilder);

		return billyPortugal.businesses().persistence().create(businessBuilder);
	}

	private PTInvoice createPtInvoice(SimpleDateFormat dateFormat,
                                      BillyPortugal billyPortugal,
                                      PTBusiness business,
                                      PTIssuingParams invoiceParameters,
                                      PTCustomer customer,
									  PTProduct product) throws ParseException, DocumentIssuingException {
		PTInvoice.Builder invoiceBuilder = billyPortugal.invoices().builder();

		Date invoiceDate = dateFormat.parse("01-03-2013");
		invoiceBuilder.setSelfBilled(false)
				.setCancelled(false)
				.setBilled(false)
				.setDate(invoiceDate)
				.setSourceId("User 1")
				.setSourceBilling(PTGenericInvoice.SourceBilling.P)
				.setBusinessUID(business.getUID())
				.setCustomerUID(customer.getUID());

		PTInvoiceEntry.Builder entryBuilder = billyPortugal.invoices().entryBuilder();
		entryBuilder.setAmountType(GenericInvoiceEntryBuilder.AmountType.WITH_TAX)
				.setCurrency(Currency.getInstance("EUR"))
				.setContextUID(billyPortugal.contexts().portugal().allRegions().getUID())
				.setQuantity(new BigDecimal("10"))
				.setTaxPointDate(dateFormat.parse("01-02-2013"))
				.setUnitAmount(GenericInvoiceEntryBuilder.AmountType.WITH_TAX, new BigDecimal("100"))
				.setContextUID(billyPortugal.contexts().continent().allContinentRegions().getUID())
				.setProductUID(product.getUID())
				.setDescription(product.getDescription())
				.setUnitOfMeasure(product.getUnitOfMeasure());

		invoiceBuilder.addEntry(entryBuilder);

		return billyPortugal.invoices().issue(invoiceBuilder, invoiceParameters);
	}

	private PTCreditNote createPtCreditNote(SimpleDateFormat dateFormat,
                                            BillyPortugal billyPortugal,
                                            PTBusiness business,
                                            PTIssuingParams invoiceParameters,
                                            PTCustomer customer,
                                            PTProduct product,
                                            PTInvoice invoice) throws ParseException, DocumentIssuingException {
		PTCreditNote.Builder creditNoteBuilder = billyPortugal.creditNotes().builder();

		Date creditNoteDate = dateFormat.parse("01-03-2013");
		creditNoteBuilder.setSelfBilled(false)
				.setCancelled(false)
				.setBilled(false)
				.setDate(creditNoteDate)
				.setSourceId("User 2")
				.setSourceBilling(PTGenericInvoice.SourceBilling.P)
				.setBusinessUID(business.getUID())
				.setCustomerUID(customer.getUID());

		PTCreditNoteEntry.Builder entryBuilder = billyPortugal.creditNotes().entryBuilder();
		entryBuilder.setAmountType(GenericInvoiceEntryBuilder.AmountType.WITH_TAX)
				.setCurrency(Currency.getInstance("EUR"))
				.setContextUID(billyPortugal.contexts().portugal().allRegions().getUID())
				.setQuantity(new BigDecimal("10"))
				.setTaxPointDate(dateFormat.parse("01-02-2013"))
				.setUnitAmount(GenericInvoiceEntryBuilder.AmountType.WITH_TAX, new BigDecimal("100"))
				.setContextUID(billyPortugal.contexts().continent().allContinentRegions().getUID())
				.setProductUID(product.getUID())
				.setDescription(product.getDescription())
				.setUnitOfMeasure(product.getUnitOfMeasure())
				.setReferenceUID(invoice.getUID())
				.setReason("some reason 1");

		creditNoteBuilder.addEntry(entryBuilder);

		return billyPortugal.creditNotes().issue(creditNoteBuilder, invoiceParameters);
	}

	private PTProduct createPtProduct(BillyPortugal billyPortugal) {
		PTProduct.Builder productBuilder = billyPortugal.products().builder();
		productBuilder.setDescription("product 1")
				.setNumberCode("1")
				.setProductCode("1")
				.setType(Product.ProductType.GOODS)
				.setUnitOfMeasure("kg")
				.setProductGroup("group 1")
				.addTaxUID(billyPortugal.taxes().continent().normal().getUID());

		return billyPortugal.products().persistence().create(productBuilder);
	}

	private PTIssuingParams getPtInvoiceIssuingParams() {
		PTIssuingParams invoiceParameters = PTIssuingParams.Util.newInstance();

		KeyGenerator gen = new KeyGenerator(App.PRIVATE_KEY_DIR);

		invoiceParameters.setPrivateKey(gen.getPrivateKey());
		invoiceParameters.setPublicKey(gen.getPublicKey());
		invoiceParameters.setInvoiceSeries("A");
		invoiceParameters.setPrivateKeyVersion("1");
		return invoiceParameters;
	}

	private PTIssuingParams getPtCreditNoteIssuingParams() {
		PTIssuingParams invoiceParameters = PTIssuingParams.Util.newInstance();

		KeyGenerator gen = new KeyGenerator(App.PRIVATE_KEY_DIR);

		invoiceParameters.setPrivateKey(gen.getPrivateKey());
		invoiceParameters.setPublicKey(gen.getPublicKey());
		invoiceParameters.setInvoiceSeries("B");
		invoiceParameters.setPrivateKeyVersion("1");
		return invoiceParameters;
	}

	private PTCustomer createPtCustomer(BillyPortugal billyPortugal) {
		PTContact.Builder contactBuilder = billyPortugal.contacts().builder();
		contactBuilder.setName("Customer 1")
				.setTelephone("telephone 1");

		PTAddress.Builder addressBuilder = billyPortugal.addresses().builder();
		addressBuilder.setStreetName("Customer 1 street name 1")
				.setNumber("2")
				.setPostalCode("1000-100")
				.setCity("Lisbon")
				.setISOCountry("PT")
				.setDetails("Av. 5 de Outubro Nº 2 1000-100 Lisboa");

		PTCustomer.Builder customerBuilder = billyPortugal.customers().builder();
		customerBuilder.setName("Customer name 1")
				.setTaxRegistrationNumber("123456789", "PT")
				.addAddress(addressBuilder, true)
				.setBillingAddress(addressBuilder)
				.setShippingAddress(addressBuilder)
				.setHasSelfBillingAgreement(false)
				.addContact(contactBuilder);

		return billyPortugal.customers().persistence().create(customerBuilder);
	}

	private void exportSAFT(BillyPortugal billyPortugal,
                            PTApplication application,
                            PTBusiness business,
                            Date startDate,
                            Date endDate)
			throws SAFTPTExportException, IOException {
		billyPortugal.saft().export(application.getUID(),
				business.getUID(),
				application.getSoftwareCertificationNumber().toString(),
				startDate,
				endDate,
				"saft.xml",
				PTSAFTFileGenerator.SAFTVersion.CURRENT);
	}

	private void exportInvoicePDF(BillyPortugal billyPortugal,
                                  PTApplication application,
                                  PTInvoice invoice)
            throws IOException, ExportServiceException {
		InputStream xslInputStream = this.getClass().getResourceAsStream(Billy.INVOICE_XSL_PATH);

		InputStream logoStream = this.getClass().getResourceAsStream("/logoBig.png");
		Files.copy(logoStream, Paths.get(Billy.LOGO_PATH), StandardCopyOption.REPLACE_EXISTING);
		PTInvoiceTemplateBundle templateBundle = new PTInvoiceTemplateBundle(Billy.LOGO_PATH, xslInputStream,
				application.getSoftwareCertificationNumber().toString());

		String resultPath = new SimpleDateFormat("'invoice_'yyyy-MM-dd'T'hh:mm:ss'.pdf'").format(new Date());
		billyPortugal.invoices().pdfExport(
				new PTInvoicePDFExportRequest(invoice.getUID(), templateBundle, resultPath));
	}

	private void exportCreditNotePDF(BillyPortugal billyPortugal,
                                     PTApplication application,
                                     PTCreditNote creditNote)
            throws IOException, ExportServiceException {
		InputStream xslInputStream = this.getClass().getResourceAsStream(Billy.CREDIT_NOTE_XSL_PATH);

		InputStream logoStream = this.getClass().getResourceAsStream("/logoBig.png");
		Files.copy(logoStream, Paths.get(Billy.LOGO_PATH), StandardCopyOption.REPLACE_EXISTING);
		PTCreditNoteTemplateBundle templateBundle = new PTCreditNoteTemplateBundle(Billy.LOGO_PATH, xslInputStream,
				application.getSoftwareCertificationNumber().toString());

		String resultPath = new SimpleDateFormat("'creditNote_'yyyy-MM-dd'T'hh:mm:ss'.pdf'").format(new Date());
		billyPortugal.creditNotes().pdfExport(
				new PTCreditNotePDFExportRequest(creditNote.getUID(), templateBundle, resultPath));
	}

}
