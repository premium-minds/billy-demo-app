package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTCustomerEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTProductEntity;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTPayment;

import dummyApp.app.AppManager;
import dummyApp.visual.DummyAppCLI;

public class CreateInvoiceCLI extends DummyAppCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public CreateInvoiceCLI(AppManager manager) {
		this.manager = manager;
	}

	@SuppressWarnings("finally")
	public void createInvoice() {
		PTProductEntity product;
		PTBusinessEntity business;
		PTCustomerEntity customer;
		String productName, businessName, customerName;
		BigDecimal quantity, price;

		try {
			System.out.println("Business Name:");
			businessName = bufferReader.readLine();

			business = (PTBusinessEntity) getBusinessByName(businessName);

			if (business == null) {
				System.out.println("Business not found, create new? (y/n)");
				String answer = bufferReader.readLine();
				if (answer.toLowerCase().contains("y")) {
					business = (PTBusinessEntity) new CreateBusinessCLI(manager)
							.createBusiness();
					getBusinesses().add(business);
				} else {
					return;
				}
			}
			
			System.out.println("Customer Name:");
			customerName = bufferReader.readLine();

			customer = (PTCustomerEntity) getCustomerByName(customerName);

			if (customer == null) {
				System.out.println("Customer not found, create new? (y/n)");
				String answer = bufferReader.readLine();
				if (answer.toLowerCase().contains("y")) {
					customer = (PTCustomerEntity) new CreateCustomerCLI(manager)
							.createCustomer();
					getCustomers().add(customer);
				} else {
					return;
				}
			}

			System.out.println("Product description:");
			productName = bufferReader.readLine();

			product = (PTProductEntity) getProductByDescription(productName);

			if (product == null) {
				System.out.println("Product not found, create new? (y/n)");
				String answer = bufferReader.readLine();
				if (answer.toLowerCase().contains("y")) {
					product = (PTProductEntity) new CreateProductCLI(manager)
							.createProduct();
					getProducts().add(product);
				} else {
					return;
				}
			}

			System.out.println("Quantity:");
			quantity = new BigDecimal(bufferReader.readLine());
			System.out.println("Price:");
			price = new BigDecimal(bufferReader.readLine());

			PTInvoiceEntry.Builder entry = manager.createInvoiceEntry(quantity,
					price, product);
			PTPayment.Builder payment = manager.createPayment(price
					.multiply(quantity));

			PTInvoice invoice = manager.createInvoice(entry, payment, business, customer);
			if (invoice == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Invoice: " + invoice.getNumber() + " created.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
