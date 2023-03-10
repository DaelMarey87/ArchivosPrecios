package domain;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;

import util.ConectaFtp;


public class HiloFtp extends Thread {
	private static final Logger LOG = LoggerFactory.getLogger("FILE");
	Properties prop;
	int num;
	
	@Override
	public void run() {
		ConectaFtp sftp = new ConectaFtp();
		ChannelSftp channel = null;
		try {
			channel = sftp.openServer(prop,num);
			sftp.borrarArchivosFtp(channel, num, prop);
			sftp.subirArchivos(channel, num, prop);
		} catch (Exception e) {
			LOG.error("ERROR AL CARGAR LOS ARCHIVOS AL SERVIDOR {}", prop.getProperty("ftp.server"+num));	
		}finally {
			if (null != channel) {
				sftp.closeServer(channel);
			}
		}
		
	}

	public HiloFtp(Properties prop, int num) {
		this.prop = prop;
		this.num = num;
	}
	
}
