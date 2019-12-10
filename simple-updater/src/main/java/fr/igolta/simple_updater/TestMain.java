package fr.igolta.simple_updater;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.json.simple.parser.ParseException;

public class TestMain {

	public static void main(String[] args) {
		System.out.println("|-------------------------|\n"
						 + "|simple-updater test build|"
					   + "\n|-------------------------|"
		);
		
		File downloadFolder = new File(System.getProperty("user.home") + "/Desktop/Test");
		downloadFolder.mkdir();
		
		File testFile = new File(System.getProperty("user.home") + "/Desktop/Test/ag.txt");
		try {
			testFile.createNewFile();
		} catch (IOException e) {}
		
		try {
			 new SimpleUpdater(URI.create("http://localhost/simple_updater/index.php"), downloadFolder);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
