package fridayqun.com.myapplication.common;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.Writer;
import java.util.regex.Pattern;

import fridayqun.com.myapplication.pay_protocol.PayRequestData;

/**
 * Created by lenovo on 2016/5/12.
 */
public class XMLUtil {

    /**

     * 扩展xstream，使其支持CDATA块
     */
    private static XStream xstream = new XStream(new XppDriver( ) {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对那些xml节点的转换增加CDATA标记   true增加  false反之
                boolean cdata = false;

                @SuppressWarnings("unchecked")
                public void startNode(String name, Class clazz) {

                    if(!name.equals("xml")){
                        char[] arr = name.toCharArray();
                        name = new String(arr);
                    }
                    name.replaceAll("__","_");
                    super.startNode(name, clazz);
                }

                @Override
                public void setValue(String text) {

                    if(text!=null && !"".equals(text)){
                        //浮点型判断
                        Pattern patternInt = Pattern.compile("[0-9]*(\\.?)[0-9]*");
                        //整型判断
                        Pattern patternFloat = Pattern.compile("[0-9]+");
                        //如果是整数或浮点数 就不要加[CDATA[]了
                        if(patternInt.matcher(text).matches() || patternFloat.matcher(text).matches()){
                            cdata = false;
                        }else{
                            cdata = true;
                        }
                    }
                    super.setValue(text);
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    }

    );

    /**
     * 文本消息对象转换成xml
     *
     * @param object 文本消息对象
     * @return xml
     */
    public static String sendDataToXml(PayRequestData object) {
        xstream.alias("xml", object.getClass());
        return xstream.toXML(object);
    }
    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     */

    public static String getData(Object xmlObj){

        //解决XStream对出现双下划线的bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
        //将要提交给API的数据对象转换成XML格式数据Post给API
        String postDataXML = xStreamForRequestPostData.toXML(xmlObj);

        return postDataXML;

    }
}
