import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("1. Add course");
        System.out.println("2. Add round");

        int choice = input.nextInt();
        input.nextLine();

        // menu
        switch (choice) {
            case 1:
                // create new course class
                Course newcourse = new Course();

                System.out.println("Enter course name");
                newcourse.setName(input.nextLine());

                System.out.println("Enter number of holes on course");
                newcourse.setHoleCount(input.nextInt());

                // ask for par for each hole
                newcourse.setHoles();

                System.out.println("New course added!");

                // TEMP DISPLAY COURSE
                System.out.println(newcourse.getName() + " (Par " + newcourse.getTotalPar() + ")");
                newcourse.getHoleResults();
                break;
            case 2:
                System.out.println("What course did you play?");
                // FIXME let user choose from list of added courses
                NewRound newround = NewRound()
        }
    }
}