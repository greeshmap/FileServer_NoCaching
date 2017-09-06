import java.io.IOException;

/* This is the interface to the client-side file system. We assume 
   that, once you open the file, you keep a pointer, and whenever
   you write or read, that pointer moves to the next byte. */

/* I hide the representation of the "filehandle" (given in a separate
   class file). You can change its implementation as you like. */


public interface fileSystemAPI  
{ 
    /* url has form IP:port/path. */
    public abstract filehandle open(String url) 
	throws java.io.IOException, ClassNotFoundException; 

    /* write data starting from the current pointer. */
    public abstract boolean write(filehandle fh, byte[] data)
	throws java.io.IOException, ClassNotFoundException; 

    /* read data.length bytes from the current position; if
       end-of-file, returns -1; if not, the next byte (position) is
       returned. */
    public abstract int read(filehandle fh, byte [] data)
	throws java.io.IOException, ClassNotFoundException; 

    /* close file. you should flush the data (over the network). */  
    public abstract boolean close(filehandle fh)
	throws java.io.IOException, ClassNotFoundException; 

    /* check if it is at the end-of-file. */
    public abstract boolean isEOF(filehandle fh)
	throws java.io.IOException, ClassNotFoundException;

	/**
	 * @throws IOException 
	 * 
	 */
	public abstract void clearAll() throws IOException; 

    /* You do not have to implement the following two methods. */
    //    public abstract boolean flush(filehandle fh);
    //   public abstract byte [] read(filehandle fh, int n);


} 
    
	
