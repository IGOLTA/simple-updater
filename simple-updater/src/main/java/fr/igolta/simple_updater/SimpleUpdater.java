package fr.igolta.simple_updater;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SimpleUpdater {
	
	public static final HttpClient httpClient = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_2)
			.build();
	
	private JSONObject files;
	
	public SimpleUpdater(URI uri, File destination) throws IllegalArgumentException, ParseException, IOException, InterruptedException {
		if(destination.isDirectory()) {
			files = (JSONObject) new JSONParser().parse(download(uri));
		}else {
			throw new IllegalArgumentException("Destionation must be a directory");
		}
	}
	
	public static String download(URI uri) throws IOException, InterruptedException {
		return download(uri, "unknown");
	}
	
	public static String download(URI uri, String dataType) throws IOException, InterruptedException {
		
		HttpRequest request = HttpRequest.newBuilder()
    			.GET()
    			.uri(uri)
    			.setHeader("Data-Type", "json")
    			.build();
		
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
}
