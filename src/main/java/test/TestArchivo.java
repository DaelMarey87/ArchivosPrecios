package test;

import static datos.MaterialLista.envioCvs;

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datos.MaterialLista;
import domain.Material;

import util.Constantes;

public class TestArchivo {
	//Iniciamos la constante del logger para mandar los mesajes
	private static final Logger lOG = LoggerFactory.getLogger(TestArchivo.class);

	public static void main(String[] args) {
		
		/* -se define la ruta %userprofile%\Desktop\precios como ruta predefinida 
		 *   para leer el archivo txt y se pregunta el nombre del archivo deve contener extencion (.txt)
		 * -se sustituira por una lectura a FTP 
		 *
		 * FTPClient cliente = ConectaFtp.inicializa(Constantes.SERV_LECTURA)
		 * String archivo = ConectaFtp.obtenerArchivo(cliente);
		 * 
		 */
		String userHomeDir = System.getProperty("user.home");
				
		String archivo = new String(
				userHomeDir + "\\Desktop\\precios1\\" + JOptionPane.showInputDialog("¿Cual es el nombre del archivo?"));
		lOG.info("se va a leer {}", archivo);
		

		MaterialLista listaMaterial = new MaterialLista();
		

		try {
//			Eviamos el archivo para su lecura
			List<Material> materiales = listaMaterial.generar(archivo);
//			recibimos un List y verificamos si esta vacio para no generar ningun archivo
			if (!materiales.isEmpty()) {
//				enviamos el List para armar los CSV
				envioCvs(materiales);
				
			} else {
				lOG.info("La lista esta vacia no se creo ningun archivo");
			}

		} catch (IOException ex) {
			lOG.error("No se encuentra el archivo {}", archivo);

		}

	}

}