package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConectaFtp {

	private static final Logger LOG = LoggerFactory.getLogger(ConectaFtp.class);

	private static String server = "";
	private static String user = "";
	private static String pwd = "";
	private static String path = "";
	private static Properties prop = new Properties();
	
	// Creando nuestro objeto ClienteFTP

	public FTPClient inicializa(String serv) {
		FTPClient client = null;
		try {
			client = new FTPClient();
			prop.load(getProperties());
			server = prop.getProperty("ftp.server" + serv);
			user = new DesEncrypter().decrypt(prop.getProperty("ftp.user" + serv));
			pwd = new DesEncrypter().decrypt(prop.getProperty("ftp.password" + serv));
			path = prop.getProperty("ftp.path" + serv);
			// Conactando al servidor
			client.connect(server);
			LOG.info("INICIALIZA FTP DE FORMA EXITOSA | " + client.getReplyCode());
			return client;
		} catch (Exception e) {
			LOG.error("HUBO UN ERROR AL INICIALIZAR EL FTP", e);
			return client;
		}
	}

	private Reader getProperties() {
		try {
			return new BufferedReader(
					new InputStreamReader(new FileInputStream(Constantes.ARCHIVO_PROPIEDADES), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			LOG.error(Constantes.LOG_ERROR, e);
			return null;
		}
	}

	public void subirArchivos(FTPClient cliente) throws Exception {
		int contSubidos = 0;
		StringBuilder fileName = new StringBuilder();
		try {

			if (FTPReply.isPositiveCompletion(cliente.getReplyCode())) {

				// Logueado un usuario (true = pudo conectarse, false = no pudo
				// conectarse)

				boolean login = cliente.login(user, pwd);
				LOG.info("SE INICIA SESION EN EL FTP " + server + " CON EL USUARIO " + user + " | "
						+ cliente.getReplyCode());

				cliente.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
				cliente.setFileTransferMode(FTP.BINARY_FILE_TYPE);
				cliente.enterLocalPassiveMode();
				cliente.changeWorkingDirectory(path);

				Date date = new Date();
				DateFormat hourdateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA);
				File file = new File(Constantes.RUTA_ARCHIVO + hourdateFormat.format(date));
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					try (FileInputStream fis = new FileInputStream(files[i].getAbsolutePath())) {
						if (login && !fileName.toString().equals(files[i].getName())
								&& cliente.storeFile(files[i].getName(), fis)) {
							// Guardando el archivo en el servidor
							LOG.info("SE SUBIO EL ARCHIVO {} | {}", files[i].getName(), cliente.getReplyCode());
							contSubidos ++;
						}
					}

				}

				try {
					// Cerrando sesión
					cliente.logout();
					LOG.info("SE CERRO LA SESION DEL USUARIO {} EN EL FTP | {}", user, cliente.getReplyCode());
				} catch (Exception ex) {
					LOG.error("ERROR AL CERRAR CONEXION DE FPT", ex);
				}
			}

		} catch (Exception e) {
			LOG.error("HUBO UN ERROR AL INICIAR LA SESION DEL FTP", e);
			try {
				// Cerrando sesión
				cliente.logout();
				LOG.info("SE CERRO LA SESION DEL USUARIO {} EN EL FTP DESPUES DE UN ERROR | {}", user,
						cliente.getReplyCode());
			} catch (Exception ex) {
				LOG.error("ERROR AL CERRAR LA SESION DE FPT", ex);
			}
		} finally {
			try {
				cliente.disconnect();
				LOG.info("SE DESCONECTO EL CLIENTE DEL SERVIDOR FTP {} | {}", server, cliente.getReplyCode());
			} catch (Exception e) {
				LOG.error("ERROR AL DESCONECTAR EL FPT", e);
			}

		}
	}

	public static void borrar(String filepath) {
		File f = new File("./archivos/" + filepath);
		if (f.exists()) {
			if (f.delete()) {
				LOG.info("SE HA BORRADO EL ARCHIVO LOCAL CORRECTAMENTE");
			} else {
				LOG.info("EL ARCHIVO NO SE HA PODIDO BORRAR");
			}
		}

	}
	
	public static void eliminar(File f){
		Date date = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA);
		File file = new File(Constantes.RUTA_ARCHIVO + hourdateFormat.format(date));
	    if(f.isDirectory()){
	        for(File f1 : f.listFiles()){
	            eliminar(f1);
	        }
	    }
	    f.delete();
	}

	public String obtenerArchivo(FTPClient cliente) {
		if (FTPReply.isPositiveCompletion(cliente.getReplyCode())) {

			try {
				FTPFile file = new FTPFile();
				
				String[] filesFTP = cliente.listNames();
				for (int i = 0; i < filesFTP.length; i++) {

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return "hola";
	}

}