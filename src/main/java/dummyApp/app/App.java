package dummyApp.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.premiumminds.billy.portugal.PortugalBootstrap;
import com.premiumminds.billy.portugal.PortugalDependencyModule;
import com.premiumminds.billy.portugal.PortugalPersistenceDependencyModule;
import dummyApp.visual.DummyAppCLI;

public class App {
	public static final String PRIVATE_KEY_DIR = "/private.pem";
	public static Injector injector;

	public static void main(String[] args) throws Exception {

		injector = Guice.createInjector(new PortugalDependencyModule(),
				new PortugalPersistenceDependencyModule("application-persistence-unit"));
		injector.getInstance(PortugalDependencyModule.Initializer.class);
		injector.getInstance(PortugalPersistenceDependencyModule.Initializer.class);
		PortugalBootstrap.execute(injector);

		if (args.length > 1 || (args.length == 1 &&  args[0] == "demo")){
			System.out.println("Usage: [demo]");
			return;
		} else if(args.length == 1){
			DemoApp demo = new DemoApp(injector);
			demo.run();
		} else {
			DummyAppCLI cli = new DummyAppCLI(injector);
			cli.start();
		}

		System.exit(0);
	}
}
