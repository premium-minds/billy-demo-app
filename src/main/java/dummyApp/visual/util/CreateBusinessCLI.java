package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.exceptions.InvalidTaxIdentificationNumberException;
import com.premiumminds.billy.portugal.services.entities.PTApplication;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;

import dummyApp.app.AppManager;

public class CreateBusinessCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public CreateBusinessCLI(AppManager manager) {
		this.manager = manager;
	}

	public PTBusiness createBusiness() {

		String name, taxNumber, street, number, postalCode, city, telephone, app;

		try {
			System.out.println("Name:");
			name = bufferReader.readLine();
			if (name.equals("")) {
				name = "Business";
				System.out.println("Name: " + name);
			}
			
			System.out.println("Tax Registration Number:");
			taxNumber = bufferReader.readLine();
			if (taxNumber.equals("")) {
				taxNumber = "500003564";
				System.out.println("Tax Number: " + taxNumber);
			}
			
			System.out.println("Street name:");
			street = bufferReader.readLine();
			if (street.equals("")) {
				street = "Via Ápia";
				System.out.println("Street: " + street);
			}
			
			System.out.println("Door number:");
			number = bufferReader.readLine();
			if (number.equals("")) {
				number = "999";
				System.out.println("Door Number: " + number);
			}
			
			System.out.println("Postal Code:");
			postalCode = bufferReader.readLine();
			if (postalCode.equals("")) {
				postalCode = "0000-001";
				System.out.println("Postal Code: " + postalCode);
			}
			
			System.out.println("City:");
			city = bufferReader.readLine();
			if (city.equals("")) {
				city = "Olissipo";
				System.out.println("Olissipo: " + city);
			}
			
			System.out.println("Telephone:");
			telephone = bufferReader.readLine();
			if (telephone.equals("")) {
				telephone = "978548961";
				System.out.println("Telephone: " + telephone);
			}
			
			System.out.println("Create App:");
			app = bufferReader.readLine();
			
			PTApplication.Builder appbuilder;

			if (app.equals("y")) {
				appbuilder = manager.createApplication();
			} else {
				appbuilder = manager.getApp();
			}
			
			PTBusiness business = manager.createBusiness(appbuilder, name,
					taxNumber, street, number, postalCode, city, telephone);
			if (business == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Business: " + business.getName() + " created.");
			return business;

		}catch(InvalidTaxIdentificationNumberException e){
			System.out.println("[ERROR] An error ocurred with the tax identification number: " + e.toString());
		} 
		catch(BillyRuntimeException e){
			System.out.println("[ERROR] An error ocurred: " + e.toString());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
