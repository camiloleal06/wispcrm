package org.wispcrm.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

	public Connection conectar() {
		Connection con = null;

		try {
		
			//String url = "jdbc:mysql://us-cdbr-east-02.cleardb.com/heroku_daa383926a65d74?user=bce3f6d3a55645&password=f16e8998";
		//	String url = "jdbc:mysql://nacionaldemuebles.com.co/nacional_crm?user=nacional_crm&password=crm2020+-+";
			String url = "jdbc:mysql://localhost:3306/tecnowisp?user=root&password=admin";

			con = DriverManager.getConnection(url);
			if (con != null) {
				System.out.println("Conexion Satisfactoria");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return con;
	}
}