package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;

import dummyApp.app.AppManager;
import dummyApp.visual.DummyAppCLI;

public class ExportSAFT {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public ExportSAFT(AppManager manager) {
		this.manager = manager;
	}

	public void exportSAFT() {
		PTBusinessEntity business;
		String businessName, date;

		try {
			System.out.println("Business Name:");
			businessName = bufferReader.readLine();

			business = (PTBusinessEntity) manager.getAppCLI().getBusinessByName(businessName);

			if (business == null) {
				System.out.println("Business not found, create new? (y/n)");
				String answer = bufferReader.readLine();
				if (answer.toLowerCase().contains("y")) {
					business = (PTBusinessEntity) new CreateBusinessCLI(manager)
							.createBusiness();
					manager.getAppCLI().getBusinesses().add(business);
				} else {
					return;
				}
			}

			System.out.println("From date: (dd/mm/yyyy)");
			date = bufferReader.readLine();

			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date formattedDate = formatter.parse(date);

			manager.exportSaft(business, formattedDate, new Date());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAFTPTExportException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
