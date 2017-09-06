import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * 
 * @author Greeshma Reddy
 * This program receives Server's details from the testCl. This implements open, read, write, isEOF, close 
 * methods of the fileSystem API.
 */
public class fileSystem implements fileSystemAPI
{ 
	// hm holds a mapping of filehandle and file attributes
	//handleToFilenameMap holds a mapping of filehandle and filename
	static HashMap<filehandle, FileStruct> hm= new HashMap<filehandle, FileStruct>();
	static Socket clientSocket = null;
	ObjectInputStream ois=null;
	ObjectOutputStream oos=null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
    HashMap<filehandle, String> handleToFilenameMap;


    /**
     * fileSystem's constructor
     * @param filename
     * @throws IOException
     */
	public fileSystem(String filename) throws IOException
	{
		handleToFilenameMap= new HashMap<filehandle, String>();
		// Extract hostname and port name from the filename
		String hostName=filename.substring(0, filename.indexOf(':'));
		int port= Integer.parseInt(filename.substring(filename.indexOf(':')+1, filename.indexOf('/')));
						
		// handleToFilenameMap keeps track of filehandles and corresponding filenames
		handleToFilenameMap= new HashMap<filehandle, String>();
		try {
			// Connects to server socket
		   	clientSocket = new Socket(hostName, port);
		   	System.out.println("Client started on "+ clientSocket.getLocalPort());
			    	
		   	// Initialize Object input stream and output stream
		   	oos= new ObjectOutputStream(clientSocket.getOutputStream());
		   	ois= new ObjectInputStream(clientSocket.getInputStream());
		    	
			} catch (Exception e) {
				System.out.println("Error occured"+ e.getMessage());
				e.printStackTrace();
			}
	}

	/**
	 * Accepts url as input and returns file handle of given url from the server
	 */
    public filehandle open(String url) throws IOException
    {
    	filehandle fh=null;
    	String filename=url.substring(url.indexOf('/')+1);
//    	System.out.println(filename);
    	// If filename contains extra '/' , null is returned
    	if(filename.contains("/")) return null;
//    	filename="C:\\Workspace\\workspacenew\\554Project2_Stage2\\src\\data.txt";
    	if(handleToFilenameMap.values().contains(filename))
    	{
    		Set<filehandle> set=  handleToFilenameMap.keySet();
    		for(filehandle fhandle:set)
    		{
    			if(handleToFilenameMap.get(fhandle).equals(filename)) 
    			{
    				return fhandle;
    			}
    		}
    	}
		try {
			oos.writeObject(1);
	    	oos.writeObject(url);
	    	oos.flush();
	    	fh= (filehandle) ois.readObject();
	    	// If file is on server, it's data is read and a local copy is created with the read content
	    	if(fh.isValid)
	    	{
	    		File f= new File(filename);
	    		handleToFilenameMap.put(fh,  filename);
				hm.put(fh, new FileStruct(filename));	
	    		dis= new DataInputStream(clientSocket.getInputStream());
	    		int len=dis.readInt();
	    		byte[] text= new byte[len];
	    		FileOutputStream fOut= new FileOutputStream(f);
	    		dis.read(text, 0, len);
	    		fOut.write(text);
	    		fOut.close();
	    	}
	    	else
	    	{
	    		fh=null;
	    	}
	    	
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return fh;
    }
	
    /**
     * Performs a write operation on a local copy
     */
    public boolean write(filehandle fh, byte[] data) throws java.io.IOException, ClassNotFoundException
    {
    	File f=null;    	
    	if(handleToFilenameMap.containsKey(fh))
    	{
    		f= doALocalWrite(fh, data, f);
    		return true;
    	}
    	return false;
    }

    /**
     * Does actual write in the local copy
     * @param fh
     * @param data
     * @param f
     * @return file
     * @throws IOException
     */
	private File doALocalWrite(filehandle fh, byte[] data, File f) throws IOException {

		f= new File(handleToFilenameMap.get(fh));
		FileWriter fWriter= new FileWriter(f, true);
		String str= new String(data);
		fWriter.write(str);
		fWriter.close();
		int writePosition= hm.get(fh).writePosition;
		hm.get(fh).writePosition= writePosition+data.length;	
		return f;
	}

	/**
	 * read bytes from the current position. returns the number of bytes read: if end-of-file, returns -1. It's a read from the local copy
	 */
	public int read(filehandle fh, byte[] data) throws java.io.IOException, ClassNotFoundException
    {   
    	File f= new File(handleToFilenameMap.get(fh));
		int offset=hm.get(fh).readPosition;
    	FileInputStream fIn= new FileInputStream(f);
    	fIn.getChannel().position(offset);
		if(fIn.available()>=0)
		{
			fIn.read(data);	
			System.out.print(new String(data).trim());
			int readPosition= hm.get(fh).readPosition;
			hm.get(fh).readPosition= readPosition+data.length;
	    	return data.length;
	    }
    	fIn.close();
    	return -1;
    }

    /**
     * Pushes all the changes to server copy by checking last modified time 
     * of server copy. This is usually after user is done with all the changes, 
     */
    public boolean close(filehandle fh) throws java.io.IOException, ClassNotFoundException
    {
    	File f= new File(handleToFilenameMap.get(fh));
    	if(handleToFilenameMap.containsKey(fh))
    	{
    		oos.writeObject(2);
        	oos.writeObject(handleToFilenameMap.get(fh));
        	if((boolean)ois.readObject())
        	{
        		long lastModifiedTime=(long) ois.readObject();
        		if(f.lastModified()>lastModifiedTime)
        		{
        			byte[] text= new byte[(int) f.length()];
        			FileInputStream fIn= new FileInputStream(f);
        			if(fIn.available()>=0)
        			{
        				fIn.read(text);	
        			}
        			fIn.close();
        			dos= new DataOutputStream(clientSocket.getOutputStream());
        			dos.writeInt(text.length);
        			dos.write(text);
        			dos.flush();
        		}
        		oos.flush();
        	}
        	System.out.println("Pushed changes to server copy");
//        	handleToFilenameMap.remove(fh);
//        	hm.remove(fh);
        	fh.discard();
        	return true;
    	}    	
    	return false;
    }

    /**
     * Checks the EOF. Returns true/ false
     */
    public boolean isEOF(filehandle fh) throws java.io.IOException, ClassNotFoundException
    {
    	int readPos=hm.get(fh).readPosition;
		File f= new File(handleToFilenameMap.get(fh));
		if(readPos>=f.length())
		{
			hm.get(fh).readPosition= 0;
			return true;
		}
		return false;
    }
    
    
    /**
     * Clears all copies in local when exiting the program
     */
    public void clearAll() throws IOException
    {
    	Set<filehandle> set= new HashSet<filehandle>();
    	set=handleToFilenameMap.keySet();
    	for(filehandle fhandle: set)
    	{
    		File f= new File(handleToFilenameMap.get(fhandle));
    		f.delete();
    	}
    	oos.writeObject(4);
    	
    	
    }
} 

