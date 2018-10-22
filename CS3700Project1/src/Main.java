/*
 * David Hau
 * CS3700
 * 10/22/2018
 */

import java.io.File;

public class Main
{
	public static void main(String[] args)
	{
		final String FILE_NAME = "Constitution.txt";
		
		try
		{
			Thread huffman = new Thread(new Huffman(new File(FILE_NAME)));
			huffman.start();
			
		} catch(Exception e) { System.out.println("FILE NOT FOUND"); }
		
	}
}
