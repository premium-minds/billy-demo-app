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
				description = "Desconhecido";
			}
			if (productCode.equals("")) {
				productCode = "Desconhecido";
			}
			if (unitOfMeasure.equals("")) {
				unitOfMeasure = "Desconhecido";
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
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		} catch (Exception e) {
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		}
		return null;

	}
}
