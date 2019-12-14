package fr.igolta.simple_updater;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class MainTest {
	public static void main(String[] args) {
		
		File downloadFolder = new File(System.getProperty("user.home") + "/desktop/Test/");
		
		try {
			System.out.println(SimpleUpdater.downloadWithReq(new URL("http://localhost/simple_updater/")));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
