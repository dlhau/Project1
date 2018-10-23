/*
 * David Hau
 * CS3700
 * 10/22/2018
 */

public class Node implements Comparable<Node>
	{
	    private char character;
	    private int frequency;
	    private Node left, right;
	    
	    public Node()
	    {
			this.character = '\0';
			this.frequency = 0;
		}
	    
	    public Node(char character, int frequency)
	    {
			this.character = character;
			this.frequency = frequency;
		}
	    
		@Override
		public int compareTo(Node that)
		{
			return this.frequency - that.frequency;
		}
		
		public boolean isLeaf() { return left == null && right == null; }
		
		public char getCharacter() { return character; }
		public int getFrequency() { return frequency; }
		public Node getNodeLeft() { return left; }
		public Node getNodeRight() { return right; }
		
		public void setCharacter(char character) { this.character = character; }
		public void setFrequency(int frequency) { this.frequency = frequency; }
		public void setNodeLeft(Node left) { this.left = left; }
		public void setNodeRight(Node right) { this.right = right; }
	}