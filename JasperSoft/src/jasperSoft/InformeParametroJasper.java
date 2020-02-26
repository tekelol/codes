package jasperSoft;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;


/**
 * <h2>Clase InformeParametro, se utiliza para leer de una BBDD y crear informes
 * mediante JasperSoft</h2> Debemos tener instalado JasperSoft y haber creado
 * previamente el archivo .jasper Información acerca de JasperSoft
 * <a href="https://community.jaspersoft.com/">JasperSoft</a>
 * 
 * @author Jose Angel Perez
 * @version 1.0
 * @since 02-2020
 */

public class InformeParametroJasper extends JFrame {

	private JComboBox comboBox;
	private Object[] ids;
	private List<String> id = new ArrayList<String>();
	private List<String> idInvoice = new ArrayList<>();
	private ConexionBBDD conexion = new ConexionBBDD();
	private JasperReport report = null;
	private JasperReport subReport = null;
	private DefaultTableModel dtm;
	private JScrollPane scrollPane;
	private JTable table;
	private JPanel contentPane;
	private JTextField numeroFactura;
	private JFileChooser chooser = new JFileChooser();
	private Boolean primero = true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InformeParametroJasper frame = new InformeParametroJasper();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public InformeParametroJasper() throws SQLException, ClassNotFoundException {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table=new JTable();
		table.setBounds(33, 81, 506, 102);
		scrollPane = new JScrollPane();
		scrollPane.setBounds(33, 81, 506, 102);
		
		
		comboBox = new JComboBox();
		comboBox.setBounds(336, 11, 102, 24);
		LlenarCombo();
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					eliminarRows();
					facturasCliente();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(comboBox);
		JButton btnNewButton = new JButton("Crear Informe");
		btnNewButton.setBounds(309, 211, 145, 25);
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					crearInforme();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(btnNewButton);
		numeroFactura = new JTextField();
		numeroFactura.setBounds(179, 213, 86, 20);
		contentPane.add(numeroFactura);
		numeroFactura.setColumns(10);
		JLabel lblIntroduzcaIdFactura = new JLabel("Introduzca Id factura");
		lblIntroduzcaIdFactura.setBounds(26, 216, 157, 14);
		contentPane.add(lblIntroduzcaIdFactura);
		JLabel lblNewLabel = new JLabel("Seleccione el cliente a consultar");
		lblNewLabel.setBounds(36, 16, 243, 14);
		contentPane.add(lblNewLabel);
	}

	/**
	 * Crea el informe en formato .PDF
	 * 
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @see InformeParametroJasper
	 */

	public void crearInforme()
			throws IllegalAccessException, ClassNotFoundException, SQLException, InstantiationException {
		String invoiceid = numeroFactura.getText();
		String idCliente=comboBox.getSelectedItem().toString();
		try {

			conexion.conectar();
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("INVOICEID", invoiceid);
			

			String path = "/Informes/practicaInformes.jasper";
			String pathSub = "/Informes/cliente.jasper";
			//report = (JasperReport) JRLoader.loadObjectFromFile(path);
			
			report = (JasperReport) JRLoader.loadObject(InformeParametroJasper.class.getResource(path));
			
			//subReport = (JasperReport) JRLoader.loadObjectFromFile(pathSub);
			
			subReport = (JasperReport) JRLoader.loadObject(InformeParametroJasper.class.getResource(pathSub));
			parametros.put("IDCliente", subReport);
			
			JasperPrint print = JasperFillManager.fillReport(report, parametros, conexion.getConexion());
			JasperViewer.viewReport(print, false);
			String ruta = "";

			int returnVal = chooser.showSaveDialog(getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("You chose to save this file: ");
				ruta = chooser.getSelectedFile().getAbsolutePath();
				System.out.println(ruta);
			}

			JasperExportManager.exportReportToPdfFile(print, ruta);

		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	/**
	 * Lee datos de la BBDD y los vuelca al componente JComboBox
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */

	public void LlenarCombo() throws SQLException, ClassNotFoundException {

		conexion.getConexion();
		PreparedStatement statement = null;
		ResultSet result = null;

		String consulta2 = "select id from customer";

		try {

			if (conexion != null) {

				statement = conexion.conectar().prepareStatement(consulta2);
				result = statement.executeQuery();

				while (result.next() == true) {
					id.add(result.getString("ID"));
					ids = id.toArray();
					comboBox.setModel(new DefaultComboBoxModel(ids));
				}
			}
		} catch (SQLException e) {
			System.out.println("Error :" + e.getMessage());
		} finally {
			conexion.desconectar();
		}

	}

	

	/**
	 * Lee datos de la BBDD y vuelca los datos al componente JTable
	 * 
	 * @throws SQLException
	 * @see InformeParametroJasper
	 */

	public void facturasCliente() throws SQLException {

		conexion.getConexion();
		PreparedStatement statement = null;
		ResultSet result = null;
		String Boxcustomerid = (String) comboBox.getSelectedItem();

		String consulta = ("select id,customerid,total from invoice where CUSTOMERID=?;");
		
		try {
			if (conexion != null) {
				
				statement = conexion.conectar().prepareStatement(consulta);
				statement.setString(1, Boxcustomerid);
				result = statement.executeQuery();
				
				while (result.next() == true) {
					
					if (primero) {
						Object[] columna = { "INVOICEID", "CUSTOMERID", "TOTAL" };
						Object[][] datos = { { result.getString("ID"),result.getString("CustomerId"),result.getString("Total")} };
						dtm = new DefaultTableModel(datos, columna);
						scrollPane.setViewportView(table);
						table.setModel(dtm);
						getContentPane().add(scrollPane);
						primero = false;
					} else {
						dtm.addRow(new Object[] {result.getString("ID"),result.getString("CustomerId"),result.getString("Total")});
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("Error :" + e.getMessage());
		} finally {
			conexion.desconectar();
		}

	}
	/**
	 * Elimina filas de la tabla según elegimos usuario a consultar
	 */
	public void eliminarRows() {
		DefaultTableModel tabla = (DefaultTableModel) table.getModel();
		int a = table.getRowCount() - 1;
		for (int i = a; i >= 0; i--) {
			tabla.removeRow(tabla.getRowCount() - 1);
		}
	}
	

}
