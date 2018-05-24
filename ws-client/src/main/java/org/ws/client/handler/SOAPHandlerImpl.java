package org.ws.client.handler;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class SOAPHandlerImpl implements SOAPHandler<SOAPMessageContext> {

	private static final Logger logger = LoggerFactory.getLogger(SOAPHandlerImpl.class);

	public boolean handleMessage(SOAPMessageContext context) {

		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outboundProperty.booleanValue()) {
			logger.info("[handleMessage]Outbound message:");
		} else {
			logger.info("[handleMessage]Inbound message:");
		}
		try {
			context.getMessage().getSOAPHeader().getOwnerDocument();
			logger.info("payload:" + transformDocumentIntoXml(context.getMessage().getSOAPBody().getOwnerDocument()));
		} catch (Exception e) {
			logger.error("", e);
		}

		return true;
	}

	public boolean handleFault(SOAPMessageContext context) {
		try {
			logger.warn(
					"[handleFault]" + transformDocumentIntoXml(context.getMessage().getSOAPBody().getOwnerDocument()));
		} catch (UnsupportedEncodingException | SOAPException e) {
			logger.error("", e);
		}
		return true;
	}

	public void close(MessageContext context) {
		for (String key : context.keySet()) {
			logger.info("[close]" + key + "=" + context.get(key));
		}
	}

	public Set<QName> getHeaders() {
		return null;
	}

	private String transformDocumentIntoXml(Document outputDocument) throws UnsupportedEncodingException {

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(outputDocument), new StreamResult(bos));

			return bos.toString("UTF-8");
		} catch (TransformerException e) {
			logger.error("", e);
			return null;
		}

	}

}
