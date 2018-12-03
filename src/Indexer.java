import java.util.ArrayList;
import java.util.HashMap;

public class Indexer {
	/**
	 * The keys in this map are all the characters contained in the indexed file. For
	 * each key, the corresponding value is a list of references to the tree nodes
	 * associated to that character. Searching a keyword starting with the 'x' character
	 * (e.g. 'xyz') uses this map to find all the nodes the search has to start from
	 * (and continue with the following characters, i.e. 'yz').
	 */
	private HashMap<Character, ArrayList<IdxNode>> allNodes = new HashMap<Character, ArrayList<IdxNode>>();
	
	/**
	 * This map contains all the tree nodes at the first-level. Tha map is used only
	 * in the indexing phase (not in the search phase). When a token starting with the
	 * 'x' character is indexed, the map is used to possibly find another already
	 * indexed token starting with 'x'. Otherwise, a new first-level node associated
	 * with the 'x' character has to be created.
	 */
	private HashMap<Character, IdxNode> rootNodes = new HashMap<Character, IdxNode>();
	
	/**
	 * Index a token.
	 * <p>
	 * Note: the token indexing is a fully recursive function. Each character in the token
	 * corresponds to a recursion step.
	 * </p>
	 * @param token The token to be indexed
	 */
	public void index(String token) {
		processTokenChar(token, 0, null);
	}

	/**
	 * Recursive method which, char by char, fully indexes the token.
	 * @param token The token to be indexed
	 * @param i The index of the currently indexed char in the token
	 * @param node The tree node corresponding to the last (already) indexed char. It is null for the first indexed character.
	 */
	private void processTokenChar(String token, int i, IdxNode node) {
		// if the character-index to analyze falls beyond the last character, exit
		if (i >= token.length())
			return;
		
		// take the currently analyzed character
		char c = token.charAt(i);
		
		IdxNode child;
		
		if (node == null || !node.getChildren().containsKey(c)) {
			if (node == null && rootNodes.containsKey(c))
				// found a first level node, assign it to child
				child = rootNodes.get(c);
			else {
				// first level node not found, nor a child of the currently analyzed node
				// (in case of recursive call)
				child = new IdxNode(c, node);
				// if we are at the first level (node == null) the created node
				// is a first level node, too. Add it to root nodes.
				// rootNodes
				if (node == null)
					rootNodes.put(c, child);
			
				// the created node is added to allNodes map
				ArrayList<IdxNode> arrayList;
				if (!allNodes.containsKey(c)) {
					arrayList = new ArrayList<IdxNode>();
					allNodes.put(c, arrayList);
				} else
					arrayList = allNodes.get(c);
				arrayList.add(child);
			}
		} else {
			child = node.getChildren().get(c);
		}
			
		// if this is the last letter of the token, set the tail flag to true
		if (i == token.length() - 1)
			child.setTail();
		
		// process the next character
		processTokenChar(token, i + 1, child);
	}
	
	/**
	 * Search the index. All the matching tokens are returned. 
	 * @param searchKey The search key
	 * @return The matching tokens
	 */
	public ArrayList<String> search(String searchKey) { 
		ArrayList<String> list = new ArrayList<String>();
		
		// in case of an empty token, exit
		if (searchKey.length() == 0)
			return list;
		
		// thake the first character
		char c = searchKey.charAt(0);
		
		// if it is not in the allNodes map, the search gives no results. Exit.
		if (!allNodes.containsKey(c))
			return list;
		
		// for each node in allNodes, a recursive visit starts
		for (IdxNode node: allNodes.get(c)) {
			// create a temporary node useful just for passing to the recursive function
			// as a parent node...
			IdxNode tempParentNode = new IdxNode(c, null);
			
			// ...and add to it the node to be visited.
			tempParentNode.getChildren().put(c, node);
			
			// start the recursive search
			searchTokenChar(searchKey, 0, tempParentNode, list);
		}
		
		return list;
	}

	/**
	 * Recursive search method.
	 * @param searchKey The search key
	 * @param i The index of the currently searched character in the token
	 * @param node The currently analyzed node
	 * @param list The list to be filled with the matching tokens
	 */
	private void searchTokenChar(String searchKey, int i, IdxNode node, ArrayList<String> list) {
		if (i >= searchKey.length())
			return;
		
		char c = searchKey.charAt(i);
		if (!node.getChildren().containsKey(c))
			return;
		
		IdxNode child = node.getChildren().get(c);
		
		if (i < searchKey.length() - 1)
			searchTokenChar(searchKey, i + 1, child, list);
		else {
			if (child.isTail())
				list.add(getTokenFromNode(child));
			
			searchAllTailsInChildren(child, list);
		}		
	}

	/**
	 * Given a node, visits all the descending nodes selecting those having
	 * tail set to true. For each of them, climbs up the tree until reaching
	 * the root in order to rebuild the token. 
	 * @param child The node rooting the subtree to be visited
	 * @param list The list to be filled with the matching tokens
	 */
	private void searchAllTailsInChildren(IdxNode child, ArrayList<String> list) {
		for(IdxNode node: child.getChildren().values()) {
			if (node.isTail())
				list.add(getTokenFromNode(node));
			searchAllTailsInChildren(node, list);
		}
	}

	/**
	 * Given a node, climbs up until the root and rebuilds the token
	 * @param child The starting node
	 * @return The rebuilt token
	 */
	private String getTokenFromNode(IdxNode child) {
		String s = new String();
		IdxNode node = child;
		while (node != null) {
			s = node.getCharacter() + s;
			node = node.getParent();
		}
		
		return s;
	}
}
