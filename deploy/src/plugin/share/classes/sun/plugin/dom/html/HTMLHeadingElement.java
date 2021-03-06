/*
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.plugin.dom.html;

import sun.plugin.dom.DOMObject;

/**
 *  For the <code>H1</code> to <code>H6</code> elements. See the  H1 element 
 * definition in HTML 4.0.
 * <p>See also the <a href='http://www.w3.org/TR/2000/CR-DOM-Level-2-20000510'>Document Object Model (DOM) Level 2 Specification</a>.
 */
public final class HTMLHeadingElement extends HTMLElement
				      implements org.w3c.dom.html.HTMLHeadingElement {

    public HTMLHeadingElement(DOMObject obj, 
			      org.w3c.dom.html.HTMLDocument doc) {
	super(obj, doc);
    }

    /**
     *  Horizontal text alignment. See the  align attribute definition in HTML 
     * 4.0. This attribute is deprecated in HTML 4.0.
     */
    public String getAlign() {
	return getAttribute(HTMLConstants.ATTR_ALIGN);
    }

    public void setAlign(String align) {
	setAttribute(HTMLConstants.ATTR_ALIGN, align);
    }

}
