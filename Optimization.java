import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Optimization {
	int[][] timeslotHillClimbing, conflict_matrix, course_sorted;
	int[] timeslot;
	String file;
	int jumlahexam, randomCourse, randomTimeslot;
	
	Optimization(String file) { this.file = file; }
	
	
	public void getTimeslotByHillClimbing(int[][] conflict_matrix, int[][] course_sorted, int jumlahexam, int jumlahmurid, int iterasi) throws IOException {
		
		
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted, timeslot);
		
		
		
		timeslotHillClimbing = schedule.getSchedule(); 
		int[][] timeslotHillClimbingSementara = new int[timeslotHillClimbing.length][2]; 
		
		
		for (int i = 0; i < timeslotHillClimbingSementara.length; i ++) {
			timeslotHillClimbingSementara[i][0] = timeslotHillClimbing[i][0]; 
			timeslotHillClimbingSementara[i][1] = timeslotHillClimbing[i][1]; 
		}
		
		double penaltiInitialFeasible = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid);
		
		for(int i = 0; i < iterasi; i++) {
			
			try {
				randomCourse = randomNumber(0, jumlahexam); // random course
				randomTimeslot = randomNumber(0, schedule.getHowManyTimeSlot(timeslot)); // random timeslot
				timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
			
				if (Schedule.checkRandomTimeslot(randomCourse, randomTimeslot, conflict_matrix, timeslotHillClimbing)) {	
					timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
					double penaltiAfterHillClimbing = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
					
					if(penaltiInitialFeasible > penaltiAfterHillClimbing) {
						penaltiInitialFeasible = penaltiAfterHillClimbing;
						timeslotHillClimbing[randomCourse][1] = timeslotHillClimbingSementara[randomCourse][1];
					} 
						else 
							timeslotHillClimbingSementara[randomCourse][1] = timeslotHillClimbing[randomCourse][1];
				}
				
				System.out.println("Iterasi ke " + (i+1) + " memiliki penalti : "+penaltiInitialFeasible);
			}
				catch (ArrayIndexOutOfBoundsException e) {
					
				}
			
		}
		
		
		System.out.println("\n================================================\n");
    	for (int course_index = 0; course_index < jumlahexam; course_index++)
    		System.out.println("Timeslot untuk course "+ timeslotHillClimbing[course_index][0] +" adalah timeslot: " + timeslotHillClimbing[course_index][1]);       
	    
    	System.out.println("\n================================================"); 
	    System.out.println("Jumlah timeslot yang dibutuhkan untuk menjadwalkan " + jumlahexam + " course di file " + file + " adalah "+ Arrays.stream(timeslot).max().getAsInt() + " timeslot.");
		System.out.println("Penalti akhir : " + penaltiInitialFeasible); // print latest penalti
	}
	
	
	public static int randomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
	
	
	
}
