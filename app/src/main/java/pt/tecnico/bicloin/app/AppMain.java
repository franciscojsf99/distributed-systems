package pt.tecnico.bicloin.app;

import java.util.Scanner;

public class AppMain {
	
	public static void main(String[] args) {
		System.out.println(AppMain.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			//System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		
		// check arguments
		if (args.length < 6) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", AppMain.class.getName());
			return;
		}


		App myApp = new App();
		myApp.run(args);
	}

}
