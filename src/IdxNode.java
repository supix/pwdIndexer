import java.util.HashMap;

/**
 * Questa classe rappresenta il generico nodo dell'albero.
 * @author supix
 *
 */
public class IdxNode {
	/**
	 * I figli del nodo. Contendono i caratteri successivi al nodo corrente
	 */
	private HashMap<Character, IdxNode> children = new HashMap<Character, IdxNode>();
	
	/**
	 * E' il carattere contenuto nel nodo
	 */
	private Character c;
	
	/**
	 * E' il nodo padre del nodo corrente. Serve a risalire l'albero per
	 * ricostruire la parola a partire da un nodo contenente la lettera
	 * finale.
	 */
	private IdxNode parent;
	
	/**
	 * Flag che indica se il carattere corrente è il carattere finale di
	 * una parola.
	 */
	private boolean tail = false;

	/**
	 * Costruttore
	 * @param c Il carattere contenuto nel nodo corrente
	 * @param parent Il nodo parent del nodo corrente
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
	 * Imposta il flag tail a true
	 */
	public void setTail() {
		this.tail = true;
	}
}