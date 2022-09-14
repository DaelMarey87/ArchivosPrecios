package datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Material;
import util.Constantes;

public class MaterialLista {
	private static final Logger LOG = LoggerFactory.getLogger(MaterialLista.class);

	public List<Material> generar(String file) throws IOException {
		
		Material material = null;
		List<Material> materiales = new ArrayList<>();
//		empeamos a leer el archivo 
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String sCurrentLine = null;
//			bucle para quitar cadenas que no necesitamos
			while ((sCurrentLine = br.readLine()) != null) {
				String[] separado = sCurrentLine.split("\\|");
				int zona = 0;
				int clave = 0;
				Double precio = 0.0;
				for (int i = 0; i < 6; i++) {
					if (i == 1) {
						zona = Integer.parseInt(separado[i].replace("MX0", ""));
					} else if (i == 2) {
						clave = Integer.parseInt(separado[i]);
					} else if (i == 4) {
						String prec = separado[i].replaceAll("-MXN|MXN|USD", "");
//						cambiamos precios meores a 0.05
						precio = (Double.parseDouble(prec) < 0.05) ? 0.05 : Double.parseDouble(prec);
					}
				}
//				obtenemos un Material y lo adicionamos al List
				material = new Material(zona, clave, precio);
				materiales.add(material);
			}
		} catch (FileNotFoundException ex) {
			LOG.error("NO SE PUDO LEER EL ARCHIVO ", ex);
		}
//		regresamos el List con todos los valores limpios econtrados en el archivo (Region, Serie, Precio) en objetos Material
		return materiales;
	}

	public static void envioCvs(List<Material> materiales) {
		
//		Un map por cada Region
		Map<Integer, Double> region1 = new HashMap<>();
		Map<Integer, Double> region2 = new HashMap<>();
		Map<Integer, Double> region3 = new HashMap<>();
		Map<Integer, Double> region4 = new HashMap<>();
		Map<Integer, Double> region5 = new HashMap<>();
		Map<Integer, Double> region6 = new HashMap<>();
		Map<Integer, Double> region7 = new HashMap<>();
		Map<Integer, Double> region8 = new HashMap<>();
		Map<Integer, Double> region9 = new HashMap<>();
//		bucle para leer cada material de la lista y direccionarlo a su Map correspondiente
		for (Material material : materiales) {
			Material mat;
			switch (material.getZona()) {
			case 1:
				mat = verPrecio(material, region1);
				region1.put(mat.getSku(), mat.getPrecio());
				break;
			case 2:
				mat = verPrecio(material, region2);
				region2.put(mat.getSku(), mat.getPrecio());
				break;
			case 3:
				mat = verPrecio(material, region3);
				region3.put(mat.getSku(), mat.getPrecio());
				break;
			case 4:
				mat = verPrecio(material, region4);
				region4.put(mat.getSku(), mat.getPrecio());
				break;
			case 5:
				mat = verPrecio(material, region5);
				region5.put(mat.getSku(), mat.getPrecio());
				break;
			case 6:
				mat = verPrecio(material, region6);
				region6.put(mat.getSku(), mat.getPrecio());
				break;
			case 7:
				mat = verPrecio(material, region7);
				region7.put(mat.getSku(), mat.getPrecio());
				break;
			case 8:
				mat = verPrecio(material, region8);
				region8.put(mat.getSku(), mat.getPrecio());
				break;
			case 9:
				mat = verPrecio(material, region9);
				region9.put(mat.getSku(), mat.getPrecio());
				break;
			default:
				break;
			}
		}
		
		if (!region1.isEmpty()) {
			String reg1 = ponerNombre("1");
			imprimir(reg1, region1);
			LOG.info(Constantes.MEN,reg1);
		}
		if (!region2.isEmpty()) {
			String reg2 = ponerNombre("2");
			imprimir(reg2, region2);
			LOG.info(Constantes.MEN,reg2);
		}
		if (!region3.isEmpty()) {
			String reg3 = ponerNombre("3");
			imprimir(reg3, region3);
			LOG.info(Constantes.MEN,reg3);
		}
		if (!region4.isEmpty()) {
			String reg4 = ponerNombre("4");
			imprimir(reg4, region4);
			LOG.info(Constantes.MEN,reg4);
		}
		if (!region5.isEmpty()) {
			String reg5 = ponerNombre("5");
			imprimir(reg5, region5);
			LOG.info(Constantes.MEN,reg5);
		}
		if (!region6.isEmpty()) {
			String reg6 = ponerNombre("6");
			imprimir(reg6, region6);
			LOG.info(Constantes.MEN,reg6);
		}
		if (!region7.isEmpty()) {
			String reg7 = ponerNombre("7");
			imprimir(reg7, region7);
			LOG.info(Constantes.MEN,reg7);
		}
		if (!region8.isEmpty()) {
			String reg8 = ponerNombre("8");
			imprimir(reg8, region8);
			LOG.info(Constantes.MEN,reg8);
		}
		if (!region9.isEmpty()) {
			String reg9 = ponerNombre("9");
			imprimir(reg9, region9);
			LOG.info(Constantes.MEN,reg9);
		}
	}
	
	
	public static Material verPrecio(Material material, Map<Integer,Double> mapa) {
		double precio1 = 0;
		double precio2 = 0;
				
		if(mapa.containsKey(material.getSku())){
			precio1 = mapa.get(material.getSku());
			precio2 = material.getPrecio();
			material.setPrecio((precio1 < precio2)? precio2 : precio1);
		}
		return material;
	}

	
	public static String ponerNombre(String numero) {
		Date date = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA);
		File directorio = new File(Constantes.RUTA_ARCHIVO + hourdateFormat.format(date));
		directorio.mkdirs();
		StringBuilder rutaSav = new StringBuilder();
		rutaSav.append(directorio + Constantes.PRECIO + numero + Constantes.EXTENCION);

		return rutaSav.toString();
	}

	public static void imprimir(String file, Map<Integer, Double> region) {
		File archivo = new File(file);
		try {
			PrintWriter salida = new PrintWriter(new FileWriter(archivo, true));
			TreeMap<Integer, Double> regOrd = new TreeMap<>(region);
			for (Map.Entry<Integer, Double> entry : regOrd.entrySet()) {
				salida.println(entry.getKey() + "," + entry.getValue());
			}
			salida.close();
		} catch (IOException ex) {
			LOG.error("NO SE PUDO IMPRIMIR EN EL ARCHIVO {}, POR {}",file,ex);
		}
	}
}
