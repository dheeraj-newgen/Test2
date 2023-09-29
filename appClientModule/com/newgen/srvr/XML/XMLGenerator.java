/**
 * <p>Title: Auto Create Server</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Newgen Software Technologies Ltd.</p>
 * @author Piyush Pratap Singh
 * @version 1.0
 */

package com.newgen.srvr.XML;

public class XMLGenerator {

    public XMLGenerator() {
    }

    public StringBuffer createXMLHeader(float versionNo) {
        StringBuffer pString = new StringBuffer(25);
        pString.append("<?xml version=\"");
        pString.append(versionNo);
        pString.append("\"?>");
        return pString;
    }

    public StringBuffer writeElement(String tag, String value) {
        StringBuffer pString = new StringBuffer();
        if (value != null) {
            pString.append("<");
            pString.append(tag);
            pString.append(">");
            pString.append(value.trim());
            pString.append("</");
            pString.append(tag);
            pString.append(">");
        }
        return pString;
    }
}
