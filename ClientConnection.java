import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * 
 */

/**
 * @author Greeshma Reddy
 *
 */
public class ClientConnection extends Thread 
{
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	String filePath;
	public ClientConnection (Socket aClientSocket) 
	{
		try 
		{
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out =new DataOutputStream( clientSocket.getOutputStream());
			this.start();
		} 
		catch(IOException e)
		{
			System.out.println("Connection:"+e.getMessage());
		}
	}
	
	
	public void run()
	{
		try 
		{
			while(true)
			{
				
				String data = in.readUTF();
				out.writeUTF(data); // server echoes client’s request
			}
			
		}
		catch(Exception e) 
		{
			System.out.println("EOF:"+e.getMessage());
		} 
 
		finally{ try {clientSocket.close();}catch (IOException e)
	{System.out.println("close:"+e.getMessage());}}
	}

}
