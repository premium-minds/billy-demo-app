package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;

import dummyApp.app.AppManager;
import dummyApp.visual.DummyAppCLI;

public class ExportSAFT extends DummyAppCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public ExportSAFT(AppManager manager) {
		this.manager = manager;
	}

	public void exportSAFT() {
		PTBusinessEntity business;
		String businessName, year, month, day;

		try {
			System.out.println("Business Name:");
			businessName = bufferReader.readLine();

			business = (PTBusinessEntity) getBusinessByName(businessName);

			if (business == null) {
				System.out.println("Business not found, create new?");
				String answer = bufferReader.readLine();
				if (answer.toLowerCase().contains("y")) {
					business = (PTBusinessEntity) new CreateBusinessCLI(manager)
							.createBusiness();
					getBusinesses().add(business);
				} else {
					return;
				}
			}

			System.out.println("From date:");
			System.out.println("year");
			year = bufferReader.readLine();
			System.out.println("month");
			month = bufferReader.readLine();
			System.out.println("day");
			day = bufferReader.readLine();

			Calendar calendar = Calendar.getInstance();
			calendar.set(Integer.parseInt(year), Integer.parseInt(month),
					Integer.parseInt(day));

			manager.exportSaft(business, calendar.getTime(), new Date());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAFTPTExportException e) {
			e.printStackTrace();
		}
	}
}
