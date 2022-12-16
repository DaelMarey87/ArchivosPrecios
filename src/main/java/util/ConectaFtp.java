package util;

import java.io.File;
import java.io.FileFilter;
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
import com.jcraft.jsch.SftpException;

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
	
	public void subirArchivos(ChannelSftp channel, int serv, Properties prop) throws SftpException {
		File directorio = new File(Constantes.RUTA_ARCHIVO);
		FileFilter filter = (File file) -> {
			boolean respuesta = false;
			boolean extencion = file.getName().endsWith(".csv");
			if (extencion && file.getName().contains("preciosR")) {
				respuesta = true;
			} else {
				respuesta = false;
			}
			return respuesta;
		};
		File[] archivos = directorio.listFiles(filter);
		String path = prop.getProperty("ftp.path"+ serv);
		String ip = prop.getProperty("ftp.server"+ serv);
		for(File archivo : archivos) {
			channel.cd(path);
			channel.put(archivo.getAbsolutePath(), path + "/" +archivo.getName());
			if(channel.get(archivo.getName()) != null)
			{
				LOG.info("SE SUBIO EL ARCHIVO {} EN LA RUTA: {}", archivo.getName(), ip + path);
			}
		}
	}
	
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
				LOG.info("SE INICIA LA SESION EN EL SERVIDOR FTP {}",keyS);
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
			LOG.error("ERROR EN LA CONEXION AL SERVIDOR {}",prop.getProperty(keyS), e);
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