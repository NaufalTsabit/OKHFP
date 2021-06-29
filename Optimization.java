import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Optimization {
	int[][] timeslotHillClimbing, timeslotTabuSearch, initialTimeslot, conflict_matrix, course_sorted;
	int[] timeslot;
	double[] tabuSearchPenaltyList1;
	String file;
	int jumlahexam, jumlahmurid, randomCourse, randomTimeslot, iterasi;
	double initialPenalty, bestPenalty, deltaPenalty;
	
	Schedule schedule;
	
	Optimization(String file, int[][] conflict_matrix, int[][] course_sorted, int jumlahexam, int jumlahmurid, int iterasi) { 
		this.file = file; 
		this.conflict_matrix = conflict_matrix;
		this.course_sorted = course_sorted;
		this.jumlahexam = jumlahexam;
		this.jumlahmurid = jumlahmurid;
		this.iterasi = iterasi;
	}
	
	
	public void getTimeslotByHillClimbing() throws IOException {
		schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		
		int[][] initialTimeslot = schedule.getSchedule(); 
		timeslotHillClimbing = Evaluator.getTimeslot(initialTimeslot);
		initialPenalty = Evaluator.getPenalty(conflict_matrix, initialTimeslot, jumlahmurid);
		
		int[][] timeslotHillClimbingSementara = Evaluator.getTimeslot(timeslotHillClimbing); 
		
		bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid);
		
		for(int i = 0; i < iterasi; i++) {
			try {
				randomCourse = random(jumlahexam); 
				randomTimeslot = random(schedule.getJumlahTimeSlot(initialTimeslot)); 
				
				if (Schedule.checkRandomTimeslot(randomCourse, randomTimeslot, conflict_matrix, timeslotHillClimbingSementara)) {	
					timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
					double penaltiAfterHillClimbing = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
					
					
					if(bestPenalty > penaltiAfterHillClimbing) {
						bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
						timeslotHillClimbing[randomCourse][1] = timeslotHillClimbingSementara[randomCourse][1];
					} 
						else 
							timeslotHillClimbingSementara[randomCourse][1] = timeslotHillClimbing[randomCourse][1];
				}
				System.out.println("Iterasi ke " + (i+1) + " memiliki penalti : "+ bestPenalty);
			}
				catch (ArrayIndexOutOfBoundsException e) {
					
				}
			
		}
		
		deltaPenalty = ((initialPenalty-bestPenalty)/initialPenalty)*100;
		
		
    	System.out.println("=============================================================");
		System.out.println("		Metode HILL CLIMBING								 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : "+ bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti : " + deltaPenalty + " % dari inisial solusi" + "\n");
		System.out.println("Timeslot yang dibutuhkan : " + schedule.getJumlahTimeSlot(timeslotHillClimbing) + "\n");
		System.out.println("=============================================================");
		
	}
	
	
	
	public void getTimeslotByTabuSearch() {
		schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		
		
		timeslotTabuSearch = schedule.getSchedule();
		initialPenalty = Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid);
		
		int[][] bestTimeslot = Evaluator.getTimeslot(timeslotTabuSearch); // handle current best timeslot
		int[][] bestcandidate  = Evaluator.getTimeslot(timeslotTabuSearch);
		int[][] timeslotTabuSearchSementara = Evaluator.getTimeslot(timeslotTabuSearch);
		

		
		
        LinkedList<int[][]> tabulist = new LinkedList<int[][]>();
        int maxtabusize = 10;
        tabulist.addLast(Evaluator.getTimeslot(timeslotTabuSearch));
        
      
        int maxiteration = 1000000;
        int iteration=0;
        
      
        double penalty1 = 0;
        double penalty2 = 0;
        double penalty3 = 0;
        
        boolean terminate = false;
        
        while(!terminate){
            iteration++;
            

           ArrayList<int[][]> sneighborhood = new ArrayList<>();

        	LowLevelHeuristics lowLevelHeuristics = new LowLevelHeuristics(conflict_matrix);
        	timeslotTabuSearchSementara = lowLevelHeuristics.move1(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.swap2(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.move2(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.swap3(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.move3(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
				
        		
        		
           int j = 0;
           while (sneighborhood.size() > j) {

               if( !(tabulist.contains(sneighborhood.get(j))) && 
            		   Evaluator.getPenalty(conflict_matrix, sneighborhood.get(j), jumlahmurid) < Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid))
                 bestcandidate = sneighborhood.get(j);
                	
               j++;
           }
                
           sneighborhood.clear();
                
           
           if(Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid) < Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid))
              timeslotTabuSearch = Evaluator.getTimeslot(bestcandidate);
                
           
           tabulist.addLast(bestcandidate);
           if(tabulist.size() > maxtabusize)
              tabulist.removeFirst();
                
           
           tabuSearchPenaltyList1 = new double[100];
           if ((iteration+1)%10 == 0)
               System.out.println("Iterasi: " + (iteration+1) + " memiliki penalty " + Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid));


           
           if (iteration == maxiteration) 
        	   terminate = true;
        }
        bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid);
        deltaPenalty = ((initialPenalty-bestPenalty)/initialPenalty)*100;
        
        System.out.println("=============================================================");
		System.out.println("		Metode TABU SEARCH						 			 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : " + bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti : " + deltaPenalty + " % dari inisial solusi");
		System.out.println("Timeslot yang dibutuhkan : " + schedule.getJumlahTimeSlot(timeslotTabuSearch) + "\n");
		System.out.println("=============================================================");
	}
	
	
	public int[][] getTimeslotHillClimbing() { return timeslotHillClimbing; }
	public int[][] getTimeslotTabuSearch() { return timeslotTabuSearch; }
	
	
	public int getJumlahTimeslotHC() { return schedule.getJumlahTimeSlot(timeslotHillClimbing); }
	public int getJumlahTimeslotTabuSearch() { return schedule.getJumlahTimeSlot(timeslotTabuSearch); }
	
	public double[] getTabuSearchPenaltyList() { 
		return tabuSearchPenaltyList1;
	}
	
	
}
