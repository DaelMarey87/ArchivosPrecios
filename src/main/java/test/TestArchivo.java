package test;

import static datos.MaterialLista.envioCvs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datos.MaterialLista;
import domain.HiloFtp;
import domain.Material;
import util.ConectaFtp;
import util.Constantes;

public class TestArchivo {
	//Iniciamos la constante del logger para mandar los mesajes
	private static final Logger LOG = LoggerFactory.getLogger(TestArchivo.class);
	private static Properties prop = new Properties();	
	private static final String CAPRPETA_TXT = "./archivos/txt";
	ConectaFtp ftp;


	public static void main(String[] args) {
		int conteo = 1;
		String archivo = obtenerArchivo();
		try {
			prop.load(getProperties());
			LOG.info("se va a leer {}", archivo);
			MaterialLista listaMaterial = new MaterialLista();

			//			Eviamos el archivo para su lecura
			List<Material> materiales = listaMaterial.generar(archivo);
			//			recibimos un List y verificamos si esta vacio para no generar ningun archivo
			if (!materiales.isEmpty()) {
				//				enviamos el List para armar los CSV
				envioCvs(materiales);
//				borrarArchivo(archivo);			
			} else {
				LOG.info("La lista esta vacia no se creo ningun archivo");
			}

		} catch (IOException ex) {
			LOG.error("No se encuentra el archivo {}", archivo);

		}
		LOG.info("SE INICIA LA CARGA AL (LOS) SERVIDOR(ES) FTP");
		while (conteo <= Integer.parseInt(args[0])) {
			HiloFtp hilo = new HiloFtp(prop,conteo);
			hilo.start();
			conteo ++;
		}
		

	}
	
	public static String obtenerArchivo(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		File directorio = new File(CAPRPETA_TXT);
		String archivo = "";
		FileFilter filter = (File file) -> {
			Date fecha = new Date();
			try {
				fecha = format.parse(format.format(fecha));
			} catch (ParseException e) {
				LOG.error("ERROR AL OBTENER LA FECHA");
			}
			boolean respuesta = false;
			long ms = file.lastModified();
			Date modificado = new Date(ms);
			boolean extencion = file.getName().endsWith(".txt");
			boolean fechaInicio = fecha.after(modificado);
			if (extencion && (fechaInicio || (fecha.after(sumarRestarDiasFecha(fecha, -1))))) {
				respuesta = true;
			} else {
				respuesta = false;
			}
			return respuesta;
		};

	File[] archivos = directorio.listFiles(filter);
	if (archivos.length > 1) {
		archivo = archivos[0].getAbsolutePath();
	}else {
		archivo = archivos[0].getAbsolutePath();
	}
		return archivo;
	}
	
	public static void borrarArchivo(String file) {
		File archivo = new File(file);
		if (archivo.delete()) {
			LOG.info("SE HA BORRADO EL ARCHIVO {} CORRECTAMENTE",archivo.getName());
		} else {
			LOG.info("EL ARCHIVO {} NO SE HA PODIDO BORRAR", archivo.getName());
		}		
	}
	
	public static Date sumarRestarDiasFecha(Date fecha, int dias) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha); // Configuramos la fecha que se recibe
		calendar.add(Calendar.DAY_OF_YEAR, dias); // numero de días a añadir, o restar en caso de días<0
		return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
	}
	
	private static Reader getProperties() {
		try {
			return new BufferedReader(
					new InputStreamReader(new FileInputStream("./config/config.properties"), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			LOG.error(Constantes.LOG_ERROR, e);
			return null;
		}
	}

}