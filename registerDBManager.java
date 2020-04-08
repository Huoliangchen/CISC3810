package register;

import java.sql.*;
import static register.registerMain.connectDB;
import java.util.Scanner;
/*
A class that contain some methods to manage the register database.
 */

public class registerDBManager {
    public Connection con;
    public Statement stmt;
    public ResultSet rs;

    // constructor that connect to database whenever object is created.
    public registerDBManager() {

        con = connectDB();
        stmt = null;
        rs =null;
    }

    //method to add a class by accepting one int parameter
    public void addClass(int user){

        showClassTable();
        System.out.println("Please select the Class by entering the ClassNo.");
        Scanner scanner = new Scanner(System.in);
        int classNo = scanner.nextInt();

        if(findClassRequirement(classNo) == 0){

            insertClassIntoSchedule(user,classNo);

        } else if (checkEligibilityFortheClass(classNo)){

            insertClassIntoSchedule(user, classNo);

        } else {

            int classReqNo = findClassRequirement(classNo);
            String className = getClassNameFromRequirementTable(classReqNo);
            System.out.println("In order to enroll the class, you have to take " + className + " first!\nPlease check the class requirement carefully!");

        }
    }

    //method to drop a class from student schedule, accept one int parameter
    public void dropClass(int user){

        showSchedule(user);
        System.out.println("Please enter the classNo that you want to drop");
        Scanner scanner = new Scanner(System.in);
        int classNo = scanner.nextInt();

        try{
            stmt =null;
            stmt = con.createStatement();
            String sql = " select * from schedule where classNo ='"+ classNo +"'&& studentID = '"+ user +"'";
            rs = stmt.executeQuery(sql);
            if(rs.next()){
                String sqlUpdate = " Delete from schedule where classNo ='"+ classNo +"'&& studentID = '"+ user +"'";
                stmt.executeUpdate(sqlUpdate);
                System.out.println("You dropped the Class\n");
            }else{

                System.out.println("ERROR! Please make sure the class Number is CORRECT!\n");
            }

        }catch(SQLException e){

            System.out.println(e.getMessage()+"ERROR! Please make sure the class Number is CORRECT!\n");
            dropClass(user);
        }

    }

    //method to show class requirement table.
    public void showClassRequirement(){

        try{
            stmt = con.createStatement();
            String sql = "select * from class_requirement" ;
            rs = stmt.executeQuery(sql);
            System.out.println("Class No    Class Name    ClassReq No    ClassReq Name");

            while(rs.next()){

                int classNo = rs.getInt("classNo");
                String className = rs.getString("className");
                int classNoReq = rs.getInt("ClassNoReq");
                String classReqName = rs.getString("classReqName");
                System.out.println("   "+classNo +"           " + className + "            " + classNoReq + "               " + classReqName);
            }
            System.out.println("");

        }catch(SQLException e){

            System.out.println(e.getMessage());
        }
    }

    //method to show student's class history.
    public void showMyHistory(int user){
        try{
            stmt = con.createStatement();
            String sql = "select a.studentID, a.lastname, a.firstname, a.classNo, b.className, b.credit, a.grade, b.instructor, b.term from class_history a inner join class b on a.classNo = b.classNo where studentID ='" + user+"'";
            rs = stmt.executeQuery(sql);

            System.out.println("StudentID   Last Name   First Name   ClassNO   Class Name   Credit   Grade   Instructor   Term");

            while(rs.next()){

                int studentID = rs.getInt("studentID");
                String lastname = rs.getString("lastname");
                String firstname = rs.getString("firstname");
                int classNO = rs.getInt("ClassNo");
                String className = rs.getString("className");
                int credit = rs.getInt("credit");
                String grade = rs.getString("grade");
                String instructor = rs. getString("instructor");
                String term = rs.getString("term");
                System.out.println("       "+studentID +"      " + lastname + "          " + firstname
                        + "          " + classNO + "          " + className + "       " + credit + "       "
                        + grade + "      " + instructor + "     " + term);
            }
            System.out.println("");

        }catch(SQLException e){

            System.out.println(e.getMessage());
        }
    }

