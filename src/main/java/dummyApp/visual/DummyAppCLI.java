package dummyApp.visual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Injector;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;
import com.premiumminds.billy.portugal.services.entities.PTProduct;

import dummyApp.app.AppManager;
import dummyApp.visual.util.CreateBusinessCLI;
import dummyApp.visual.util.CreateCustomerCLI;
import dummyApp.visual.util.CreateInvoiceCLI;
import dummyApp.visual.util.CreateProductCLI;
import dummyApp.visual.util.ExportSAFT;

public class DummyAppCLI {

	Injector injector;
	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;
	List<PTProduct> products;
	List<PTCustomer> customers;
	List<PTBusiness> businesses;

	public DummyAppCLI() {
		products = new ArrayList<PTProduct>();
		customers = new ArrayList<PTCustomer>();
		businesses = new ArrayList<PTBusiness>();
	}

	public DummyAppCLI(Injector injector) {
		this.injector = injector;
		manager = new AppManager(injector);
		products = new ArrayList<PTProduct>();
		customers = new ArrayList<PTCustomer>();
		businesses = new ArrayList<PTBusiness>();
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
	
	public PTProduct getProductByDescription(String description) {
		for(PTProduct p : products) {
			if(p.getDescription().equals(description)) {
				return p;
			}
		}
		return null;
	}
	
	public PTBusiness getBusinessByName(String name) {
		for(PTBusiness b : businesses) {
			if(b.getName().equals(name)) {
			return b;
			}
		}
		return null;
	}
	
	public PTCustomer getCustomerByName(String name) {
		for(PTCustomer c : customers) {
			if(c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public void start() {
		CreateCustomerCLI createCustomerCLI = new CreateCustomerCLI(manager);
		CreateBusinessCLI createBusinessCLI = new CreateBusinessCLI(manager);
		CreateProductCLI createProductCLI = new CreateProductCLI(manager);
		CreateInvoiceCLI createInvoiceCLI = new CreateInvoiceCLI(manager);
		ExportSAFT exportSAFT = new ExportSAFT(manager);

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
						createInvoiceCLI.createInvoice();
						break;
					case 5:
						// Create Simple Invoice
						break;
					case 6:
						// Create Credit Note
						break;
					case 7:
						exportSAFT.exportSAFT();
						break;
					case 8:
						// Export PDF
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
