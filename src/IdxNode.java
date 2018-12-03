import java.util.HashMap;

/**
 * This class models the tree node
 * @author supix
 *
 */
public class IdxNode {
	/**
	 * The node's children, i.e. the characters which follow the current node character.
	 */
	private HashMap<Character, IdxNode> children = new HashMap<Character, IdxNode>();
	
	/**
	 * The character contained in the node
	 */
	private Character c;
	
	/**
	 * The parent of the node. It allows to climb up the node to rebuild
	 * the token starting from the node containing the last character.
	 */
	private IdxNode parent;
	
	/**
	 * Flag indicating whether the node corresponds to the last letter
	 * of a token.
	 */
	private boolean tail = false;

	/**
	 * Constructor
	 * @param c The node's character
	 * @param parent The node's parent
	 */
	public IdxNode(Character c, IdxNode parent) {
		this.c = c;
		this.parent = parent;
		
		if (this.parent != null)
			this.parent.getChildren().put(c, this);
	}

	public Character getCharacter() {
		return this.c;
	}

	public HashMap<Character, IdxNode> getChildren() {
		return this.children;
	}

	public IdxNode getParent() {
		return this.parent;
	}

	public boolean isTail() {
		return this.tail;
	}
	
	/**
	 * Set the tail to true
	 */
	public void setTail() {
		this.tail = true;
	}
}