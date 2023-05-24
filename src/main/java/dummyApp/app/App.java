package dummyApp.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.premiumminds.billy.portugal.PortugalBootstrap;
import com.premiumminds.billy.portugal.PortugalDependencyModule;
import com.premiumminds.billy.spain.SpainBootstrap;
import com.premiumminds.billy.spain.SpainDependencyModule;
import dummyApp.visual.DummyAppCLI;

public class App {
	public static final String PRIVATE_KEY_DIR = "/private.pem";
	public static Injector injector;

	public static void main(String[] args) throws Exception {

		injector = Guice.createInjector(new PortugalDependencyModule(),
				new SpainDependencyModule(),
				new JpaPersistModule("application-persistence-unit"));
		injector.getInstance(PersistService.class).start();
		PortugalBootstrap.execute(injector);
		SpainBootstrap.execute(injector);

		if (args.length == 1 || (args.length == 2 && !args[0].equals("demo")) || args.length > 2){
			usage();
			return;
		} else if(args.length == 2){
			if (args[1].equals("portugal")){
				PortugalDemoApp demo = new PortugalDemoApp(injector);
				demo.run();
			} else if (args[1].equals("spain")){
				SpainDemoApp demo = new SpainDemoApp(injector);
				demo.run();
			} else {
				System.out.println("unknown " + args[1]);
				usage();
			}
		} else {
			DummyAppCLI cli = new DummyAppCLI(injector);
			cli.start();
		}

		System.exit(0);
	}

	private static void usage() {
		System.out.println("Usage: [demo portugal|spain]");
	}
}
