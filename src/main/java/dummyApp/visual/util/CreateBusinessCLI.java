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
			System.out.println("Create App:");
			app = bufferReader.readLine();
			PTApplication.Builder appbuilder;

			if (name.equals("")) {
				name = "Exemplo";
			}
			if (taxNumber.equals("")) {
				taxNumber = "123456789";
			}
			if (street.equals("")) {
				street = "Via Ápia";
			}
			if (number.equals("")) {
				number = "999";
			}
			if (postalCode.equals("")) {
				postalCode = "0000-001";
			}
			if (city.equals("")) {
				city = "Olissipo";
			}
			if (telephone.equals("")) {
				telephone = "978548961";
			}
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
