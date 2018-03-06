/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author webapp
 */
public final class MySQLConnString {
    private static String proto;
    private static String host;
    private static String port;
    private static String db;
    private static String uname;
    private static String passwd;
    private static String connStr;

    public MySQLConnString() { 
	proto = "jdbc:mysql";
	host = "localhost";
	port = "3306";
	db = "securitydb";
        uname = "root";
        passwd = "V8nilla";
        connStr = proto + "://" + host + ":" + port + "/" + db + "?user="+ uname + "&password=" + passwd;
    };
    
    public String getMySQLConnString () {
	//	//jdbc:mysql://localhost:3306/test?user=root&password=root123"
        return connStr;
    };
}
