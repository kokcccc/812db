/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util ;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {

    private final String serverUrl;
    private final String dbName;
    private String user = "";
    private String password = "";
    private String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private final boolean readyToWork; 

    private final String sslProtocol = ";sslProtocol=TLSv1.2";

    public DBConnect(String serverUrl, String dbName) {
        this.serverUrl = serverUrl;
        this.dbName = dbName;
        readyToWork = checkConnection();
    }

    public DBConnect(String serverUrl, String dbName, String driver) {
        this.serverUrl = serverUrl;
        this.dbName = dbName;
        this.driver = driver;

        readyToWork = checkConnection();
    }

    public DBConnect(String serverUrl, String dbName, String user, String password) {
        this.serverUrl = serverUrl;
        this.dbName = dbName;
        this.user = user;
        this.password = password;

        readyToWork = checkConnection();
    }

    public DBConnect(String serverUrl, String dbName, String user, String password, String driver) {
        this.serverUrl = serverUrl;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.driver = driver;

        readyToWork = checkConnection();
    }

    private boolean checkConnection() {
        Connection testConnection = createConnection();

        if (testConnection == null) {
            System.out.println("Can't connect to server. Check throws & url/dbName/username/password.\n"
                    + "*IntegratedSecurity option require sqljdbc_auth.dll in java.library.path.");
            return false;
        } else {
            try {
                testConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    private Connection createConnection() {
        String connStr = "jdbc:sqlserver://" + this.serverUrl + ";databaseName=" + this.dbName + ";";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }

        Connection conn;
        if (this.user.equals("") && this.password.equals("")) {
            connStr += "integratedSecurity=true;";
            try {

                conn = DriverManager.getConnection(connStr);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            try {
                conn = DriverManager.getConnection(connStr, user, password);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        return conn;

    }
    public ResultSet execQuery(String query) {
        if (!readyToWork) {
            System.out.println("DBC not ready to work! Abort:execQuerySelected");
            return null;
        }

        Connection conn = createConnection();

        Statement stmt;

        try {
            assert conn != null;
            stmt = conn.createStatement();

            return stmt.executeQuery(query);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something wrong... Check your query text.");
            return null;
        }
    }

    public int insertDeleteUpdate(String query) {

        if (!readyToWork) {
            System.out.println("DBC not ready to work! Abort:execQuerySelected");
            return 0;
        }
        Connection conn = createConnection();
        Statement stmt;
        try {
            assert conn != null;
            stmt = conn.createStatement();
            int result = stmt.executeUpdate(query);
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something wrong... Check your query text.");
            return -1;
        }

    }

    public Connection getConnect() {
        return createConnection();
    }
}
