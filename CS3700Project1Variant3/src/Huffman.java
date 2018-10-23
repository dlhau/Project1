/*
 * David Hau
 * CS3700
 * 10/22/2018
 */

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap; 
  
public class Huffman implements Runnable
{
	private static final int ASCII = 256;
	
	private static int threads = 4;
	private static ExecutorService executorOne = Executors.newFixedThreadPool(threads);
	private static ExecutorService executorTwo = Executors.newFixedThreadPool(threads);
	private File file;
	private String fileString;
	private StringBuilder encodedString;
	private HashMap<Character, String> charCodes;
	private PriorityQueue<Node> pq;
	
	public Huffman(File file) throws IOException
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
		
		char[] ch = fileString.toCharArray();
		int startIndex = 0;
		int fileDivision = (int) Math.ceil((double) fileString.length() / 4);
		
		int finishIndex = fileDivision;
		
		for (int i = 0; i < threads; i++)
		{
			executorOne.execute(new Parse(startIndex, finishIndex, ch,freq));
			startIndex = startIndex + fileDivision;
			finishIndex = startIndex + fileDivision;
		}
		executorOne.shutdown();
		
		while (!executorOne.isTerminated())
		{
			Thread.yield();
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
			findCodes(node.getNodeLeft(), code + 0);
		}
		
		if (node.getNodeRight() != null)
		{
			findCodes(node.getNodeRight(), code + 1);
		}
	}

	private void encode()
	{
		long start = System.nanoTime();
		
		StringBuilder[] threadResults = new StringBuilder[threads];
		
		for (int i = 0; i < threadResults.length; i++)
		{
			threadResults[i] = new StringBuilder();
		}
		
		int startingIndex = 0;
		int fileDivision = (int) Math.ceil((double) fileString.length() / threads);
		
		int lastIndex = fileDivision;
		
		executorTwo = Executors.newFixedThreadPool(threads);
		
		for (int i = 0; i < threads; i++)
		{
			executorTwo.execute(new Encode(threadResults[i], startingIndex,
					lastIndex, fileString, charCodes));
			startingIndex = startingIndex + fileDivision;
			lastIndex = startingIndex + fileDivision;
		}
		
		executorTwo.shutdown();
		
		while (!executorTwo.isTerminated())
		{
			Thread.yield();
		}
		for (StringBuilder sb : threadResults)
		{
			encodedString.append(sb.toString());
		}
		
		System.out.println("Time to Encode File: " + (System.nanoTime() - start) + " ns");
		
	}
    
	
	static class Encode implements Runnable
	{
		StringBuilder result;
		int firstIndex, lastIndex;
		String data;
		HashMap<Character, String> charCodes;

		Encode(StringBuilder result, int startingIndex, int finishIndex,
				String data, HashMap<Character, String> charCodes)
		{
			this.result = result;
			this.firstIndex = startingIndex;
			this.lastIndex = finishIndex;
			this.data = data;
			this.charCodes = charCodes;
		}

		@Override
		public void run() {
			for (int i = firstIndex; i < lastIndex; i++)
			{
				if (i >= data.length())
				{
					break;
				}
				result.append(charCodes.get(data.charAt(i)));
			}
		}
	}
	
	static class Parse implements Runnable
	{
		StringBuilder result;
		int firstIndex, lastIndex;
		char[] ch;
		int[] freq;
		
		Parse(int firstIndex, int lastIndex, char[] ch, int[] freq)
		{
			this.firstIndex = firstIndex;
			this.lastIndex =lastIndex;
			this.ch = ch;
			this.freq = freq;
		}

		@Override
		public void run() {
			for (int i = firstIndex; i < lastIndex; i++)
			{
				if (i >= ch.length)
				{
					break;
				}
				
				freq[ch[i]] = freq[ch[i]] + 1;
			}
		}
	}

	@Override
	public void run()
	{
		try
		{
			System.out.println("MultiThread Huffman Three\n");
			
			String fileString = new String(Files.readAllBytes(Paths.get(file.getPath())));
			int fileBytes = fileString.length() * 8;
			
			Encoder fileEncoded = compress();

			// Get compression percent
			int compressedBytes = fileEncoded.getEncodedString().length();
			System.out.printf("File Compression Percent: %.2f%n", (double) compressedBytes / fileBytes);
		} catch(Exception e) {}
		
	}
	
	
}