/*
 * David Hau
 * CS3700
 * 10/22/2018
 */

import java.util.PriorityQueue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap; 
  
public class HuffmanVariantOne implements Runnable
{
	private static final int ASCII = 256;
	
	private File file;
	private String fileString;
	private StringBuilder encodedString;
	private HashMap<Character, String> charCodes;
	private PriorityQueue<Node> pq;
	
	public HuffmanVariantOne(File file) throws IOException
	{
		this.file = file;
		this.pq = new PriorityQueue<>();
		this.charCodes = new HashMap<>();
		this.readFile(file.getName());
		this.encodedString = new StringBuilder();
	}
	
	class MyComparator implements Comparator<Node>
	{ 
	    public int compare(Node x, Node y) 
	    { 
	        return x.getFrequency() - y.getFrequency(); 
	    } 
	}
	
	private void readFile(String filename) throws IOException
	{
		fileString = new String(Files.readAllBytes(Paths.get(filename)));
		int[] freq = new int[ASCII];
		
		for (char c : fileString.toCharArray())
		{
			freq[c] = freq[c] + 1;
		}
		
		int freqCount = 0;
		
		for (int i = 0; i < freq.length; i++)
		{
			freqCount = freq[i];
			if (freqCount != 0)
			{
				pq.offer(new Node((char) i, freqCount));
			}
		}
	}
	
	public Encoder compress()
	{
		Node x, y, z;
		long start = System.nanoTime();
		
		while (pq.size() > 1)
		{
			z = new Node();
			x = pq.poll();
			z.setNodeLeft(x);
			y = pq.poll();
			z.setNodeRight(y);
			z.setFrequency(x.getFrequency() + y.getFrequency());
			pq.offer(z);
		}
		
		Node root = pq.poll();
		
		System.out.println("Time to Construct Tree: " + (System.nanoTime() - start) + " ns");
		
		findCodes(root, "");
		start = System.nanoTime();
		encode();
		
		System.out.println("Total Compression Time: " + (System.nanoTime() - start) + " ns");
		
		return new Encoder(root, encodedString.toString());
	}

	private void findCodes(Node node, String code)
	{
		if (node.isLeaf())
		{
			charCodes.put(node.getCharacter(), code);
		}
		
		if (node.getNodeLeft() != null)
		{
			findCodes(node.getNodeLeft(), code + "0");
		}
		
		if (node.getNodeRight() != null)
		{
			findCodes(node.getNodeRight(), code + "1");
		}
	}

	private void encode()
	{
		long start = System.nanoTime();
		
		for (int i = 0; i < fileString.length(); i++)
		{
			encodedString.append(charCodes.get(fileString.charAt(i)));
		}
		
		System.out.println("Time to Encode File: " + (System.nanoTime() - start) + " ns");
	}
    
	@Override
	public void run()
	{
		try
		{
			System.out.println("Huffman Variant One\n");
			
			String fileString = new String(Files.readAllBytes(Paths.get(file.getPath())));
			int fileBytes = fileString.length() * 8;
			
			Encoder fileEncoded = compress();

			// Get compression percent
			int compressedBytes = fileEncoded.getEncodedString().length();
			System.out.printf("File Compression Percent: %.2f%n", (double) compressedBytes / fileBytes);
			
		} catch(Exception e) {}
		
	}
    
}