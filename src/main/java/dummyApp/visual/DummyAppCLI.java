package dummyApp.visual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dummyApp.visual.util.CreateCustomerCLI;

public class DummyAppCLI {

	public void start() {
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
				System.in));
		CreateCustomerCLI createCustomerCLI = new CreateCustomerCLI();

		while (true) {
			System.out.println("Press number key and return to:");
			System.out.println("1: Create Customer");
			System.out.println("2: Create Business");
			System.out.println("3: Create Product");
			System.out.println("4: Create Invoice");
			System.out.println("5: Create Simple Invoice");
			System.out.println("6: Create Credit Note");
			System.out.println("7: Export SAFT");
			System.out.println("8: Export PDF");
			System.out.println("0: Exit");

			try {
				int choice = Integer.parseInt(bufferReader.readLine());

				switch (choice) {
					case 1:
						createCustomerCLI.createCustomer();
						break;
					case 2:
						// Create business
						break;
					case 3:
						// Create product
						break;
					case 4:
						// Create Invoice
						break;
					case 5:
						// Create Simple Invoice
						break;
					case 6:
						// Create Credit Note
						break;
					case 7:
						// Export SAFT
						break;
					case 8:
						// Export PDF
						break;
					case 0:
						return;

					default:
						System.out.println("Invalid option");
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.println("Invalid option");
			}

		}

	}

}