    //method to show student's schedule.
    public void showSchedule(int user) {
        try {
            stmt = con.createStatement();
            String sql = "select a.classNo, b.className, b.start_time, b.end_time, b.location, b.instructor from schedule a inner join class b on a.classNo = b.classNo where studentID ='" + user + "'";
            rs = stmt.executeQuery(sql);

            System.out.println("ClassNO  ClassName  Start Time  End Time  Location  Instructor");

                while (rs.next()) {
                    int classNo = rs.getInt("classNO");
                    String className = rs.getString("className");
                    Time start_time = rs.getTime("start_time");
                    Time end_time = rs.getTime("end_time");
                    String location = rs.getString("location");
                    String instructor = rs.getString("instructor");
                    System.out.println("     " + classNo + "     "+className + "    " +
                            start_time + "    " + end_time + "   " + location + "   " + instructor);
                }

            System.out.println("");

        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }

    }

    //private method to show class that are provided
    private void showClassTable(){

        try {
            stmt = con.createStatement();
            String sql = "select * from class";
            rs = stmt.executeQuery(sql);

            System.out.println("ClassNO  ClassName  Credit  Instructor   Start Time     End Time      Term      Location");

            while (rs.next()) {
                int classNo = rs.getInt("classNO");
                String className = rs.getString("className");
                int credit = rs.getInt("credit");
                String instructor = rs.getString("instructor");
                Time start_time = rs.getTime("start_time");
                Time end_time = rs.getTime("end_time");
                String term = rs.getString("term");
                String location = rs.getString("location");

                System.out.println("     " + classNo + "     "+className + "      " + credit +
                                    "       " + instructor + "       " + start_time + "      " + end_time +
                                    "   " + term + "    "+ location);
            }

            System.out.println("");

        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }
    }

    //method to check if the student eligibility for the class they want to enrolled
    private boolean checkEligibilityFortheClass(int classNo){

        int classNoReq = findClassRequirement(classNo);
        boolean isEligibility = false;
        try {
            stmt = con.createStatement();
            String sql = "Select classNo from class_history where classNo ='" + classNoReq + "'";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {

                isEligibility = true;

            }

        } catch (SQLException e) {

            System.out.println(e);
        }
        return isEligibility;
    }

    //method to return a corresponding class name for an input class number.
    private int findClassRequirement(int classNo){

        int classNumber = 0 ;

        try {
            stmt = con.createStatement();
            String sql = "Select classNoReq from class_requirement where classNo ='" + classNo + "'";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {

                classNumber = rs.getInt("classNoReq");
            }

        } catch (SQLException e) {

            System.out.println(e);
        }

        return classNumber;
    }

    // method to return a class name for the class number from class_requirement table
    private String getClassNameFromRequirementTable(int classNoReq){

        String className = null;
        try {
            stmt = con.createStatement();
            String sql = "Select classReqName from class_requirement where classNoReq ='" + classNoReq + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                className = rs.getString("classReqName");
            }
        } catch (SQLException e) {

            System.out.println(e);
        }
        return className;
    }

    //method to insert data into class schedule table.
    private void insertClassIntoSchedule(int user, int classNo){

        int classNumb;
        Time startTime;
        Time endTime;

        // get information from the class table for the use of insert into schedule
        try{
            stmt = con.createStatement();
            String sql = "select classNo, start_time, end_time from class where classNo ='" + classNo +"'";
            rs = stmt.executeQuery(sql);

            if(rs.next()){
                 classNumb = rs.getInt("classNo");
                 startTime = rs.getTime("start_Time");
                 endTime = rs.getTime("end_time");
                String insertSql = "insert into schedule Values('" +
                        user + "','" + classNumb + "','" + startTime + "','" + endTime + "')";
                stmt.executeUpdate(insertSql);
                System.out.println("SUCCESSFULLY ENROLLED!");
            }

        }catch(SQLException e){

            System.out.println(e.getMessage() + "\nYou may have this class in your schedule already!");
        }
    }
}





