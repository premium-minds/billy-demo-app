package dummyApp.app;

import java.io.IOException;
import java.net.MalformedURLException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.BillyPortugal;
import com.premiumminds.billy.portugal.PortugalBootstrap;
import com.premiumminds.billy.portugal.PortugalDependencyModule;
import com.premiumminds.billy.portugal.PortugalPersistenceDependencyModule;
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;

import dummyApp.visual.DummyAppCLI;

/**
 * Hello world!
 * 
 */
public class App {
	public static final String PRIVATE_KEY_DIR = "/private.pem";
	public static BillyPortugal billyPortugal;
	public static Injector injector;

	public static void main(String[] args) throws MalformedURLException,
			DocumentIssuingException, IOException, SAFTPTExportException {

		injector = Guice.createInjector(new PortugalDependencyModule(),
				new PortugalPersistenceDependencyModule("application-persistence-unit"));
		injector.getInstance(PortugalDependencyModule.Initializer.class);
		injector.getInstance(PortugalPersistenceDependencyModule.Initializer.class);
		PortugalBootstrap.execute(injector);
		
		DummyAppCLI cli = new DummyAppCLI(injector);
		cli.start();

		System.exit(0);
	}
}
