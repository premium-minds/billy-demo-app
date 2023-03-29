package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.premiumminds.billy.core.services.StringID;
import com.premiumminds.billy.core.services.entities.Tax;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
import dummyApp.app.AppManager;

public class CreateProductCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public CreateProductCLI(AppManager manager) {
		this.manager = manager;
	}

	public PTProduct createProduct() {

		String description, productCode, unitOfMeasure, iva;

		try {
			System.out.println("Description:");
			description = bufferReader.readLine();
			if (description.equals("")) {
				description = "Product";
				System.out.println("Description: " + description);
			}
			
			System.out.println("Product Code:");
			productCode = bufferReader.readLine();
			if (productCode.equals("")) {
				productCode = "1";
				System.out.println("Product Code: " + productCode);
			}
			
			System.out.println("Unit of Measure:");
			unitOfMeasure = bufferReader.readLine();
			if (unitOfMeasure.equals("")) {
				unitOfMeasure = "Kg";
				System.out.println("Unit of Measure: " + unitOfMeasure);
			}
			
			System.out.println("IVA: (6/13/23)");
			iva = bufferReader.readLine();			
			if(iva.equals("")) {
				iva = "23";
				System.out.println("IVA: " + iva);
			}
			
			StringID<Tax> tax;
			if(iva.equals("23")) {
				tax = manager.getTaxes().continent().normal().getUID();
			} else if(iva.equals("13")) {
				tax = manager.getTaxes().continent().intermediate().getUID();
			} else {
				tax = manager.getTaxes().continent().reduced().getUID();
			}

			PTProduct product = manager.createProduct(productCode, description,
					unitOfMeasure, tax);

			if (product == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Product: " + product.getDescription()
					+ " created.");
			return product;

		} catch (IOException e) {
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		} catch (Exception e) {
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		}
		return null;

	}
}
