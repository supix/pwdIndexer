import java.util.ArrayList;
import java.util.HashMap;

public class Indexer {
	/**
	 * Questa mappa ha come chiavi tutti i caratteri contenuti nelle parole indicizzate.
	 * Per ogni chiave, ha una lista di puntatori ai nodi dell'albero contenenti quel
	 * carattere. Una ricerca con una keyword che inizia col carattere x userà questa
	 * mappa per individuare tutti i nodi da cui ha inizio la ricerca in cerca dei
	 * successivi caratteri (quelli dopo x).
	 */
	private HashMap<Character, ArrayList<IdxNode>> allNodes = new HashMap<Character, ArrayList<IdxNode>>();
	
	/**
	 * Questa mappa contiene tutti i nodi al primo livello dell'albero. La mappa
	 * è usata esclusivamente in fase di indicizzazione. Se devo indicizzare una parola
	 * che inizia con la lettera x, la mappa mi dice se c'è già un'altra parola che
	 * inizia con la lettera x oppure devo creare un nuovo nodo al livello 1.
	 */
	private HashMap<Character, IdxNode> rootNodes = new HashMap<Character, IdxNode>();
	
	/**
	 * Indicizza un token
	 * @param token Il token da indicizzare
	 */
	public void index(String token) {
		processTokenChar(token, 0, null);
	}

	/**
	 * Metodo ricorsivo che, carattere per carattere, indicizza l'intero token
	 * @param token Il token da indicizzare
	 * @param i Il carattere correntemente in fase di indicizzazione
	 * @param node Il nodo dell'albero relativo al carattere appena indicizzato
	 */
	private void processTokenChar(String token, int i, IdxNode node) {
		// se la lettera da analizzare è oltre la lunghezza del token, esco
		if (i >= token.length())
			return;
		
		// prendo il carattere correntemente analizzato
		char c = token.charAt(i);
		
		IdxNode child;
		
		if (node == null || !node.getChildren().containsKey(c)) {
			if (node == null && rootNodes.containsKey(c))
				// ho trovato un nodo al primo livello, lo assegno a child
				child = rootNodes.get(c);
			else {
				// non ho trovato un nodo al primo livello, oppure come
				// successivo di quello correntemente analizzato (in caso
				// di chiamata ricorsiva)
				child = new IdxNode(c, node);
				// se sono al primo livello (node == null) il nodo create
				// è anche un nodo al primo livello, e lo aggiungo a
				// rootNodes
				if (node == null)
					rootNodes.put(c, child);
			
				// il nodo creato va aggiunto alla mappa allNodes
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
			
		// se sono alla ultima lettera, marco il nodo come nodo tail
		if (i == token.length() - 1)
			child.setTail();
		
		// processo il successivo carattere
		processTokenChar(token, i + 1, child);
	}
	
	/**
	 * Cerca tutti i token nell'indice che matchano una chiave di ricerca 
	 * @param searchKey La chiave di ricerca
	 * @return La lista dei token che matchano
	 */
	public ArrayList<String> search(String searchKey) { 
		ArrayList<String> list = new ArrayList<String>();
		
		// se il token è vuoto, esco
		if (searchKey.length() == 0)
			return list;
		
		// prendo il primo carattere
		char c = searchKey.charAt(0);
		
		// se non c'è nell'albero allNodes, la ricerca non dà alcun risultato
		if (!allNodes.containsKey(c))
			return list;
		
		// per ogni nodo in allNodes, inizio una visita ricorsiva
		// dell'albero
		for (IdxNode node: allNodes.get(c)) {
			// costruisco un nodo fittizio, da passare all'algoritmo ricorsivo
			// come parent del nodo da visitare
			IdxNode tempParentNode = new IdxNode(c, null);
			
			// aggiungo ai suoi figli il nodo da visitare
			tempParentNode.getChildren().put(c, node);
			
			// inizio la ricerca ricorsiva
			searchTokenChar(searchKey, 0, tempParentNode, list);
		}
		
		return list;
	}

	/**
	 * Metodo ricorsivo di ricerca dei token nell'indice
	 * @param searchKey La chiave di ricerca
	 * @param i Il carattere correntemente ricercato
	 * @param node Il nodo correntemente analizzato
	 * @param list La lista da riempire con i match a mano a mano trovati
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
	 * Dato un nodo, trova tra i suoi discendenti tutti nodi che hanno tail a true
	 * @param child Il nodo del quale vengono analizzati i discendenti
	 * @param list La lista da riempire con i match a mano a mano trovati
	 */
	private void searchAllTailsInChildren(IdxNode child, ArrayList<String> list) {
		for(IdxNode node: child.getChildren().values()) {
			if (node.isTail())
				list.add(getTokenFromNode(node));
			searchAllTailsInChildren(node, list);
		}
	}

	/**
	 * Ricostruisce la parola a partire da un nodo, risalendo l'albero fino alla radice.
	 * @param child Il nodo dal quale risalire l'albero verso la radice.
	 * @return La parola ricostruita.
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
