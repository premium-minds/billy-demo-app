package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

	@SuppressWarnings("finally")
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
				name = "Desconhecido";
			}
			if (taxNumber.equals("")) {
				taxNumber = "123456789";
			}
			if (street.equals("")) {
				street = "Desconhecido";
			}
			if (number.equals("")) {
				number = "Desconhecido";
			}
			if (postalCode.equals("")) {
				postalCode = "Desconhecido";
			}
			if (city.equals("")) {
				city = "Desconhecido";
			}
			if (telephone.equals("")) {
				telephone = "Desconhecido";
			}
			if (app.equals("y")) {
				appbuilder = manager.createApplication();
			} else {
				appbuilder = manager.getApp();
			}

			PTBusiness business = manager.createBusiness(appbuilder, name, taxNumber,
					street, number, postalCode, city, telephone);
			if (business == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Business: " + business.getName() + " created.");
			return business;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return null;
		}

	}
}
