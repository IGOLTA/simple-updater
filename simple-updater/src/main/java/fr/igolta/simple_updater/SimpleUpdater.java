package fr.igolta.simple_updater;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SimpleUpdater {
	
	public static final String rootFileName = "files";
	
	public static final HttpClient httpClient = HttpClients.createDefault();
	
	private URI serverURI;
	private File destination;
	private JSONObject jsonData;
	private HashMap<File, String> localFiles;
	private HashMap<File, String> serverFiles = new HashMap<File, String>();
	private ArrayList<File> filesToDelete = new ArrayList<File>();;
	private ArrayList<File> filesToDownload = new ArrayList<File>();;
	
	@SuppressWarnings("unchecked")
	public SimpleUpdater(URI uri, File destination) throws IOException, InterruptedException, IllegalArgumentException {
		if(destination.isDirectory()) {
			this.destination = destination;
			this.serverURI = uri;
			
			log("Downloading checksums and paths...");
			
			try {
				jsonData = (JSONObject) new JSONParser().parse(downloadWithReq(uri.toURL()));
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			}
			
			localFiles = listAndCheckFiles(destination);
			
			for(Entry<String, String> entry:((HashMap<String, String>) jsonData.get("checksums")).entrySet()) {
				String finalPath = entry.getKey();
				
				finalPath = finalPath.replaceFirst(rootFileName, destination.getAbsolutePath().replace("\\", "\\\\"));
				
				serverFiles.put(new File(finalPath), entry.getValue());
			}
			
			log("Listing files...");
			
			for(Entry<File, String> entry:localFiles.entrySet()) {
				if(!containsFileSame(entry, serverFiles)) {
					filesToDelete.add(entry.getKey());
				}
			}
			
			for(Entry<File, String> entry:serverFiles.entrySet()) {
				if(!containsFileSame(entry, localFiles)) {
					filesToDownload.add(entry.getKey());
				}
			}
			
			if(filesToDelete.size() > 0) {
				log("Deleting files ...");
				for(File f:filesToDelete) {
					log("Deleting " + f.getAbsolutePath());
					
					f.delete();
				}
			}else {
				log("Nothing to delete");
			}
			
			if(filesToDownload.size() > 0) {
				log("Downloading files ...");
				for(File f:filesToDownload) {
					f.getParentFile().mkdirs();
					
					log("Downloading " + getServerPath(f) + " at " + f.getAbsolutePath() + "...");
					
					downloadToFile(getServerPath(f).toURL(), f);
				}
			}else {
				log("Nothing to download");
			}
			
			log("Update finished");
		}else {
			throw new IllegalArgumentException("Destionation must be a directory");
		}
	}
	
	private URI getServerPath(File f) {
		String serverPath = destination.toURI().relativize(f.toURI()).getPath();;
		
		serverPath = rootFileName + "/" + serverPath;
		
		return URI.create(serverURI.toASCIIString() + serverPath.replace(" ", "%20"));
	}
	
	private static void log(String m) {
		System.out.println("[simple-updater] " + m);
	}
	
	private static boolean containsFileSame(Map.Entry<File, String> entry, HashMap<File, String> ref) {
		
		for(Entry<File, String> refEntry:ref.entrySet()) {
			if(refEntry.getKey().getAbsolutePath().equals(entry.getKey().getAbsolutePath())) {
				if(refEntry.getValue().equals(entry.getValue())) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static HashMap<File, String> listAndCheckFiles(File dir) throws IllegalArgumentException, IOException {
		if(dir.isDirectory()) {
			HashMap<File, String> files = new HashMap<File, String>();
			
			for(File file:listFiles(dir)) {
				files.put(file, generateChecksumFor(file));
			}
			
			return files;
		}else {
			throw new IllegalArgumentException("Folder must be a directory");
		}
	}
	
	public static String generateChecksumFor(File file) throws IllegalArgumentException, IOException {
		if(file.isFile()) {
			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("MD5");
				FileInputStream fis = new FileInputStream(file);
				byte[] byteArray = new byte[1024];
			    int bytesCount = 0; 
			    
			    while ((bytesCount = fis.read(byteArray)) != -1) {
			        digest.update(byteArray, 0, bytesCount);
			    };
			    
			    fis.close();
			    
			    byte[] bytes = digest.digest();
			    
			    StringBuilder sb = new StringBuilder();
			    for(int i=0; i< bytes.length ;i++)
			    {
			        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			    }
			    
			    return sb.toString();
			} catch (NoSuchAlgorithmException e) {
				return "";
			}
		}else {
			throw new IllegalArgumentException("File must be a file");
		}
	}
	
	public static ArrayList<File> listFiles(File dir) throws IOException {
		if(dir.isDirectory()) {
			ArrayList<File> subdirs  = new ArrayList<File>();
			ArrayList<File> files = new ArrayList<File>();
			
			for(File file:dir.listFiles()) {
				if(file.isFile()) {
					files.add(file);
				}else if(file.isDirectory()) {
					subdirs.add(file);
				}
			}
			
			while(subdirs.size() > 0) {
				for(File file: subdirs.get(0).listFiles()) {
					if(file.isFile()) {
						files.add(file);
					}else if(file.isDirectory()) {
						subdirs.add(file);
					}
				}
				
				subdirs.remove(0);
			}
			
			return files;
		}else {
			throw new IllegalArgumentException("Folder must be a directory");
		}
	}
	
	public static String downloadWithReq(URL uri) throws IOException, InterruptedException {
		return downloadWithReq(uri, "unknown");
	}
	
	public static String downloadWithReq(URL url, String dataType) throws IOException, InterruptedException {
		
		HttpGet request = new HttpGet(url.toString());
		request.addHeader("Content-Type", dataType);
		
		HttpResponse response = httpClient.execute(request);
		
		return EntityUtils.toString(response.getEntity());
	}
	
	public static void downloadToFile(URL url, File file) throws IOException {
		if(file.exists()) {
			file.delete();
		}
		file.createNewFile();
		
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
	    URLConnection conn = url.openConnection();
	    String encoded = Base64.getEncoder().encodeToString(("username"+":"+"password").getBytes(StandardCharsets.UTF_8));
	    conn.setRequestProperty("Authorization", "Basic "+ encoded);
	    InputStream in = conn.getInputStream();
	    byte[] buffer = new byte[1024];
	
	    int numRead;
	    while ((numRead = in.read(buffer)) != -1) {
	        out.write(buffer, 0, numRead);
	    }
	    if (in != null) {
	        in.close();
	    }
	    if (out != null) {
	        out.close();
	    }
	}
}
