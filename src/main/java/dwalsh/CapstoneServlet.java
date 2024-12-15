package dwalsh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet(urlPatterns = "/capstone.do")
public class CapstoneServlet extends HttpServlet {

    private Connection connect = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet result;
    private PreparedStatement checkExists = null;
    private PreparedStatement updateRow = null;
    private PreparedStatement createRow = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            String query = req.getParameter("Student_ID");
            int id = Integer.parseInt(query); 

            connect = DriverManager.getConnection("jdbc:mysql://localhost/project?"+ "user=pace&password=123456");

            preparedStatement = connect.prepareStatement("SELECT * FROM project.student_life WHERE Student_ID = ?");
            preparedStatement.setInt(1, id);

            result = preparedStatement.executeQuery();

            PrintWriter out = resp.getWriter();
            out.println("<html>");
            out.println("<head>");
            out.println("<title>");
            out.println("</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("Capstone Project.");
            out.println("<br>");
            out.println(printResults(result));
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            System.err.println(e);
        }
        


    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        PrintWriter out = resp.getWriter();

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost/project?"+ "user=pace&password=123456");

            StringBuilder jsondata = new StringBuilder();
            String jsonString;
            BufferedReader reader = req.getReader();
            String line;

            //Build the string
            while ((line = reader.readLine()) != null){
                jsondata.append(line);
            }
            jsonString = jsondata.toString();

            //assign it to a Student object
            ObjectMapper objectMapper = new ObjectMapper();
            Student student = objectMapper.readValue(jsonString, Student.class);
            
            out.println("Here is the JSON:");
            out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(student));

            //Check if the ID is already in the table
            checkExists = connect.prepareStatement("SELECT EXISTS( SELECT 1 FROM student_life WHERE Student_ID = ?) AS row_exists;");
            checkExists.setInt(1, student.ID);
            result = checkExists.executeQuery();
            result.next();
            boolean doesIDExist = result.getString("row_exists").equals("1");

            //If it does, update the row of the given id
            if (doesIDExist){
                out.println("Match Found, Updating student " + student.ID);
                updateRow = connect.prepareStatement("UPDATE student_life" 
                +" SET Study_Hours_Per_Day = " + student.study_Hours
                +", Extracurricular_Hours_Per_Day = " + student.ex_Hours
                +", Sleep_Hours_Per_Day = " + student.sleep_Hours
                +", Physical_Activity_Hours_Per_Day = " + student.phys_Hours
                +", Social_Hours_Per_Day = " + student.social_Hours
                +", GPA = " + student.GPA
                +", Stress_Level = '" + student.Stress + "'"
                +" WHERE Student_ID = " + student.ID + ";");
                out.println(updateRow.toString());
                updateRow.executeUpdate();

            }

            //If the id is not found, insert that row in the table.
            else {
                out.println("Match not found, inserting ID " + student.ID +" into table");
                createRow = connect.prepareStatement("INSERT INTO student_life "
                +"(Student_ID, Study_Hours_Per_Day, Extracurricular_Hours_Per_Day, Sleep_Hours_Per_Day, Physical_Activity_Hours_Per_Day, Social_Hours_Per_Day, GPA, Stress_Level) "
                +"VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
                createRow.setInt(1, student.ID);
                createRow.setFloat(2, student.study_Hours);
                createRow.setFloat(3, student.ex_Hours);
                createRow.setFloat(4, student.sleep_Hours);
                createRow.setFloat(5, student.phys_Hours);
                createRow.setFloat(6, student.social_Hours);
                createRow.setFloat(7, student.GPA);
                createRow.setString(8, student.Stress);
                createRow.executeUpdate();
                out.println("Finished Adding the row");
            }

            

        } catch (Exception e) {
            out.println(e);
        }


    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    public String printResults(ResultSet resultSet) throws SQLException{
        String answer = "";
        while (resultSet.next()) {

            int student_id = resultSet.getInt("Student_ID");
            float studyHoursPerDay = resultSet.getFloat("Study_Hours_Per_Day");
            float extraHours = resultSet.getFloat("Extracurricular_Hours_Per_Day");
            float sleepHours = resultSet.getFloat("Sleep_Hours_Per_Day");
            float socialHours = resultSet.getFloat("Social_Hours_Per_Day");
            float physicalHours = resultSet.getFloat("Physical_Activity_Hours_Per_Day");
            float GPA  = resultSet.getFloat("GPA");
            String stress = resultSet.getString("Stress_Level");
            answer = ("Student_ID: " + student_id + " Study_Hours_Per_Day: " + studyHoursPerDay + " Extracurricular_Hours_Per_Day: " + 
            extraHours + " Sleep_Hours_Per_Day: " + sleepHours + " Social_Hours_Per_Day " + socialHours + " Physical_Activity_Hours_Per_Day: " + physicalHours
            + " GPA: " + GPA + " Stress: " + stress);
        }
        return answer;

    }

    
}

class Student{
    public int ID;
    public float study_Hours;
    public float ex_Hours;
    public float sleep_Hours;
    public float social_Hours;
    public float phys_Hours;
    public float GPA;
    public String Stress;
}