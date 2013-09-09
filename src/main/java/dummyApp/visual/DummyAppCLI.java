package dummyApp.visual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Injector;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;
import com.premiumminds.billy.portugal.services.entities.PTCreditNote;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;

import dummyApp.app.AppManager;
import dummyApp.visual.util.CreateBusinessCLI;
import dummyApp.visual.util.CreateCreditNoteCLI;
import dummyApp.visual.util.CreateCustomerCLI;
import dummyApp.visual.util.CreateInvoiceCLI;
import dummyApp.visual.util.CreateProductCLI;
import dummyApp.visual.util.CreateSimpleInvoiceCLI;
import dummyApp.visual.util.ExportPDF;
import dummyApp.visual.util.ExportSAFT;

public class DummyAppCLI {

	Injector injector;
	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;
	List<PTProduct> products;
	List<PTCustomer> customers;
	List<PTBusiness> businesses;
	List<PTInvoice> invoices;
	List<PTSimpleInvoice> simpleInvoices;
	List<PTCreditNote> creditNotes;

	public DummyAppCLI() {
		products = new ArrayList<PTProduct>();
		customers = new ArrayList<PTCustomer>();
		businesses = new ArrayList<PTBusiness>();
		invoices = new ArrayList<PTInvoice>();
		simpleInvoices = new ArrayList<PTSimpleInvoice>();
		creditNotes = new ArrayList<PTCreditNote>();
	}

	public DummyAppCLI(Injector injector) {
		this.injector = injector;
		manager = new AppManager(injector);
		products = new ArrayList<PTProduct>();
		customers = new ArrayList<PTCustomer>();
		businesses = new ArrayList<PTBusiness>();
		invoices = new ArrayList<PTInvoice>();
		simpleInvoices = new ArrayList<PTSimpleInvoice>();
		creditNotes = new ArrayList<PTCreditNote>();
		manager.setAppCLI(this);
	}

	public List<PTCustomer> getCustomers() {
		return customers;
	}

	public List<PTProduct> getProducts() {
		return products;
	}

	public List<PTBusiness> getBusinesses() {
		return businesses;
	}

	public List<PTInvoice> getInvoices() {
		return invoices;
	}

	public List<PTSimpleInvoice> getSimpleInvoices() {
		return simpleInvoices;
	}
	
	public List<PTCreditNote> getCreditNotes() {
		return creditNotes;
	}

	public PTProduct getProductByDescription(String description) {
		for (PTProduct p : products) {
			if (p.getDescription().equals(description)) {
				return p;
			}
		}
		return null;
	}

	public PTBusiness getBusinessByName(String name) {
		for (PTBusiness b : businesses) {
			if (b.getName().equals(name)) {
				return b;
			}
		}
		return null;
	}

	public PTCustomer getCustomerByName(String name) {
		for (PTCustomer c : customers) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public PTInvoice getInvoiceByNumber(String number) {
		for (PTInvoice i : invoices) {
			if (i.getNumber().equals(number)) {
				return i;
			}
		}
		return null;
	}

	public PTSimpleInvoice getSimpleInvoiceByNumber(String number) {
		for (PTSimpleInvoice si : simpleInvoices) {
			if (si.getNumber().equals(number)) {
				return si;
			}
		}
		return null;
	}
	
	public PTCreditNote getcreditNoteByNumber(String number) {
		for (PTCreditNote cn : creditNotes) {
			if (cn.getNumber().equals(number)) {
				return cn;
			}
		}
		return null;
	}

	public void start() {
		CreateCustomerCLI createCustomerCLI = new CreateCustomerCLI(manager);
		CreateBusinessCLI createBusinessCLI = new CreateBusinessCLI(manager);
		CreateProductCLI createProductCLI = new CreateProductCLI(manager);
		CreateInvoiceCLI createInvoiceCLI = new CreateInvoiceCLI(manager);
		CreateSimpleInvoiceCLI createSimpleInvoiceCLI = new CreateSimpleInvoiceCLI(
				manager);
		CreateCreditNoteCLI createCreditNoteCLI = new CreateCreditNoteCLI(
				manager);
		ExportSAFT exportSAFT = new ExportSAFT(manager);
		ExportPDF exportPDF = new ExportPDF(manager);

		while (true) {
			System.out.println("Press number key and return to:");
			System.out.println("1: Create Customer");
			System.out.println("2: Create Business");
			System.out.println("3: Create Product");
			System.out.println("4: Create Invoice");
			System.out.println("5: Create Simple Invoice");
			System.out.println("6: Create Credit Note");
			System.out.println("7: Export SAFT");
			System.out.println("8: Export PDF");
			System.out.println("0: Exit");

			try {
				int choice = Integer.parseInt(bufferReader.readLine());

				switch (choice) {
					case 1:
						PTCustomer c;
						if ((c = createCustomerCLI.createCustomer()) != null) {
							customers.add(c);
						}
						break;
					case 2:
						PTBusiness b;
						if ((b = createBusinessCLI.createBusiness()) != null) {
							businesses.add(b);
						}
						break;
					case 3:
						PTProduct p;
						if ((p = createProductCLI.createProduct()) != null) {
							products.add(p);
						}
						break;
					case 4:
						PTInvoice i;
						if ((i = createInvoiceCLI.createInvoice()) != null) {
							invoices.add(i);
						}
						break;
					case 5:
						PTSimpleInvoice si;
						if ((si = createSimpleInvoiceCLI.createInvoice()) != null) {
							simpleInvoices.add(si);
						}
						break;
					case 6:
						PTCreditNote cn;
						if((cn = createCreditNoteCLI.createCreditNote()) != null){
							creditNotes.add(cn);
						}
						break;
					case 7:
						exportSAFT.exportSAFT();
						break;
					case 8:
						exportPDF.exportPDF();
						break;
					case 0:
						return;

					default:
						System.out.println("Invalid option");
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.println("Invalid option");
			}

		}

	}

}
