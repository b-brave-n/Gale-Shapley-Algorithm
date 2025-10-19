
import java.util.*;


public class GS {

	public static void main(String[] args) {
		String[] menlist = {"xavier", "yancey", "zeus"};
		String[] womenlist = {"amy", "bertha", "clare"};
		
		HashMap<String, String[]> thepref = new HashMap<>(); //for standard GS algorithm input
		String[][] names = {
				{"amy", "bertha", "clare"}, //xavier
				{"bertha", "amy", "clare"}, //yancey
				{"amy", "bertha", "clare"}, //zeus
				{"yancey", "xavier", "zeus"}, //amy
				{"xavier", "yancey", "zeus"}, //bertha
				{"xavier", "yancey", "zeus"} //clare
		};
		thepref.put("xavier", names[0]);
		thepref.put("yancey", names[1]);
		thepref.put("zeus", names[2]);
		thepref.put("amy", names[3]);
		thepref.put("bertha", names[4]);
		thepref.put("clare", names[5]);
		
		String[][] blocked = { //for blocked GS algorithm
				{"xavier", "clare"}, {"zeus", "clare"}, {"zeus", "amy"}
		};
		
		HashMap<String, String[][]> thepreftie = new HashMap<>(); //for GS algorithm dealing with ties
		String[][][] names2 = {
				{{"bertha"}, {"amy"}, {"clare"}},
				{{"amy", "bertha"}, {"clare"}},
				{{"amy"}, {"bertha", "clare"}},
				{{"zeus", "xavier", "yancey"}},
				{{"zeus"}, {"xavier"}, {"yancey"}},
				{{"xavier", "yancey"}, {"zeus"}}
		};
		thepreftie.put("xavier", names2[0]);
		thepreftie.put("yancey", names2[1]);
		thepreftie.put("zeus", names2[2]);
		thepreftie.put("amy", names2[3]);
		thepreftie.put("bertha", names2[4]);
		thepreftie.put("clare", names2[5]);
		
		HashMap<String, String> match = gs(menlist, womenlist, thepref);
		System.out.print("{ ");
		for(String w: match.keySet()) {
			System.out.printf("%s: %s, ", w, match.get(w));
		}
		System.out.println("}");
		
		
		HashMap<String, String> match_block = gs_block(menlist, womenlist, thepref, blocked);
		System.out.print("{ ");
		for(String w: match_block.keySet()) {
			System.out.printf("%s: %s, ", w, match_block.get(w));
		}
		System.out.println("}");
		
		HashMap<String, String> match_tie = gs_tie(menlist, womenlist, thepreftie);
		System.out.print("{ ");
		for(String w: match_tie.keySet()) {
			System.out.printf("%s: %s, ", w, match_tie.get(w));
		}
		System.out.println("}");
	}
	
	/*
	 * Original Gale-Shapley Algorithm
	 * Inputs: men (array of men's names)
	 *         women (array of women's names)
	 *         pref (dictionary of preferences mapping names to list of preferred names in sorted order
	 * Output: a Dictionary (HashMap) of stable matches
	 */
	public static HashMap<String, String> gs(String[] men, String[] women, 
			HashMap<String, String[]> pref) {
		//Dictionary (HashMap) to store results
		//first String indicates the woman (key)
		//second String indicates the matched man (value)
		//Please study the use of Java HashMap class 
		HashMap<String, String> S = new HashMap<>(); 
		
		// build the rank dictionary
		HashMap<String, HashMap<String, Integer>> rank = new HashMap<>();
		for(String w: women) {
			HashMap<String, Integer> mrank = new HashMap<>();
			int i = 1;
			for(String m: pref.get(w)) {
				mrank.put(m, i);
				i++;
			}
			rank.put(w, mrank);
		}
		
		// create a pointer to the next woman to propose
		HashMap<String, Integer> prefptr = new HashMap<>();
		for(String m: men) {
			prefptr.put(m, 0); //all starting from 0
		}
		
		// create a freemen list as we did in class
		ArrayList<String> freemen = new ArrayList<String>();
		for(String m: men) {
			freemen.add(m);
		}

		
		//algorithm start here
		while(freemen.size()>0) {
			String m = freemen.remove(0); //always remove from the front end
			int currentPtr = prefptr.get(m);
			String w = pref.get(m)[currentPtr];
			prefptr.put(m, currentPtr+1);
			if(!S.containsKey(w)) S.put(w, m);
			else {
				String mprime = S.get(w);
				if(rank.get(w).get(m)<rank.get(w).get(mprime)) {
					S.put(w, m);
					freemen.add(mprime); //put the previous man back to freemen list
				}
				else {
					freemen.add(m); //put the current man back to freemen list
				}
			}
		}
		
		return S;
	}
	
