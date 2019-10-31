package ec.com.kruger.kruger.activemq;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import ec.com.kruger.util.impl.DocumentTransformUtilImpl;

public class ProcesarArchivo implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub

	}

	public void procesarArchivo(Exchange exchange) throws Exception {

		String bloque = exchange.getIn().getBody(String.class);
		String tipoLinea = DocumentTransformUtilImpl.getTipoLinea(bloque);
		if("T".equals(tipoLinea)){
			exchange.getIn().setBody(null);
		}else {
			String nuevoBody = exchange.getIn().getHeader("cabeceraMasiva")
					+ "\n" + "C"+exchange.getIn().getBody(String.class);
			exchange.getIn().setBody(nuevoBody);
		}
	}

	
	public void finalizaCargaArchivo(Exchange ex) throws Exception {
		int splitSize = ex.getIn().getHeader("splitSize", int.class);
		ex.getIn().setHeader("comprobantesCargados", splitSize - 1);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String fecha = df.format(new Date());
		ex.getIn().setHeader("fechaFinProceso", fecha);
		ex.getIn().setBody("");
	}
}
