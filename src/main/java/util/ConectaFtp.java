package util;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ConectaFtp {
	private static final Logger LOG = LoggerFactory.getLogger("FILE");
	Session session;
	JSch jsch = new JSch();
	
	public void borrarArchivosFtp(ChannelSftp channel, int serv, Properties prop) {
		String servidor = prop.getProperty("ftp.server"+serv);
		String path = prop.getProperty("ftp.path"+serv);
		try {
			LOG.info("SE INICIA EL BORRADO DE LOS ARCHIVOS DEL SERVIDOR {}",servidor);
			List<LsEntry> flLst = channel.ls(path);
			for(LsEntry rFile : flLst) {
				if(rFile.getFilename().contains("preciosR") && rFile.getFilename().contains(".csv")) {
					channel.rm(path+"/"+ rFile.getFilename());
					LOG.info("EL ARCHIVO {}, FUE BORRADO CON EXITO DEL SERVIDOR {}",rFile.getFilename(),servidor);
				}
			}
		}catch (Exception e) {
			LOG.info("NO SE PUEDE OBTENER LA LISTA DE ARCHIVOS DEL SERVIDOR {}",servidor);
		}	
	}
	
//	public void subirArchivo(File archivo, int cont) {
//		Properties prop = new Properties();
//		String path;
//		ChannelSftp sftpChannel = null;
//		StringBuilder fileName = new StringBuilder();
//		try {
//			prop.load(getProperties());
//			path = prop.getProperty("ftp.path");
//			sftpChannel = openServer(prop);
//			fileName.append(archivo.getName());
//			sftpChannel.cd(path);
//			sftpChannel.put(archivo.getAbsolutePath(), path + "/" + cont + fileName.toString());
//			if (sftpChannel.get(cont + fileName.toString()) != null) {
//				LOG.info("SE SUBIO EL ARCHIVO {} EN LA RUTA: {}", fileName, path);
////				borrar(fileName.toString());
//			}
//
//		} catch (Exception e) {
//			LOG.error("ERROR AL SUBIR EL ARCHIVO AL SERVIDOR FTP", e);
//		} finally {
//			if (null != sftpChannel) {
//				closeServer(sftpChannel);
//			}
//		}
//	}
//
//	public void borrar(String filepath) {
//		FileSystem sistemaFicheros = FileSystems.getDefault();
//		Path ruta = sistemaFicheros.getPath(Constantes.ARCHIVOS + "/" + filepath);
//		try {
//			Files.deleteIfExists(ruta);
//			LOG.info("SE HA BORRADO EL ARCHIVO LOCAL CORRECTAMENTE");
//		} catch (Exception e) {
//			LOG.info("EL ARCHIVO NO SE HA PODIDO BORRAR", e);
//		}
//	}
//
//	public void subirArchivos() {
//		LOG_CON.info("Revisando si hay archivos");
//		Properties prop = new Properties();
//		String path;
//		ChannelSftp sftpChannel;
//		File file = new File(Constantes.ARCHIVOS);
//		try {
//			prop.load(getProperties());
//			path = prop.getProperty("ftp.path");
//			sftpChannel = openServer(prop);
//			if (file.listFiles().length >= 1) {
//				LOG.info("SE ENCUENTRAN ARCHIVOS PARA SUBIR EN LA CARPETA");
//				File[] files = file.listFiles();
//				for (int i = 0; i < files.length; i++) {
//					String fileName = files[i].getName();
//					sftpChannel.cd(path);
//					sftpChannel.put(files[i].getAbsolutePath(), path + "/" + fileName);
//					// Guardando el archivo en el servidor
//					if (sftpChannel.get(fileName) != null) {
//						LOG.info("SE SUBIO EL ARCHIVO {} ", files[i].getName());
//						borrar(files[i].getName());
//					}
//				}
//				closeServer(sftpChannel);
//			}
//		} catch (Exception e) {
//			LOG.error("ERROR AL SUBIR LOS ARCHIVOS, SE VOLVERAN A PROCESAR", e);
//		}
//	}
//
	public ChannelSftp openServer(Properties prop, int num) throws Exception {
		Channel channel = null;
		ChannelSftp sftpChannel = null;
		String keyU = "ftp.user"+ num;
		String keyS = "ftp.server"+ num;
		String keyP = "ftp.password"+ num;
		
		try {
			if (session == null) {
				session = jsch.getSession(new DesEncrypter().decrypt(prop.getProperty(keyU)),
						prop.getProperty(keyS), 22);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(new DesEncrypter().decrypt(prop.getProperty(keyP)));
			}
			if (!session.isConnected()) {
				session.connect();
				LOG.info("SE INICIA LA SESION EN EL SERVIDOR FTP");
				channel = session.openChannel("sftp");
				channel.connect(5000);
				sftpChannel = (ChannelSftp) channel;
				return sftpChannel;
			} else {
				channel = session.openChannel("sftp");
				channel.connect(5000);
				sftpChannel = (ChannelSftp) channel;
				return sftpChannel;
			}
		} catch (Exception e) {
			if (sftpChannel != null && sftpChannel.isConnected()) {
				sftpChannel.disconnect();
			}
			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
			LOG.error("ERROR EN LA CONEXION AL SERVIDOR ", e);
			sftpChannel = null;
			channel = null;
			session = null;
			throw new Exception(e);
		}
	}

	public void closeServer(ChannelSftp sftpChannel) {
		if (sftpChannel != null) {
			try {
				sftpChannel.disconnect();
				sftpChannel.getSession().disconnect();
				sftpChannel = null;
				session.disconnect();
				session = null;
				LOG.info("SE CIERRA CONEXION A SERVIDOR");
			} catch (JSchException e) {
				LOG.error("ERROR AL DESCONECTAR SERVIDOR", e);
			} catch (Exception e) {
				LOG.error("ERROR GENERAL AL DESCONECTAR SERVIDOR", e);
			}

		}
	}

}