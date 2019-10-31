package ec.com.kruger.kruger.activemq;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;

import ec.com.kruger.bean.principal.Cabecera;
import ec.com.kruger.util.impl.CabeceraPrincipalBuildUtil;

public class InsertarArchivoComprobanteBean {

	public void convertirCabecera(Exchange exchange) {
		String cabeceraPrincipalLine = (String) exchange.getIn().getHeader(LeerArchivoConstant.HEADER_CABECERA_MASIVA);
		Cabecera cabeceraPrincipal = CabeceraPrincipalBuildUtil.getCabeceraFile(cabeceraPrincipalLine);
		
		StringBuilder msgArchivoComprobante = new StringBuilder();
		msgArchivoComprobante.append(LeerArchivoConstant.ESTADO_INICIO_PROCESO);
		msgArchivoComprobante.append(LeerArchivoConstant.SEPARADOR_CADENA);
		msgArchivoComprobante.append(cabeceraPrincipal.getCtrRucEmpresa());
		msgArchivoComprobante.append(LeerArchivoConstant.SEPARADOR_CADENA);
		msgArchivoComprobante.append(cabeceraPrincipal.getCtrCodigoEmpresa());
		msgArchivoComprobante.append(LeerArchivoConstant.SEPARADOR_CADENA);
		msgArchivoComprobante.append(cabeceraPrincipal.getCtrPuntoVenta());
		msgArchivoComprobante.append(LeerArchivoConstant.SEPARADOR_CADENA);
		msgArchivoComprobante.append(exchange.getIn().getHeader(LeerArchivoConstant.HEADER_NOMBRE_ARCHIVO));
		msgArchivoComprobante.append(LeerArchivoConstant.SEPARADOR_CADENA);
		msgArchivoComprobante.append(LeerArchivoConstant.VALOR_NULO_STR);
		msgArchivoComprobante.append(LeerArchivoConstant.SEPARADOR_CADENA);
		msgArchivoComprobante.append(cabeceraPrincipal.getCtrNumeroComprobantes());
		msgArchivoComprobante.append(LeerArchivoConstant.SEPARADOR_CADENA);
		msgArchivoComprobante.append(new Date());
		msgArchivoComprobante.append(LeerArchivoConstant.SEPARADOR_CADENA);
		msgArchivoComprobante.append(LeerArchivoConstant.TIPO_PROCESO_BG);
		
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_MSG_INSERT_SQL, msgArchivoComprobante.toString());
	}

	public void crearArchivoComprobante(Exchange exchange) {
		String archivoComprobante = (String) exchange.getIn().getHeader(LeerArchivoConstant.HEADER_MSG_INSERT_SQL);
		String[] archivosComprobante = archivoComprobante.split(LeerArchivoConstant.SEPARADOR_CADENA);

		String status = archivosComprobante[0];
		String ruc = archivosComprobante[1];
		String codEmpresa = archivosComprobante[2];
		String ptoVenta = archivosComprobante[3];
		String fileName = archivosComprobante[4];
		String fileNameOut = archivosComprobante[5];
		String numeroComp = archivosComprobante[6];
		Date fechaInicio = new Date();
		String tipoProceso = archivosComprobante[8];

		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_STATUS, status);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_RUC, ruc);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_COD_EMPRESA, codEmpresa);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_PTO_VENTA, ptoVenta);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_FILE_NAME, fileName);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_FILE_NAME_OUT, fileNameOut);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_NUMERO_COMP, numeroComp);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_TIPO_PROCESO, tipoProceso);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_FECHA_INICIO, fechaInicio);
		exchange.getIn().setHeader(LeerArchivoConstant.HEADER_GENERAR_KEY_SQL, true);
	}

	public static void obtenerIdArchivo(Exchange exchange) {
		try {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<Map> headersMap = (List<Map>) exchange.getIn().getHeader("CamelSqlGeneratedKeyRows");
			
			if (!headersMap.isEmpty()) {
				String idArchivoStr = String.valueOf(headersMap.get(0).get("GENERATED_KEYS"));
				Integer idArchivo = Integer.valueOf(idArchivoStr);
				exchange.getIn().setHeader("idArchivo", idArchivo);
				
			} else {
				throw new Exception("Error al tratar de recuperar el archivo generado");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void procesarArchivoCabecera(Exchange exchange) throws IOException {
		@SuppressWarnings("rawtypes")
		GenericFile gf = (GenericFile<?>) exchange.getIn().getBody();
		String pathFile = gf.getBody().toString();
		InputStream is = new FileInputStream(pathFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String firstLine = reader.readLine();
		exchange.getIn().setHeader("cabeceraMasiva", firstLine);
		is.close();
		reader.close();
	}

	public void finalizaCargaArchivo(Exchange ex) throws Exception {
		ex.getIn().setHeader("fechaFinProceso", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
		ex.getIn().removeHeader("cabeceraMasiva");
		ex.getIn().setBody("");
	}
	
	public static void main(String ... string) {
		String cabecera = "T00120170208124324BG 0990049459001216925 SIBANCO GUAYAQUIL S.A.                                                            BANCO GUAYAQUIL S.A.                                                            PICHINCHA 105 - 107 Y FRANCISCO DE PAULA YCAZA              0100000008400000021N                                                                                                                     ";
		Cabecera cabeceraPrincipal = CabeceraPrincipalBuildUtil.getCabeceraFile(cabecera);
		
		System.out.println(cabeceraPrincipal);
	}

}
