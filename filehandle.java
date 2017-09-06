import java.io.Serializable;


public class filehandle implements Serializable
{
    /* The "filehandle" is simply an integer.  We keep a counter in a
       static variable "cnt" so that no duplication occurs.  When
       filehandle is discarded its number becomes 0. */

    private int index;
    private static int cnt = 1;
    public boolean isValid=true;

    public filehandle()
    {
	index=cnt++;
    }

    public boolean isAlive()
    {
	return (this.index!=0);
    }

    /* checks two handles are equal or not. */
    public boolean Equals(filehandle fh) 
    { 
	return (fh.index==this.index);
    }

    /* discarding a filehandle. you do not have to use this. */
    public void discard()
    {
	index=0;
    }
}
