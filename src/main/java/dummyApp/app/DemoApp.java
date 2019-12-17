package dummyApp.app;

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
import com.premiumminds.billy.portugal.services.entities.PTCustomer;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;
import com.premiumminds.billy.portugal.services.export.pdf.invoice.PTInvoicePDFExportRequest;
import com.premiumminds.billy.portugal.services.export.pdf.invoice.PTInvoiceTemplateBundle;
import com.premiumminds.billy.portugal.services.export.saftpt.PTSAFTFileGenerator;
import com.premiumminds.billy.portugal.util.KeyGenerator;
import dummyApp.persistence.Billy;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

public class DemoApp {
    private final Injector injector;

    public DemoApp(Injector injector) {
        this.injector = injector;
    }

    public void run() throws IOException, ParseException, DocumentIssuingException, SAFTPTExportException, ExportServiceException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        BillyPortugal billyPortugal = new BillyPortugal(injector);

        PTApplication.Builder applicationBuilder = billyPortugal.applications().builder();

        applicationBuilder.setDeveloperCompanyName("Premium Minds")
                .setDeveloperCompanyTaxIdentifier("12345789")
                .setSoftwareCertificationNumber(123)
                .setName("Billy")
                .setVersion("1.0")
                .setApplicationKeysPath(new URL("http://www.keys.path"));

        PTContact.Builder contactBuilder = billyPortugal.contacts().builder();
        contactBuilder.setName("Bob Conner")
                .setTelephone("219211231");

        applicationBuilder.addContact(contactBuilder)
                .setMainContact(contactBuilder);

        PTApplication application = billyPortugal.applications().persistence().create(applicationBuilder);

        PTInvoice.Builder invoiceBuilder = billyPortugal.invoices().builder();

        Date date1 = dateFormat.parse("01-03-2013");
        invoiceBuilder.setSelfBilled(false)
                .setCancelled(false)
                .setBilled(false)
                .setDate(date1)
                .setSourceId("Source ID")
                .setSourceBilling(PTGenericInvoice.SourceBilling.P);

        PTBusiness.Builder businessBuilder = billyPortugal.businesses().builder();

        businessBuilder.addApplication(applicationBuilder)
                .addContact(contactBuilder, true)
                .setMainContactUID(contactBuilder.build().getUID())
                .setName("Business")
                .setCommercialName("Business, INC")
                .setFinancialID("500003564", "PT");

        PTAddress.Builder addressBuilder = billyPortugal.addresses().builder();

        addressBuilder.setStreetName("Av. 5 de Outubro")
                .setNumber("2")
                .setPostalCode("1000-100")
                .setCity("Lisbon")
                .setISOCountry("Portugal")
                .setDetails("Av. 5 de Outubro Nº 2 1000-100 Lisboa");

        businessBuilder.setAddress(addressBuilder)
                .setBillingAddress(addressBuilder);

        PTBusiness business = billyPortugal.businesses().persistence().create(businessBuilder);


        PTCustomer.Builder customerBuilder = billyPortugal.customers().builder();

        customerBuilder.setName("John Conner")
                .setTaxRegistrationNumber("123456789", "PT")
                .addAddress(addressBuilder, true)
                .setBillingAddress(addressBuilder)
                .setShippingAddress(addressBuilder)
                .setHasSelfBillingAgreement(false)
                .addContact(contactBuilder);

        PTCustomer customer = billyPortugal.customers().persistence().create(customerBuilder);

        invoiceBuilder.setBusinessUID(business.getUID())
                .setCustomerUID(customer.getUID());

        PTInvoiceEntry.Builder entryBuilder = billyPortugal.invoices().entryBuilder();

        entryBuilder.setAmountType(GenericInvoiceEntryBuilder.AmountType.WITH_TAX)
                .setCurrency(Currency.getInstance("EUR"))
                .setContextUID(billyPortugal.contexts().portugal().allRegions().getUID())
                .setQuantity(new BigDecimal("10"))
                .setTaxPointDate(dateFormat.parse("01-02-2013"))
                .setUnitAmount(GenericInvoiceEntryBuilder.AmountType.WITH_TAX, new BigDecimal("100"));

        PTProduct.Builder productBuilder = billyPortugal.products().builder();

        productBuilder.setDescription("product 1")
                .setNumberCode("1")
                .setProductCode("1")
                .setType(Product.ProductType.GOODS)
                .setUnitOfMeasure("kg")
                .addTaxUID(billyPortugal.taxes().continent().normal().getUID());

        PTProduct product = billyPortugal.products().persistence().create(productBuilder);

        entryBuilder.setProductUID(product.getUID())
                .setDescription(product.getDescription())
                .setUnitOfMeasure(product.getUnitOfMeasure());

        invoiceBuilder.addEntry(entryBuilder);

        PTIssuingParams invoiceParameters = PTIssuingParams.Util.newInstance();

        KeyGenerator gen = new KeyGenerator(App.PRIVATE_KEY_DIR);

        invoiceParameters.setPrivateKey(gen.getPrivateKey());
        invoiceParameters.setPublicKey(gen.getPublicKey());
        invoiceParameters.setInvoiceSeries("New Series");

        PTInvoice invoice = billyPortugal.invoices().issue(invoiceBuilder, invoiceParameters);

        Date startDate = dateFormat.parse("01-01-2013");
        Date endDate = dateFormat.parse("01-01-2014");

        billyPortugal.saft().export(application.getUID(),
                business.getUID(),
                Billy.SOFTWARE_CERTIFICATION,
                startDate,
                endDate,
                "saft.xml",
                PTSAFTFileGenerator.SAFTVersion.CURRENT);

        InputStream xslInputStream = this.getClass().getResourceAsStream(Billy.INVOICE_XSL_PATH);

        InputStream logoStream = this.getClass().getResourceAsStream("/logoBig.png");
        Files.copy(logoStream, Paths.get(Billy.LOGO_PATH), StandardCopyOption.REPLACE_EXISTING);
        PTInvoiceTemplateBundle templateBundle = new PTInvoiceTemplateBundle(Billy.LOGO_PATH, xslInputStream,
                Billy.SOFTWARE_CERTIFICATION);

        String resultPath = new SimpleDateFormat("'invoice_'yyyy-MM-dd'T'hh:mm:ss'.pdf'").format(new Date());
        billyPortugal.invoices().pdfExport(
                new PTInvoicePDFExportRequest(invoice.getUID(), templateBundle, resultPath));
    }
}
