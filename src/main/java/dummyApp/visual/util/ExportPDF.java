package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.premiumminds.billy.portugal.services.entities.PTCreditNote;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;

import dummyApp.app.AppManager;


public class ExportPDF {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public ExportPDF(AppManager manager) {
		this.manager = manager;
	}

	public void exportPDF() {
		
		try {
		System.out.println("Financial documents:");
		for(PTInvoice i : manager.getAppCLI().getInvoices()) {
			System.out.println(i.getNumber());
		}
		for(PTSimpleInvoice si : manager.getAppCLI().getSimpleInvoices()) {
			System.out.println(si.getNumber());
		}
		for(PTCreditNote cn : manager.getAppCLI().getCreditNotes()) {
			System.out.println(cn.getNumber());
		}
		System.out.println("Insert document name to export:");
		String document;
			document = bufferReader.readLine();
		
		if (document.contains("FT")) {
			manager.exportInvoicePDF(manager.getAppCLI().getInvoiceByNumber(document).getUID());
		} else if (document.contains("FS")) {
			manager.exportSimpleInvoicePDF(manager.getAppCLI().getSimpleInvoiceByNumber(document).getUID());
		} else if (document.contains("NC")){
			manager.exportCreditNotePDF(manager.getAppCLI().getcreditNoteByNumber(document).getUID());
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
