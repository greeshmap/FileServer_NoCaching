import java.io.File;


/**
 * FileStruct holds attributes of the file- fileName, readPosition, writePosition and lastModifiedTime
 */

/**
 * @author Greeshma Reddy
 *
 */
public class FileStruct {
	
	String filename;
	int readPosition;
	int writePosition;
	long lastModifiedTime;
	
	public FileStruct(String filename)
	{
		this.filename=filename;
		File f= new File(filename);
		lastModifiedTime= f.lastModified();
		readPosition=0;
		writePosition=(int) f.length();
	}

}
