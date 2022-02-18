package dummyApp.persistence;

import com.google.inject.Injector;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.gin.services.exceptions.ExportServiceException;
import com.premiumminds.billy.portugal.BillyPortugal;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;
import com.premiumminds.billy.portugal.services.entities.PTCreditNote;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;
import com.premiumminds.billy.portugal.services.export.pdf.creditnote.PTCreditNotePDFExportRequest;
import com.premiumminds.billy.portugal.services.export.pdf.creditnote.PTCreditNoteTemplateBundle;
import com.premiumminds.billy.portugal.services.export.pdf.invoice.PTInvoicePDFExportRequest;
import com.premiumminds.billy.portugal.services.export.pdf.invoice.PTInvoiceTemplateBundle;
import com.premiumminds.billy.portugal.services.export.pdf.simpleinvoice.PTSimpleInvoicePDFExportRequest;
import com.premiumminds.billy.portugal.services.export.pdf.simpleinvoice.PTSimpleInvoiceTemplateBundle;
import com.premiumminds.billy.portugal.services.export.saftpt.PTSAFTFileGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Date;

public class Billy {

	public static final Integer SOFTWARE_CERTIFICATION = 12;
	public static final String INVOICE_XSL_PATH = "/templates/pt_invoice.xsl";
	public static final String CREDIT_NOTE_XSL_PATH = "/templates/pt_creditnote.xsl";
	public static final String SIMPLE_INVOICE_XSL_PATH = "/templates/pt_simpleinvoice.xsl";
	public static final String RESULT_PATH = System
			.getProperty("java.io.tmpdir") + "/Result.pdf";
	public static final String LOGO_PATH = System
			.getProperty("java.io.tmpdir") + "/logoBig.png";
	public Injector injector;
	public BillyPortugal billyPortugal;

	public Billy(Injector injector, BillyPortugal billyPortugal) {
		this.injector = injector;
		this.billyPortugal = billyPortugal;
	}

	public void persistCustomer(PTCustomer.Builder customer) {
		billyPortugal.customers().persistence().create(customer);
	}

	public void persistBusiness(PTBusiness.Builder business) {
		billyPortugal.businesses().persistence().create(business);
	}

	public void persistProduct(PTProduct.Builder product) {
		billyPortugal.products().persistence().create(product);
	}

	public void issueInvoice(PTInvoice.Builder invoice, PTIssuingParams params)
			throws DocumentIssuingException {
		billyPortugal.invoices().issue(invoice, params);
	}

	public void issueSimpleInvoice(PTSimpleInvoice.Builder simpleInvoice,
			PTIssuingParams params) throws DocumentIssuingException {
		billyPortugal.simpleInvoices().issue(simpleInvoice, params);
	}

	public void issueCreditNote(PTCreditNote.Builder creditNote,
			PTIssuingParams params) throws DocumentIssuingException {
		billyPortugal.creditNotes().issue(creditNote, params);
	}

	public InputStream exportSaft(UID appUID, UID businessUID, Date from,
			Date to) throws IOException, SAFTPTExportException {
		return billyPortugal.saft().export(appUID, businessUID, from, to,
				PTSAFTFileGenerator.SAFTVersion.CURRENT);
	}

	public InputStream exportInvoicePDF(UID invoiceUID) throws ExportServiceException {
		InputStream xsl = this.getClass().getResourceAsStream(Billy.INVOICE_XSL_PATH);
		PTInvoiceTemplateBundle bundle = new PTInvoiceTemplateBundle(
				Billy.LOGO_PATH, xsl, SOFTWARE_CERTIFICATION.toString());
		return billyPortugal.invoices().pdfExport(
				new PTInvoicePDFExportRequest(invoiceUID, bundle, RESULT_PATH));
	}

	public InputStream exportSimpleInvoicePDF(UID simpleInvoiceUID) throws ExportServiceException {
		InputStream xsl = this.getClass().getResourceAsStream(Billy.SIMPLE_INVOICE_XSL_PATH);
		PTSimpleInvoiceTemplateBundle bundle = new PTSimpleInvoiceTemplateBundle(
				Billy.LOGO_PATH, xsl, SOFTWARE_CERTIFICATION.toString());
		return billyPortugal.simpleInvoices().pdfExport(
				new PTSimpleInvoicePDFExportRequest(simpleInvoiceUID, bundle,
						RESULT_PATH));
	}

	public InputStream exportCreditNotePDF(UID invoiceUID) throws ExportServiceException {
		InputStream xsl = this.getClass().getResourceAsStream(Billy.CREDIT_NOTE_XSL_PATH);
		PTCreditNoteTemplateBundle bundle = new PTCreditNoteTemplateBundle(
				Billy.LOGO_PATH, xsl, SOFTWARE_CERTIFICATION.toString());
		return billyPortugal.creditNotes().pdfExport(
				new PTCreditNotePDFExportRequest(invoiceUID, bundle,
						RESULT_PATH));
	}

	public PTCustomer endConsumer() {
		return billyPortugal.customers().endConsumer();
	}
}
