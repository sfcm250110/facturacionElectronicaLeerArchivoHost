package ec.com.kruger.kruger.activemq;

import java.io.Serializable;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;

public class ExceptionLeerArchivoHostService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(ExceptionLeerArchivoHostService.class);
	
	public void procesarException(Exchange pExchange) {
		try {
			Exception exception = pExchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			String stackTrace = Arrays.toString(exception.getStackTrace());
			
			pExchange.getIn().setHeader("flujoProceso", "leer-archivo-host-bg");
			pExchange.getIn().setHeader("fechaProceso", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
			pExchange.getIn().setHeader("messageException", exception.getMessage());
			pExchange.getIn().setBody(stackTrace);
			
			
		} catch(Exception e) {
			log.error("Error al procesar la exception para el proceso [leerArchivoHostBg]. Detalle: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
