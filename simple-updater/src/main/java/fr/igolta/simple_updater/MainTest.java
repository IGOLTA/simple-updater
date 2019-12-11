package fr.igolta.simple_updater;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MainTest {
	public static void main(String[] args) {
		
		File downloadFolder = new File(System.getProperty("user.home") + "/desktop/Test/");
		
		try {
			new SimpleUpdater(URI.create("http://localhost/simple_updater/"), downloadFolder);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
