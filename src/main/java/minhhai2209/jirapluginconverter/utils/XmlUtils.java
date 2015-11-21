package minhhai2209.jirapluginconverter.utils;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class XmlUtils {

  /**
   * write generate content to writer
   * @param object
   * @param writer
   */
  public static <T> void toXml(T object, StringWriter writer) {
    try {
      JAXBContext context = JAXBContext.newInstance(object.getClass());
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
      marshaller.marshal(object, writer);
      writer.append("\n");
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }
  
}
