package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CreateCustomerCLI {
	
	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	
	public void createCustomer() {
		String name;
		String taxNumber;
		
		try {
			System.out.println("Name: ");
			name = bufferReader.readLine();
			System.out.println("Tax Registration Number: ");
			taxNumber = bufferReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
