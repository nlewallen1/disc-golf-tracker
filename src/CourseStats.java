// Keep track of stats, like times played, total throws, stats for each hole
public class CourseStats {
    private Course course;
    private int timesPlayed;
    private double averageScore;
    private int totalThrows;

    // construtor, link course
    public CourseStats (Course course) {
        this.course = course;
    }
}

