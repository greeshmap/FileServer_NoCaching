
import java.util.*;

/* fileSystemAPI should be implemented by your client-side file system. */


public class testCl{

    public static void main(String [] args) 
	throws java.lang.InterruptedException, java.io.IOException, ClassNotFoundException
    {
//		String Filename=args[0]; 
    	String Filename="pyrite.cs.iastate.edu:10609/data.txt";

    	fileSystemAPI fs = new fileSystem(Filename); 
    	filehandle fh;
    	long startTime, endTime, turnAround;
    	
    	// Fetches filehandle for given file. If null is returned, exits the program.
    	fh=fs.open(Filename);	
	    if(fh==null)
	    {
	    	System.out.println("No such file on the server or invalid filename");
	    	System.exit(1);
	    }
    	Scanner sc= new Scanner(System.in);
    	while (true){

    		// open file.
    		fh=fs.open(Filename);	
    		if(fh==null)
    		{
    			System.out.println("No such file on the server or invalid filename");
    			continue;
    		}
    		System.out.println("Enter your choice.\n2. Read\n3. Write\n4. Close and push changes to server\n5. Exit");
    		switch(sc.nextInt())
    		{
    			case 2: 
    			{
    				fh= fs.open(Filename);
    				startTime=Calendar.getInstance().getTime().getTime();
    				// Reads the file 1024 bytes at a time

    				while (!fs.isEOF(fh)){
    					byte[] data= new byte[1024];
    					fs.read(fh, data);
    				}
    				endTime=Calendar.getInstance().getTime().getTime();
    				turnAround=endTime-startTime;
    				System.out.println();
    				System.out.println("This round took "+turnAround+" ms.");
    				break;
    			}
    			case 3:
    			{
    				System.out.println("Enter the text you want to write");
    				sc.nextLine();
    				String str= sc.nextLine();
    				// Writes the user's input to file in local
    				fs.write(fh,str.getBytes("UTF-8"));
    				break;
    			}
    			case 4:
    			{
    				// Pushes all changes in local copy to server copy
    				fs.close(fh);
    				break;
    			}
    			case 5:
    			{
    				// Clears all files in local when exiting the program
    				fs.clearAll();
    				System.out.println("Exiting Program..");
    				System.exit(1);
    			}
    			default: break;
	    
    		}
    		Thread.sleep(1000);
    	}

    }

}
