package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import com.premiumminds.billy.portugal.persistence.entities.PTProductEntity;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTPayment;
import com.premiumminds.billy.portugal.services.entities.PTProduct;

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
		String productName;
		BigDecimal quantity, price;

		try {
			
		System.out.println("Product description:");
		productName = bufferReader.readLine();

		product = (PTProductEntity) getProductByDescription(productName);

		if (product == null) {
			System.out.println("Product not found, create new?");
			String answer = bufferReader.readLine();
			if (answer.toLowerCase().contains("y")) {
				product = (PTProductEntity) new CreateProductCLI(manager).createProduct();
			}
			else {
				return;
			}
		}
		
		System.out.println("Quantity:");
		quantity = new BigDecimal(bufferReader.readLine());
		System.out.println("Price:");
		price = new BigDecimal(bufferReader.readLine());
		

		PTInvoiceEntry.Builder entry = manager.createInvoiceEntry(quantity, price, product);
//		PTPayment.Builder payment = manager.createPayment()
//		
//		manager.createInvoice(entry, payment, business, customer);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return;
		}
	}

}
