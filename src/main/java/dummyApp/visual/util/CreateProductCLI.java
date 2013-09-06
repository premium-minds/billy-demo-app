package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dummyApp.app.AppManager;


public class CreateProductCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;
	
	public CreateProductCLI(AppManager manager) {
		this.manager = manager;
	}
	
	@SuppressWarnings("finally")
	public void createProduct() {
		String description, productCode, unitOfMeasure;
		
		try {
			System.out.println("Description:");
			description = bufferReader.readLine();
			System.out.println("Product Code:");
			productCode = bufferReader.readLine();
			System.out.println("Unit of Measure:");
			unitOfMeasure = bufferReader.readLine();
//			PTProduct product = (PTProduct) manager.createProduct();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return;
		}
		
	}
}
