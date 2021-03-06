package mtree.index;


import similarity.highlevelop.HighLevelOP;

public class GraphDistance {

	static int graphDistance(Fragment fragment1, Fragment fragment2) {
		
		int distance = HighLevelOP.GetHighLevelOPDistance(fragment1.getGraph(), fragment2.getGraph());
		
		return distance;
	}


	public static void testTimer() {

		Timer t = new Timer();
		System.out.println("Indexing...");
		
		Timer.Times times = t.getTimes();
		System.out.printf("TIMES: %.2freal\n\n", times.real / 1000000000.0);

		times = t.getTimes();
		System.out.printf("TIMES: %.2freal\n\n", times.real / 1000000000.0);

	}
}
