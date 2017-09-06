

/* standard java classes. */
import java.util.*;

/* fileSystemAPI should be implemented by your client-side file system. */

/* There are two methods, main and readField. The latter does the following.

   (1) It reads one field upto, but not including, a specified delimiter.
   (2) It also converts the byte array to String and returns it.
   (3) The pointer should reside after the delimeter.

   It does NOT expect the file can be in a wrong format... */

public class testClient{

    public static void main(String [] args) throws java.lang.InterruptedException, java.io.IOException, ClassNotFoundException
    {
	// get arguments.
	String IPadr=args[0]; // e.g. 223.223.223.223
	String Port=args[1];  // e.g. 7777 
	String Filename=args[2]; // e.g. MyFile

	/* Initialise the client-side file system.
	   The following line should be replaced by something like:
	     FileSystemAPI fs=new YourClientSideFileSystem()
	   in your version.
	*/
	fileSystemAPI fs=null; 
	
	// variables used.
	filehandle fh;
	String city, weather, date, updatetime;
	long startTime, endTime;
	long turnAround;

	

	// repeat displaying remote data and turn-around time.

	while (true){
	    
          // open file.
	    fh=fs.open(IPadr+":"+Port+"/"+Filename);	
	    
          // read the whole file, check the time needed.
	    startTime=Calendar.getInstance().getTime().getTime();

	    while (!fs.isEOF(fh)){

		// read data.
		city=readField(fs, fh, ';');
		weather=readField(fs, fh, ';');
		date=readField(fs, fh, ';');
		updatetime=readField(fs, fh, '.');
		
		// print data.
		System.out.println(city+", "+weather+", "+date+", "+updatetime);
	    }
	    endTime=Calendar.getInstance().getTime().getTime();
	    turnAround=endTime-startTime;

	    // print the turn around time.
	    System.out.println("");
	    System.out.println("This round takes"+turnAround+"ms.");	

	    // wait a bit.
	    Thread.sleep(500);

	}

    }

    /* We need to convert between bytes and chars. we do this by
       simply getting ascii values and storing them as bytes. 
    */

    static String readField(fileSystemAPI fs, filehandle fh, char delimeter)
	throws java.lang.InterruptedException, java.io.IOException, ClassNotFoundException
    {
	/* This method reads a file byte-by-byte.
	   this is slow, but simple.
	*/
	char ch;
	byte [] data = {(byte) 'a'};
	int res;
	String field="";
	if (fs.isEOF(fh)) 
	    return null;
	else {
		while (true){
		    // reads one byte, and converts it into character.
		    res=fs.read(fh, data);
		    if (res==-1)
			return field;
		    else { 		
			ch=(char) data[0];
			if (ch!=delimeter) {
			    field=field+(new Character(ch)).toString();
			    if (fs.isEOF(fh))
				return field;
			}
			else
			    return field;
		    }
		}
	}
    }
} 
    
	    

	    
	    
	    
	    


		
