package ec.com.kruger.kruger.activemq;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class TransformXml implements Processor  {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public void transformar(Exchange exchange) throws Exception {
		
		List<String>	listaFacturas= new LinkedList<String>(	Arrays.asList(exchange.getIn().getBody().toString().split("\nC",1)));
		exchange.getIn().setHeader("tamanioFacturas", listaFacturas.size());
		exchange.getIn().setHeader("cabeceraMasiva", listaFacturas.get(0));
		listaFacturas.remove(0);
		exchange.getIn().setHeader("listaFacturas", listaFacturas);
	}

}