	/*
	 * Modified Gale-Shapley Algorithm to handle unacceptable matches
	 * Inputs: men (array of men's names)
	 *         women (array of women's names)
	 *         pref (dictionary of preferences mapping names to list of preferred names in sorted order
	 *         blocked (2D array of blocked pairs)
	 * Output: a Dictionary (HashMap) of stable matches        
	 */
	public static HashMap<String, String> gs_block(String[] men, String[] women, 
			HashMap<String, String[]> pref, String[][] blocked) {
		// This is where I'll store the final matches
		HashMap<String, String> S = new HashMap<>(); 
		
		// 1st, make a Set of the blocked pairs for easy checking
		Set<String> blockedPairs = new HashSet<>();
		for(String[] pair : blocked) {
			blockedPairs.add(pair[0] + "," + pair[1]);
		}
		
		// build the women's rank list so I can compare who they prefer
		HashMap<String, HashMap<String, Integer>> rank = new HashMap<>();
		for(String w: women) {
			HashMap<String, Integer> mrank = new HashMap<>();
			int i = 1;
			for(String m: pref.get(w)) {
				mrank.put(m, i);
				i++;
			}
			rank.put(w, mrank);
		}
		
		// need a pointer to keep track of which woman each guy has proposed to
		HashMap<String, Integer> prefptr = new HashMap<>();
		for(String m: men) {
			prefptr.put(m, 0); 
		}
		
		// list of men who are still single
		ArrayList<String> freemen = new ArrayList<String>();
		for(String m: men) {
			freemen.add(m);
		}

		// start the main loop
		while(freemen.size() > 0) {
			String m = freemen.remove(0); 
			
			// find the next woman on the guys' list while skipping any blocked ones
			String w = null;
			int currentPtr = prefptr.get(m);
			while(currentPtr < pref.get(m).length) {
				String potentialW = pref.get(m)[currentPtr];
				if(!blockedPairs.contains(m + "," + potentialW)) {
					w = potentialW;
					prefptr.put(m, currentPtr + 1);
					break;
				}
				currentPtr++;
			}
			
			// if found a woman who's not blocked, it'll continue
			if(w != null) {
				if(!S.containsKey(w)) {
					S.put(w, m);
				} else {
					String mprime = S.get(w);
					if(rank.get(w).get(m) < rank.get(w).get(mprime)) {
						S.put(w, m);
						freemen.add(mprime); 
					} else {
						freemen.add(m); 
					}
				}
			}
		}
		
		return S;
	}



//GS_TIE METHOD 
	public static HashMap<String, String> gs_tie(String[] men, String[] women, 
			HashMap<String, String[][]> preftie) {
		
		//final matches
		HashMap<String, String> S = new HashMap<>(); 
		
		//give everyone in a tie the same rank
		HashMap<String, HashMap<String, Integer>> rank = new HashMap<>();
		for(String w: women) {
			HashMap<String, Integer> mrank = new HashMap<>();
			int i = 1;
			for(String[] tieSet : preftie.get(w)) {
				for(String m : tieSet) {
					mrank.put(m, i);
				}
				i++;
			}
			rank.put(w, mrank);
		}
		
		//pointer to track which preference group each man is on
		HashMap<String, Integer> prefptr = new HashMap<>();
		for(String m: men) {
			prefptr.put(m, 0); 
		}
		
		//free guys.
		ArrayList<String> freemen = new ArrayList<String>();
		for(String m: men) {
			freemen.add(m);
		}
		
		HashMap<String, Set<String>> proposedInTieSet = new HashMap<>();
		for(String m: men) {
			proposedInTieSet.put(m, new HashSet<>());
		}
		
		//loop goes through
		while(freemen.size() > 0) {
			String m = freemen.remove(0); 
			
			//next best woman to propose to
			String w = null;
			int currentPtr = prefptr.get(m);
			
			//go through the current tied group first
			String[] currentTieSet = preftie.get(m)[currentPtr];
			for (String potentialW : currentTieSet) {
				if (!proposedInTieSet.get(m).contains(potentialW)) {
					w = potentialW;
					proposedInTieSet.get(m).add(potentialW);
					break;
				}
			}
			
			if(w == null) {
				prefptr.put(m, currentPtr + 1);
				freemen.add(m); 
				continue;
			}
			
			if(!S.containsKey(w)) {
				S.put(w, m);
			} else {
				String mprime = S.get(w);
				
				int rankM = rank.get(w).get(m);
				int rankMprime = rank.get(w).get(mprime);
				
				if(rankM < rankMprime) {
					S.put(w, m);
					freemen.add(mprime); 
				} else {
					freemen.add(m); 
				}
			}
		}
		
		return S;
	}
}
