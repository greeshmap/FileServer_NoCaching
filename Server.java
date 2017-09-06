import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


/**
 * @author Greeshma Reddy
 *  Server program is responsible for sending file content and accepting changes from client
 *  
 */
public class Server {	
	
	static HashMap<String, filehandle> filenameToHandleMap= new HashMap<String, filehandle>();
	static HashMap<filehandle, FileStruct> hm= new HashMap<filehandle, FileStruct>();
	static ServerSocket serverSocket=null;
	
	public static void main (String args[]) throws ClassNotFoundException 
	{
		// Checks for valid number of arguments. Port number must be entered as a command-line argument
		if(args.length!=1)
		{
			System.out.println("Port number missing..Exiting the program");
			System.exit(1);
		}
		try
		{
			Socket clientSocket=null;
//			int serverPort = Integer.parseInt(args[0]);
			int serverPort = 10608;

			// Creates a server socket on given port

	        serverSocket = new ServerSocket(serverPort);
	        System.out.println("Starting server on "+serverSocket.getLocalPort());

			while(true) 
			{
				// Accept a new client socket connection
				clientSocket = serverSocket.accept();
				
				// Initialize objectinputstream, objectoutputstream and datainputstream, dataoutputstream
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				ObjectOutputStream oos= new ObjectOutputStream(clientSocket.getOutputStream());
				DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
				while(true)
				{
					int operation= (int) ois.readObject();
					switch(operation)
					{
					case 1: 
					{
						// Open- 1
						String url= (String) ois.readObject();
						String filename=url.substring(url.indexOf('/')+1, url.length());
						File f=null;
						
						// Checks if the file is in current directory
						if(lookup(url))
						{
							f= new File(filename);
							if(filenameToHandleMap.containsKey(filename))
							{
								oos.writeObject(filenameToHandleMap.get(filename));
							}
							else
							{
								// Creates a new file handle and sends it to the client
								filehandle fh= new filehandle();
								filenameToHandleMap.put(filename, fh);
								hm.put(fh, new FileStruct(filename));	
								oos.writeObject(fh);
								
							}
							byte[] text= new byte[(int) f.length()];
							FileInputStream fIn= new FileInputStream(f);
							fIn.read(text);
							fIn.close();
							dos.writeInt(text.length);
							dos.write(text);
						}
						else
						{
							filehandle fh= new filehandle();
							fh.isValid=false;
							oos.writeObject(fh);
						}
						oos.flush();
						break;
						
					}
					case 2:
					{
						// Get's changes from client's local copy and writes to actual file
						String filename=(String) ois.readObject();						
						if(filenameToHandleMap.containsKey(filename))
						{
							long lastModifiedTime= getAttribute(filename);
							oos.writeObject(true);
							// Send last modified time of file on server
							oos.writeObject(lastModifiedTime);	
							int len= dis.readInt();
							byte[] text= new byte[len];
							dis.read(text, 0, len);							
							FileOutputStream fOut= new FileOutputStream(new File(filename));
							fOut.write(text, 0, len);
							fOut.close();
						}
						else
						{
							oos.writeObject(false);
						}
						oos.flush();
						break;
					}
					case 3:
					{
						// getAttribute- 3
						String filename= (String) ois.readObject();
						oos.writeObject(getAttribute(filename));
						oos.flush();
					}
					case 4:
					{
						// Exit server
						System.out.println("Exiting Server");
						System.exit(1);
					}
					default: break;
					}	

				}
			}
		} 	
		catch(IOException e) 
		{	
			System.out.println("Server :"+e.getMessage());
			System.out.println("Exiting Server due to an exception caused");
		}
		}



	/**
	 * Returns last modified time of file on server
	 * @param filename
	 * @return last modified time
	 */
	private static long getAttribute(String filename)
	{
		File f= new File(filename);
		return f.lastModified();
	}
	
	/**
	 * Returns true if the file exists on server. Else returns false
	 * @param url
	 * @return true/ false
	 */
	private static boolean lookup(String url)
	{
		File dir = new File(".");
		String filename=url.substring(url.indexOf('/')+1, url.length());
		File[] list = dir.listFiles();
        if(list!=null)
        for (File fil : list)
        {
             if (filename.equalsIgnoreCase(fil.getName()))
            {
               return true;
            }
        }        
        return false;
	}

}
