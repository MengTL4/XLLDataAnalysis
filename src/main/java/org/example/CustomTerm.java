package org.example;

import java.time.LocalDate;

public class CustomTerm {
    static int firstSemesterStartMonth = 9;  // September
    static int firstSemesterEndMonth = 1;    // January
    static int secondSemesterStartMonth = 2; // February
    static int secondSemesterEndMonth = 6;   // June

    // Get the current date
    static LocalDate currentDate = LocalDate.now();
    static int currentYear = currentDate.getYear();
    static int currentMonth = currentDate.getMonthValue();

    // Determine the current semester based on the current month
    static int startMonth;
    static int endMonth;
    static int academicYear;
    public static boolean judgeTerm(){
        return currentMonth >= firstSemesterStartMonth || currentMonth <= firstSemesterEndMonth;
    }
    public static LocalDate countTermStart(){

        if (judgeTerm()) {
            startMonth = firstSemesterStartMonth;
            academicYear = currentYear;
        } else {
            startMonth = secondSemesterStartMonth;
            academicYear = currentYear - 1;
        }
        return LocalDate.of(academicYear, startMonth, 1);
    }
    public static LocalDate countTermEnd(){

        if (judgeTerm()) {
            startMonth = firstSemesterStartMonth;
            endMonth = firstSemesterEndMonth;
            academicYear = currentYear;
        } else {
            startMonth = secondSemesterStartMonth;
            endMonth = secondSemesterEndMonth;
            academicYear = currentYear - 1;
        }
        LocalDate startDate = LocalDate.of(academicYear, startMonth, 1);
        return LocalDate.of(academicYear + 1, endMonth, startDate.lengthOfMonth());
    }
}
