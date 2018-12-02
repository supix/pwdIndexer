import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Main {
	static int i = 0;
	public static void main(String[] args) throws Exception  {
		Indexer indexer = new Indexer();
		
		try (Stream<String> stream = Files.lines(Paths.get("passwords.file.txt"))) 
		{
			System.out.println("Indexing...");
			System.out.println("0 lines so far...");
			long start = System.nanoTime();
			stream.forEach(line -> {
	        	indexer.index(line);
				
	        	// stampa un messaggio in fase di lettura del file, ogni milione di righe lette
	        	i++;
	        	if (i % 1000000 == 0)
	        		System.out.println(i + " lines so far...");
	        });
			long end = System.nanoTime();
			long elapsed = (end - start) / 1000000;
	        System.out.println(i + " lines indexed in " + elapsed + "ms");
		}
		
		System.out.println("");
		System.out.println("Now searching...");
		printResults("bastimen", indexer);
		printResults("totor", indexer);
		printResults("BIK", indexer);
		printResults("sall", indexer);
		printResults("6731", indexer);
		printResults("stipula", indexer);
		printResults("t3050", indexer);
		printResults("ahtkwelotn", indexer);
		printResults("#$%()", indexer);
		
//		indexer.index("toppa");
//		indexer.index("top");
//		indexer.index("top");
//		indexer.index("tappo");
//		indexer.index("stappo");
//		indexer.index("strappo");
//		
//		printResults("toppa", indexer);
//		printResults("top", indexer);
//		printResults("op", indexer);
//		printResults("pp", indexer);
//		printResults("p", indexer);
	}

	private static void printResults(String key, Indexer indexer) {
		// takes the time before the search
		long start = System.nanoTime();
		
		// searches in the index
		ArrayList<String> results = indexer.search(key);
		
		// takes the time after the search
		long end = System.nanoTime();
		
		// computes the time in milliseconds
		long diff_msec = (end - start) / 1000000;
		
		// print search results and elapsed times
		System.out.print(key + " (" + results.size() + " found in " + diff_msec + "ms): ");
		System.out.print(String.join(", ", results));
		System.out.println();
	}
}
