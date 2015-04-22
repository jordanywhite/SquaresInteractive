package gui_client;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */

public class ResourceLoader {
	
	ArrayList<File> dirs = null;
	// A mapping of directory to hashmap of filename to file
	HashMap<String, HashMap<String, File>> files = null;
	
	public ResourceLoader() {
		// Initialize the file hash map
		files = new HashMap<String, HashMap<String, File>>();
		// Use the file loader to populate an array list of directories
		FileLoader fileLoader = new FileLoader("res/xml/Textures.xml");
		// Save the list of directory files
		dirs = fileLoader.getDirectories();
		// Get the paths to all the directory files
		ArrayList<String> dirPaths = fileLoader.getFileDirectories();
		// Go through each directory path
		for (String dir : dirPaths) {
			// Get the name of the directory by extracting the last portion of the file URL
			String group = GlobalHelper.getLastBitFromUrl(dir);
			// Create a hash map to hold key value pairs of <filename,file>
			HashMap<String, File> fileMap = new HashMap<String, File>();
			// Using the file loader, get an array list of all files in the current directory (named group)
			ArrayList<File> fileGroup = fileLoader.createFileGroup(group, ".*");	// Get files of all types	
			// Populate the <filename,file> hash map
			for (File f : fileGroup) {
				fileMap.put(f.getName(), f);
			}
			// If no files were mapped, continue onto the next directory
			if (fileMap.isEmpty()) continue;
			// Populate the <dirName,dirFiles> hash map
			files.put(group, fileMap);
		}
		// Print the number of loaded files to the console
		System.out.println("Loaded " + files.size() + " resource groups");
	}
	
	public File getFile(String dir, String fileName) {
		// Look through all directories
		for (String key : files.keySet()) {			
			// Check if this directory contains the file we want
			if (files.get(key).containsKey(fileName)) {
				// If it contains the file we want, return the file
				return files.get(key).get(fileName);
			}
		}
		// File not found
		return null;
	}
	
	public ArrayList<File> getAllFilesInDir(String dir) {
		// Holds the list of files in the directory
		ArrayList<File> filesInDir = new ArrayList<File>();
		// Look for the directory
		if (files.containsKey(dir)) {
			// Get the fileName,File mapping for the directory
			HashMap<String, File> mapOfDir = files.get(dir);
			// Populate an array of all the files in the directory
			for (String fileName : mapOfDir.keySet()) {
				filesInDir.add(mapOfDir.get(fileName));
			}
		}
		// Sort the files
		Collections.sort(filesInDir);
		// Return the array of files (empty if dir doesn't exist or no files in dir)
		return filesInDir;
	}
	
	public ArrayList<File> getAllFilesWithExten(String exten) {
		// Holds the files with the specified extension
		ArrayList<File> filesWithExten = new ArrayList<File>();
		// Go through all directories
		for (String dirKey : files.keySet()) {
			// Get the map for the current dir
			HashMap<String, File> dirMap = files.get(dirKey);
			// Go through all files in the current dir
			for (String fileKey : dirMap.keySet()) {
				// If the file has the requested extension or we are using .* to indicate
				// a request for any file, then add the file to the array list
				if (fileKey.endsWith(exten) || exten.equals(".*")) {
					filesWithExten.add(dirMap.get(fileKey));
				}
			}
		}
		// Sort the files
		Collections.sort(filesWithExten);
		// Return the list of files with the specified extension (empty if no files found)
		return filesWithExten;
	}
	
	public ArrayList<File> getAllFilesWithExtenExcluding(String exten, String[] exclude) {
		// First get all files that have the specified extension
		ArrayList<File> allFilesWithExten = getAllFilesWithExten(exten);		
		// Go through and remove all files whose paths contain one of the excluded directories
		for (Iterator<File> fileIterator = allFilesWithExten.iterator(); fileIterator.hasNext();) {
			File file = fileIterator.next();
			for (String excludedDir : exclude) {
				if (file.getAbsolutePath().contains(excludedDir)) {
					fileIterator.remove();
				}
			}
		}
		// Sort the files
		Collections.sort(allFilesWithExten);
		// Return the files
		return allFilesWithExten;
	}
	
	public ArrayList<File> getAllFilesInDirWithExten(String dir, String exten) {
		// Get all files and we can filter out by extension
		ArrayList<File> allFilesInDir = getAllFilesInDir(dir);
		// .* indicates a request for files of any type
		if (exten.equals(".*")) {
			// If they want files of any type, return all files in the directory
			return allFilesInDir;
		}
		// Go through all files in the directory
		for (Iterator<File> fileIterator = allFilesInDir.iterator(); fileIterator.hasNext();) {
			File file = fileIterator.next();
			// Check if the file does not have the proper extension
			if (!file.getName().endsWith(exten)) {
				// Remove it
				fileIterator.remove();
			}
		}
		// Sort the files
		Collections.sort(allFilesInDir);
		// Return all files in the directory that had the specified extension (empty if none found)
		return allFilesInDir;
	}
	
	public ArrayList<File> getAllDirs() {
		// Return a copy of the list of all directory files
		return new ArrayList<File>(dirs);	
	}
	
	public ArrayList<File> getAllDirsExcluding(String[] exclude) {
		// Get a copy of the list of all directory files
		ArrayList<File> dirs = getAllDirs();
		// Go through the directory files
		for (Iterator<File> fileIterator = dirs.iterator(); fileIterator.hasNext();) {
			File file = fileIterator.next();
			// Check the directory path against all excluded directories
			for (String excludedDir : exclude) {
				// If an excluded directory is somewhere in the current 
				// directory's file path remove the file from the list
				if (file.getAbsolutePath().contains(excludedDir)) {
					fileIterator.remove();
				}
			}
		}
		// Sort the files
		Collections.sort(dirs);
		// Return the list of directory files 
		return dirs;
	}
	
	public ArrayList<File> getAllDirsInDir(String dir) {
		// Get a copy of the list of all directory files
		ArrayList<File> dirs = new ArrayList<File>();;
		// Go through the directory files
		for (File file : getAllDirs()) {
			// Get the parent dir
			String filePath = file.getAbsolutePath();
			String parentDir = GlobalHelper.getNthSegmentFromURLEnd(filePath,1);
			// If an include directory is not somewhere in the current 
			// directory's file path remove the file from the list
			if (parentDir.equals(dir)) {
				dirs.add(file);
			}
		}
		// Sort the files
		Collections.sort(dirs);
		// Return the list of directory files 
		return dirs;
	}
}
