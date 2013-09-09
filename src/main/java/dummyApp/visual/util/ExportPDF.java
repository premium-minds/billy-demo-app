package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import dummyApp.app.AppManager;


public class ExportPDF {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public ExportPDF(AppManager manager) {
		this.manager = manager;
	}

	public void exportPDF() {
		
	}
}
