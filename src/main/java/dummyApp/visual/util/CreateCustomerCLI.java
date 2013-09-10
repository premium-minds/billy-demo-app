package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.inject.Injector;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.exceptions.InvalidTaxIdentificationNumberException;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;

import dummyApp.app.AppManager;

public class CreateCustomerCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public CreateCustomerCLI(AppManager manager) {
		this.manager = manager;
	}

	public PTCustomer createCustomer() {
		String name;
		String taxNumber;
		String street;
		String number;
		String postalCode;
		String city;
		String telephone;

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

			PTCustomer customer = manager.createCustomer(name, taxNumber,
					street, number, postalCode, city, telephone);
			if (customer == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Customer: " + customer.getName() + " created.");
			return customer;
		}catch(InvalidTaxIdentificationNumberException e){
			System.out.println("[ERROR] An error ocurred with the tax identification number: " + e.toString());
		} 
		catch(BillyRuntimeException e){
			System.out.println("[ERROR] An error ocurred: " + e.toString());
		} 
		catch (IOException e) {
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		} catch (Exception e) {
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		}
		return null;
	}

}
