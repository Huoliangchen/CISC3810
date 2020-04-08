package register;
import java.sql.*;
import java.util.Scanner;
/*
RegisterMain Class is a simple student course registration program.
It allows student to add class, drop class, view class history and view schedule.
it will also update the data in the backend database.
 */

public class registerMain {
    public static void main(String[] args) {

        boolean programState = true;
        boolean IsLogin = false;

        int user = 0;

        while (programState) {

            if(!IsLogin){

                Scanner obj = new Scanner(System.in);
                System.out.println("Please enter your Student ID.");
                user = obj.nextInt();
                Scanner obj2 = new Scanner(System.in);
                System.out.println("Please enter your Password.");
                String pw = obj2.nextLine();
                IsLogin = CheckLogin(user, pw);
                System.out.println("\n");


            } else {

                System.out.println("Please pick the action by entering number!");
                System.out.println(
                                "1. Add Class\n" +
                                "2. Drop Class\n" +
                                "3. Show Class Requirement\n" +
                                "4. My History.\n" +
                                "5. My Schedule\n" +
                                "6. Log Out");
                Scanner obj = new Scanner(System.in);
                int nb = obj.nextInt();
                registerDBManager Manager = new registerDBManager();
                switch(nb) {
                    case 1:
                        System.out.println("The class below are provided: ");
                        Manager.addClass(user);
                        break;
                    case 2:
                        System.out.println("Your current schedule:");
                        Manager.dropClass(user);
                        break;
                    case 3:
                        System.out.println("Class Requirement:");
                        Manager.showClassRequirement();
                        break;
                    case 4:
                        System.out.println("Your Class History:");
                        Manager.showMyHistory(user);
                        break;
                    case 5:
                        System.out.println("Your Schedule:");
                        Manager.showSchedule(user);
                        break;
                    case 6:
                        IsLogin = false;
                        System.out.println("You have been Log out!\n");
                        break;

                }
            }

        }

    }


        //function for connect database by JDBC and return a Connection type.
        public static Connection connectDB () {

            final String driver = "com.mysql.cj.jdbc.Driver";
            final String url = "jdbc:mysql://127.0.0.1:3306/registrardb";
            final String user = "root";
            final String password = "Huoliang5201314";
            Connection conn;
            try {

                Class.forName(driver);
                conn = DriverManager.getConnection(url, user, password);
                return conn;
            } catch (Exception e) {

                System.out.println("Error" + e.getMessage());
                return null;
            }
        }

        // function that check login information.
        public static boolean CheckLogin ( int id, String pw){
            boolean passwordIsRight = false;
            Connection con;
            Statement stmt;
            ResultSet rs;
            try {
                con = connectDB();
                stmt = con.createStatement();
                String sql = "Select * from student where studentID ='" + id + " '&& password = '" + pw + "'";
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    passwordIsRight = true;
                    System.out.println("You login successfully!");
                } else {
                    System.out.println("Wrong ID or Password.");
                }
            } catch (SQLException e) {

                System.out.println(e);
            }
            return passwordIsRight;
        }

}
