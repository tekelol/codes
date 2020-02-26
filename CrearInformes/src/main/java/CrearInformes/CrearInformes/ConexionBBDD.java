package CrearInformes.CrearInformes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <h2> Clase ConexionBBDD, se utiliza para realizar la conexion a una BBDD</h2>
 * @author Jose Angel Perez
 * @version 1.0
 */

final public class ConexionBBDD {

	/**
	 * Atributo conexion
	 */
	private Connection conexion = null;

	/**
	 * Getter
	 * @return
	 */
	public Connection getConexion() {
		return conexion;
	}

	/**
	 * Realiza la conexion a la BBDD
	 * @return
	 * @see InformeParametro
	 */
	public Connection conectar() {
		String db = "jdbc:hsqldb:hsql://localhost/testdb;ifexists=true";
		String user = "SA";
		String password = "";

		if (conexion == null) {
			try {
				Class.forName("org.hsqldb.jdbcDriver");
				conexion = DriverManager.getConnection(db, user, password);
			} catch (SQLException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return conexion;

	}

	/**
	 * Desconexion de la BBDD
	 * @see InformeParametro
	 */
	public void desconectar() {
		if (conexion != null)
			try {
				if (!conexion.isClosed()) {
					conexion.close();
					conexion = null;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
