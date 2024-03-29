# Simple-updater

Simple-updater lightweight is a java library for updating and downloading files the easiest to use.

## Todo list

- [x] Build basic static methods
- [x] Finalise updater class
## How to use simple-updater
### Simple-updater's static methods

- Use this method to download text from web
  ```
  File SimpleUpdater.downloadWithReq(URI uri)
  ```
- Use this method to text from web and specify a data type
  ```
  File SimpleUpdater.downloadWithReq(URI uri, String data type)
  ```
- Use this method to list recursively all files in a folder
  ```
  ArrayList<File> SimpleUpdater.listFiles(File dir) throws IOException
  ```
- Use this method to generate MD5 checksum of a file
  ```
  String generateChecksumFor(File file) throws IllegalArgumentException, IOException
  ```
- Use this method to generate MD5 checksum for all files in a folder recursively,
  returns HashMap<File in folder, MD5 checksum>.
  ```
  HashMap<File, String> listAndCheckFiles(File dir) throws IllegalArgumentException, IOException 
  ```
- Use this method to check if a Map entry <File, Checksum> is contained in another HashMap<File, Checksum>
  ```
  boolean containsFileSame(Map.Entry<File, String> entry, HashMap<File, String> ref)
  ```
- Use this method to download from an url to a file
  ```
  void downloadToFile(URL url, File file) throws IOException
  ```
### Simple-updater's class

You can use this class to update a folder. Use ``` new SimpleUpdater(URI simpleUpdaterServerURI, File folderToUpdate) ``` to update a folder. You can download simple-updater Server [here](https://github.com/IGOLTA/simple-updater-Server). The constructor of SimpleUpdater class throws IOException, InterruptedException and IllegalArgumentException.
## Dependencies

|Name|Version|
|:-:|:-:|
|Json-Simple|[1.1.1](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple)|
|Apache HttpClient|[4.5.10](https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient)|
