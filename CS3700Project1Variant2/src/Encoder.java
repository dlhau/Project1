/*
 * David Hau
 * CS3700
 * 10/22/2018
 */

public class Encoder
{
	private Node root;
	private String encodedString;

	public Encoder()
	{
		this.root = null;
		this.encodedString = "";
	}
	
	public Encoder(Node root, String encodedString)
	{
		this.root = root;
		this.encodedString = encodedString;
	}

	public Node getRoot() { return root; }
	public String getEncodedString() { return encodedString; }

	public void setRoot(Node root) { this.root = root; }
	public void setEncodedString(String encodedString) { this.encodedString = encodedString; }
}