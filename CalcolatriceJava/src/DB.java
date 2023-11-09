import java.sql.*;

import static javax.management.remote.JMXConnectorFactory.connect;

public class DB {

    private Connection connect(){
        final String URL = "jdbc:mysql://localhost:3306/dbcalcolatrice";
        final String PASSWORD = "";
        final String USERNAME = "root";
        Connection conn;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        catch(SQLException e){
            throw new RuntimeException();
        }
        return conn;
    }

    public int regirster(String Name, String Surname, String Nickname, String Password, int Age ) throws SQLException
    {
        if(Name.isEmpty() || Surname.isEmpty() || Nickname.isEmpty() || Password.isEmpty() || Age<=0)
            return -1;

        Connection conn = connect();
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO dbcalcolatrice (Name, Surname, Nickname, Password, Age) VALUES" +
                        "(?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, Name);
        preparedStatement.setString(2, Surname);
        preparedStatement.setString(3, Nickname);
        preparedStatement.setString(4, Password);
        preparedStatement.setInt(5, Age);

        preparedStatement.executeUpdate();

        stmt.close();
        conn.close();

        return 1;
    }

    public int login(String Nickname, String Password) throws SQLException
    {
        Connection conn = connect();

        try
        {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dbcalcolatrice WHERE Nickname =? AND Password =?";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, Nickname);
            preparedStatement.setString(2, Password);

            ResultSet resultSet =  preparedStatement.executeQuery();

            if(resultSet.next())
            {
                System.out.print("Login avvenuto con successo");
                stmt.close();
                conn.close();
                return 1;
            }
            else
            {
                System.out.print("Login non andato a buon fine");
                stmt.close();
                conn.close();
                return -1;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.print("login non andato a buon fine");
            return -1;
        }
    }

}
