package dwalsh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MySQLAccess {
    //
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public void readDataBase() throws Exception {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/project?"
                            + "user=pace&password=123456");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select * from project.student_life");
            writeResultSet(resultSet);

            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  project.student_life values (?, ?, ?, ?, ? , ?, ?, ?)");
            // "myuser, webpage, datum, summary, COMMENTS from project.student_life");
            // Parameters start with 1
            preparedStatement.setInt(1, 2001);
            preparedStatement.setFloat(2, 2.2f);
            preparedStatement.setFloat(3, 2.2f);
            preparedStatement.setFloat(4, 2.2f);
            preparedStatement.setFloat(5, 2.2f);
            preparedStatement.setFloat(6, 2.2f);
            preparedStatement.setFloat(7, 2.2f);
            preparedStatement.setString(8, "low");
            preparedStatement.executeUpdate();
            

            preparedStatement = connect
                    .prepareStatement("SELECT * from project.student_life");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);

            // Remove again the insert comment
            preparedStatement = connect
            .prepareStatement("delete from project.student_life where Student_ID= ? ; ");
            preparedStatement.setInt(1, 2001);
            preparedStatement.executeUpdate();
            
            resultSet = statement
            .executeQuery("select * from project.student_life");
            writeMetaData(resultSet);
            
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query
        
        System.out.println("The columns in the table are: ");
        
        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            int student_id = resultSet.getInt("Student_ID");
            float studyHoursPerDay = resultSet.getFloat("Study_Hours_Per_Day");
            float extraHours = resultSet.getFloat("Extracurricular_Hours_Per_Day");
            float sleepHours = resultSet.getFloat("Sleep_Hours_Per_Day");
            float socialHours = resultSet.getFloat("Social_Hours_Per_Day");
            float physicalHours = resultSet.getFloat("Physical_Activity_Hours_Per_Day");
            float GPA  = resultSet.getFloat("GPA");
            String stress = resultSet.getString("Stress_Level");
            System.out.println("Student_ID: " + student_id);
            System.out.println("Study_Hours_Per_Day: " + studyHoursPerDay);
            System.out.println("Extracurricular_Hours_Per_Day: " + extraHours);
            System.out.println("Sleep_Hours_Per_Day: " + sleepHours);
            System.out.println("Social_Hours_Per_Day" + socialHours);
            System.out.println("Physical_Activity_Hours_Per_Day: " + physicalHours);
            System.out.println("GPA: " + GPA);
            System.out.println("Stress: " + stress);
        }
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) throws Exception {
        MySQLAccess dao = new MySQLAccess();
        dao.readDataBase();
    }
}

