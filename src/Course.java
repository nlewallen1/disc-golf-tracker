import java.util.InputMismatchException;
import java.util.Scanner;

// Hold information about the course
public class Course {
    Scanner input = new Scanner(System.in);
    private int holeCount;
    private String name;
    private int[] holes;
    private int totalPar;


    // set holeCount
    public int getHoleCount() {
        return holeCount;
    }

    // get holeCount
    public void setHoleCount(int holeCount) {
        this.holeCount = holeCount;
    }

    // set name
    public void setName(String name) {
        this.name = name;
    }

    // get name
    public String getName() {
        return name;
    }

    // get totalPar
    public int getTotalPar() {
        return totalPar;
    }

    // set par for each hole
    public void setHoles() {
        // initialize array with the amount of holes on the course
        holes = new int[holeCount];

        // get par for each hole
        for (int i = 0; i < holeCount; i++) {
            while (true) {
                System.out.println("Enter par for hole " + (i + 1) + ":");
                try {
                    int par = input.nextInt();
                    holes[i] = par;
                    totalPar += par;
                    break; // good input
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input, please enter an integer");
                    input.nextLine();
                }
            }
        }
    }

    // show each hole, par for it
    public void getHoleResults() {
        for (int i = 0; i < holeCount; i++) {
            System.out.println("Hole " + (i + 1) + " - Par " + holes[i]);
        }
    }

    public int getHolePar(int num) {
        return holes[num];
    }
}
