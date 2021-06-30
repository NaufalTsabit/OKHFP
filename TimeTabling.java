import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TimeTabling {

    static String folderDataset = "D:/Kuliah/OKH/FP/Toronto/";
    static String namafile[][] = {	{"car-f-92", "Carleton92"}, {"car-s-91", "Carleton91"}, {"ear-f-83", "EarlHaig83"}, {"hec-s-92", "EdHEC92"}, 
									{"kfu-s-93", "KingFahd93"}, {"lse-f-91", "LSE91"}, {"pur-s-93", "pur93"}, {"rye-s-93", "rye92"}, {"sta-f-83", "St.Andrews83"},
									{"tre-s-92", "Trent92"}, {"uta-s-92", "TorontoAS92"}, {"ute-s-92", "TorontoE92"}, {"yor-f-83", "YorkMills83"}
								};
    
    static int timeslot[]; // fill with course & its timeslot
    static int[][] conflict_matrix, course_sorted, hasil_timeslot;
	
	private static Scanner scanner;
	
    public static void main(String[] args) throws IOException {
        scanner = new Scanner(System.in);
        for	(int i=0; i< namafile.length; i++)
        	System.out.println(i+1 + ". Penjadwalan " + namafile[i][1]);
        
        System.out.print("\nSilahkan pilih file untuk dijadwalkan : ");
        int pilih = scanner.nextInt();
        
        String filePilihanInput = namafile[pilih-1][0];
        String filePilihanOutput = namafile[pilih-1][1];
        
        String file = folderDataset + filePilihanInput;
        
        
		
        Course course = new Course(file);
        int jumlahexam = course.getJumlahCourse();
        
        conflict_matrix = course.getConflictMatrix();
        int jumlahmurid = course.getJumlahMurid();
        

		course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);
		
		
		
		long starttimeLargestDegree = System.nanoTime();
		Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		long endtimeLargestDegree = System.nanoTime();
		
		
		Optimization optimization = new Optimization(file, conflict_matrix, course_sorted, jumlahexam, jumlahmurid, 1000000);
		
		long starttimeHC = System.nanoTime();
		optimization.getTimeslotByHillClimbing(); 
		long endtimeHC = System.nanoTime();
		
		
		
		
		long starttimeTS = System.nanoTime();
		optimization.getTimeslotByTabuSearch();
		long endtimeTS = System.nanoTime();
		double minus = Evaluator.getPenalty(conflict_matrix, optimization.getTimeslotHillClimbing(), jumlahmurid) - Evaluator.getPenalty(conflict_matrix, optimization.getTimeslotTabuSearch(), jumlahmurid);
		double delta = minus/Evaluator.getPenalty(conflict_matrix, optimization.getTimeslotHillClimbing(), jumlahmurid);
		
		System.out.println("PENJADWALAN UNTUK " + filePilihanOutput + "\n");
		
		System.out.println("Timeslot dibutuhkan (menggunakan \"Constructive Heuristics\") 	: " + schedule.getJumlahTimeSlot(schedule.getSchedule()));
		System.out.println("Penalti \"Constructive Heuristics\" 				: " + Evaluator.getPenalty(conflict_matrix, schedule.getSchedule(), jumlahmurid));
		System.out.println("Waktu eksekusi yang dibutuhkan \"Constructive Heuristics\" " + ((double) (endtimeLargestDegree - starttimeLargestDegree)/1000000000) + " detik.\n");
		
		System.out.println("Timeslot dibutuhkan (menggunakan Hill Climbing) 		: " + optimization.getJumlahTimeslotHC());
		System.out.println("Penalti Hill Climbing 						: " + Evaluator.getPenalty(conflict_matrix, optimization.getTimeslotHillClimbing(), jumlahmurid));
		System.out.println("Waktu eksekusi yang dibutuhkan Hill Climbing " + ((double) (endtimeHC - starttimeHC)/1000000000) + " detik.\n");
		
		System.out.println("Timeslot dibutuhkan (menggunakan Tabu Search) 			: " + optimization.getJumlahTimeslotTabuSearch());
		System.out.println("Penalti Tabu Search 						: " + Evaluator.getPenalty(conflict_matrix, optimization.getTimeslotTabuSearch(), jumlahmurid));
		System.out.println("Waktu eksekusi yang dibutuhkan Tabu Search " + ((double) (endtimeTS - starttimeTS)/1000000000) + " detik.");

		System.out.println("perbandingan Hill Climbing dengan Tabu Search (delta): " + delta);
    }
    
    public static void writeSolFile(int[][] hasiltimeslot, String namaFileOutput) throws IOException {
    	String directoryOutput = "C:/Users/ZAP/Google Drive/KULIAH/OKH/Tugas/UAS/ExamTimetableEvaluation/" + namaFileOutput +".sol";
        FileWriter writer = new FileWriter(directoryOutput, true);
        for (int i = 0; i < hasiltimeslot.length; i++) {
            for (int j = 0; j < hasiltimeslot[i].length; j++) {
                  writer.write(hasiltimeslot[i][j]+ " ");
            }
            writer.write("\n");
        }
        writer.close();
        
		System.out.println("\nFile penjadwalan " + namaFileOutput+ " berhasil dibuat");
	}
    
    public static void writePenaltyListFile(double[] penaltyList, String namaFileOutput) throws IOException {
    	String directoryOutput = "C:/Users/ZAP/Google Drive/KULIAH/OKH/Tugas/UAS/" + namaFileOutput +".txt";
        FileWriter writer = new FileWriter(directoryOutput, true);
        
        for (int j = 0; j < penaltyList.length; j++) {
            writer.write(penaltyList[j]+ " ");
            writer.write("\n");
//        	System.out.println(penaltyList[j]);
        }
        writer.close();
        
		System.out.println("\nFile list penalty " + namaFileOutput+ " berhasil dibuat");
	}
}