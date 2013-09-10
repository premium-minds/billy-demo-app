package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

		String description, productCode, unitOfMeasure;

		try {
			System.out.println("Description:");
			description = bufferReader.readLine();
			System.out.println("Product Code:");
			productCode = bufferReader.readLine();
			System.out.println("Unit of Measure:");
			unitOfMeasure = bufferReader.readLine();

			if (description.equals("")) {
				description = "Delta Café";
			}
			if (productCode.equals("")) {
				productCode = "56012345667";
			}
			if (unitOfMeasure.equals("")) {
				unitOfMeasure = "Kg";
			}

			PTProduct product = manager.createProduct(productCode, description,
					unitOfMeasure);

			if (product == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Product: " + product.getDescription()
					+ " created.");
			return product;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
