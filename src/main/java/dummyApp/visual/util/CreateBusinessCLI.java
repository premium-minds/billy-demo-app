package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.premiumminds.billy.portugal.services.entities.PTBusiness;

import dummyApp.app.AppManager;

public class CreateBusinessCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public CreateBusinessCLI(AppManager manager) {
		this.manager = manager;
	}

	@SuppressWarnings("finally")
	public void createBusiness() {
		
		String name, taxNumber, street, number, postalCode, city, telephone;
		
		try {
			System.out.println("Name:");
			name = bufferReader.readLine();
			System.out.println("Tax Registration Number:");
			taxNumber = bufferReader.readLine();
			System.out.println("Street name:");
			street = bufferReader.readLine();
			System.out.println("Door number:");
			number = bufferReader.readLine();
			System.out.println("Postal Code:");
			postalCode = bufferReader.readLine();
			System.out.println("City:");
			city = bufferReader.readLine();
			System.out.println("Telephone:");
			telephone = bufferReader.readLine();
			
			PTBusiness business = manager.createBusiness(name, taxNumber, street, number, postalCode, city, telephone);
			if(business == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Business: " + business.getName() + " created.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return;
		}

	}
}
