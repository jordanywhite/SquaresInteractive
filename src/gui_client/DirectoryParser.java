package gui_client;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * 
 * @author Caleb Piekstra
 *
 */

public class DirectoryParser{	
	
	// XML specific vars
	private static Document doc = null;
	private static Element root	= null;
	
	// Runtime configuration options
	private static final String rootDir = "res\\images\\";
	private static final String outputFileName = "Textures";
	private static final Boolean outputToDocument = true; 
	private static final Boolean timeProgram = true;
	
	// Debugging variables
	private static final Boolean verbose_mode = false;
	private static int fileCount = 0;
	public static long startTime = 0;
	
	public DirectoryParser(String dir, String fileName) {
		// Set up a document
		try {	
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();		
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}		
		
		if (timeProgram) {
			// Start a timer to time the program runtime
			startTime = System.currentTimeMillis();
		}
		
		// Map the directories if we have a doc
		if (doc != null) {
			mapDirectories(dir);			
		}		

		// Keep track of when the program ends so we can do some end-of-the-line methods / output
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	    		if (timeProgram) {
	    			long endTime = System.currentTimeMillis();
	    			// Print the runtime of the program
	    			System.out.println("Total execution time: " + (endTime - startTime) + " ms");
	    			// Print out the number of files and directories (folders) encountered
	    			System.out.println("Files and folder processed: " + fileCount);
	    		}
	        }
	    }, "Shutdown-thread"));
	    
		if (outputToDocument) {
			// Print the XML document out to the console
			if (verbose_mode) printDocumentToConsole(doc, System.out);
			// Output the XML document to a .xml file created at the root directory
			outputDocument(doc, dir, fileName);	
		}
	}

	public static void main(String[] args) 
	{		
		new DirectoryParser(rootDir, outputFileName);
	}
	
	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	Directory Mapping
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/
	
	/**
	 * Maps a directory structure using XML elements
	 * 
	 * @param dirPath	The path of the directory to be mapped
	 */
	private static void mapDirectories(String dirPath) {

		// Make a file using the (root) directory path
		File topDir = new File(dirPath);
		System.out.println("Mapping\n\t" + topDir.getAbsolutePath() + "\n");

		// Initialize the root element
		root = doc.createElement("root");
		root.setAttribute("path",topDir.getAbsolutePath());
		doc.appendChild(root);
		
		// Begin the recursive method using the file at the root
		parseDir(topDir, root);
	}
	
	/**
	 * Given a file and a root Element, this function performs a linear recursive
	 * parsing of all directories and sub-directories, and adding XML elements
	 * to a document
	 * 
	 * @param file			The parent file
	 * @param parentElement	The parent XML element
	 */	

	private static void parseDir (File file, Element parentElement) {		
		// Get all subfiles of the file
		File[] content = file.listFiles();
		// Stop if there are no subfiles (bottomed out)
		if (content == null) return;
		// Handle each subfile
		for ( File f : content) {
			// Print out the path to the subfile
			if (verbose_mode) System.out.println(f.getAbsolutePath());

			// Create the child element
			Element child = createXMLElement(f);

			// recurse on the child
			parseDir(f, child);
			
			// Append the child to the parent element
			parentElement.appendChild(child);
		}
	}
	
	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	XML Object Creation
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/
	
	/**
	 * Given a file, create an XML element to represent the file
	 * 
	 * @param f		The file for which an XML element will be created
	 * @return		The XML element
	 */
	private static Element createXMLElement(File f) {				
		// Get the name of the file
		String fileName = f.getName();			
		// If no file name could be created, do not create an element
		if (fileName == null) return null;			
		// The XML element that will be returned
		Element returnElement = null;
		try {
			// Check if the file is a directory or a file and set attributes / content based on that
			if (f.isDirectory()) {
				// Directory element
				returnElement = doc.createElement("dir");
				returnElement.setAttribute("name", fileName);
			} else {
				// File element
				returnElement = doc.createElement("file");
				returnElement.setTextContent(fileName);				
			}
		} catch (Exception e){
			System.out.println(e);
		}
		if (returnElement != null ) { 
			fileCount++;	// Add to the file count if we have created an element
		}
		return returnElement;
	}
	
	/**___________________________________________________________________________________________**\
   /  / 
  /  |  
 < | |	XML Document File Writing / Printing
  \  |
   \  \___________________________________________________________________________________________
	\**                                                                                           **/
	
	/**
	 * Given a document and an output stream (System.out for this implmenetation)
	 * Prints the XML document to the console
	 * 
	 * @param doc	The XML document
	 * @param out	The output stream (where the document is printed)
	 */
	public static void printDocumentToConsole(Document doc, OutputStream out) {
		try {
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer = tf.newTransformer();
		    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		    transformer.transform(new DOMSource(doc), 
		         new StreamResult(new OutputStreamWriter(out, "UTF-8")));			
		} catch (TransformerException te) {
            System.out.println(te.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
	}
	
	/**
	 * Creates and writes an XML document (no overwrite)
	 *  
	 * @param doc			The XML document to be written
	 * @param saveLocation	Where the document should be created
	 * @param fileName		What to name the document
	 */
	public static void outputDocument(Document doc, String saveLocation, String fileName) {
		 try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
		    tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Make sure the file doesn't already exist before we try writing to it
            File aFile = createFileNoOverwrite(saveLocation, fileName, ".xml");
            FileOutputStream outputFile = null;
            try {
            	outputFile = new FileOutputStream(aFile);
            } catch (Exception e) {
            	e.printStackTrace(System.err);
            }
            // Write to the file
            tr.transform(new DOMSource(doc), 
                    new StreamResult(outputFile));

        } catch (TransformerException te) {
            System.out.println(te.getMessage());
        } 
	}
	
	/**
	 * Creates a file for the XML document to be written to
	 * 
	 * @param location	Where to store the file
	 * @param name		What to name the file
	 * @param extension	What type of file it is
	 * @return			The file
	 */
	private static File createFileNoOverwrite(String location, String name, String extension) {
		String filepath = location + name + extension;
        File newFile = new File(filepath);
        if (newFile.isFile()) {
        	long fileNumber = 1;	// Start this at 1
        	// This keeps checking if a file exists already to prevent overwriting it following a naming scheme
        	// File names are generated as like this: name.xml then name (1).xml then name (2).xml etc
        	do {
        		String newFileName = newFile.getName();
        		int periodIdx = newFileName.indexOf('.');
        		int l_parenIdx = newFileName.indexOf('(') == -1 ? periodIdx : newFileName.indexOf('(');
        		String fileNumStr = "(" + fileNumber++ + ")";
        		if (l_parenIdx == periodIdx) fileNumStr = " " + fileNumStr;
        		newFile = new File(newFile.getParent(), newFileName.substring(0, l_parenIdx) + fileNumStr
        				+ newFileName.substring(periodIdx));
        	} while (newFile.exists());
        }
        return newFile;
	}
}
