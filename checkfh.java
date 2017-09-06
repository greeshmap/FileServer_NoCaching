

import java.io.*;

public class checkfh
{
    public static void main(String argv[])
    {
	filehandle fh1, fh2;

	fh1=new filehandle();
	System.out.println("one made.");

	if (fh1.isAlive()) 
	        System.out.println("one is alive.");

	fh2=new filehandle();
	System.out.println("two made.");
	if (fh2.isAlive()) 
	        System.out.println("two is alive.");

	if (fh1.Equals(fh2))
	        System.out.println("one and two are same, this is strange..");
	else
	        System.out.println("one and two are not equal.This is good.");
	fh1.discard();
	fh2.discard();
	if (fh1.Equals(fh2))
	        System.out.println("one and two are discarded and are equal.");
	else
	        System.out.println("one and two are not equal, this is strange.");
    }
}






