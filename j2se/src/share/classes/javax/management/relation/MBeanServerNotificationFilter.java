/*
 * @(#)MBeanServerNotificationFilter.java	1.33 04/02/10
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.MBeanServerNotification;
import javax.management.ObjectName;

import java.util.List;
import java.util.Vector;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.trace.Trace;

/**
 * Filter for {@link MBeanServerNotification}.
 * This filter filters MBeanServerNotification notifications by
 * selecting the ObjectNames of interest and the operations (registration,
 * unregistration, both) of interest (corresponding to notification
 * types).
 *
 * @since 1.5
 */
public class MBeanServerNotificationFilter extends NotificationFilterSupport {

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form 
    private static final long oldSerialVersionUID = 6001782699077323605L;
    //
    // Serial version for new serial form 
    private static final long newSerialVersionUID = 2605900539589789736L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields = 
    {
      new ObjectStreamField("mySelectObjNameList", Vector.class),
      new ObjectStreamField("myDeselectObjNameList", Vector.class)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields = 
    {
      new ObjectStreamField("selectedNames", List.class),
      new ObjectStreamField("deselectedNames", List.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /**
     * @serialField selectedNames List List of {@link ObjectName}s of interest
     *         <ul>
     *         <li><code>null</code> means that all {@link ObjectName}s are implicitly selected
     *         (check for explicit deselections)</li>
     *         <li>Empty vector means that no {@link ObjectName} is explicitly selected</li>
     *         </ul>
     * @serialField deselectedNames List List of {@link ObjectName}s with no interest
     *         <ul>
     *         <li><code>null</code> means that all {@link ObjectName}s are implicitly deselected 
     *         (check for explicit selections))</li>
     *         <li>Empty vector means that no {@link ObjectName} is explicitly deselected</li>
     *         </ul>
     */
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat = false;  
    static {
	try {
	    PrivilegedAction act = new GetPropertyAction("jmx.serial.form");
	    String form = (String) AccessController.doPrivileged(act);
	    compat = (form != null && form.equals("1.0"));
	} catch (Exception e) {
	    // OK : Too bad, no compat with 1.0
	}
	if (compat) {
	    serialPersistentFields = oldSerialPersistentFields;
	    serialVersionUID = oldSerialVersionUID;
	} else {
	    serialPersistentFields = newSerialPersistentFields;
	    serialVersionUID = newSerialVersionUID;
	}
    }
    //
    // END Serialization compatibility stuff

    //
    // Private members
    //

    /**
     * @serial List of {@link ObjectName}s of interest
     *         <ul>
     *         <li><code>null</code> means that all {@link ObjectName}s are implicitly selected
     *         (check for explicit deselections)</li>
     *         <li>Empty vector means that no {@link ObjectName} is explicitly selected</li>
     *         </ul>
     */
    private List selectedNames = new Vector();

    /**
     * @serial List of {@link ObjectName}s with no interest
     *         <ul>
     *         <li><code>null</code> means that all {@link ObjectName}s are implicitly deselected 
     *         (check for explicit selections))</li>
     *         <li>Empty vector means that no {@link ObjectName} is explicitly deselected</li>
     *         </ul>
     */
    private List deselectedNames = null;
  
    //
    // Constructor
    //

    /**
     * Creates a filter selecting all MBeanServerNotification notifications for
     * all ObjectNames.
     */
    public MBeanServerNotificationFilter() {

	super();

        if (isTraceOn())
            trace("Constructor: entering", null);

	enableType(MBeanServerNotification.REGISTRATION_NOTIFICATION);
	enableType(MBeanServerNotification.UNREGISTRATION_NOTIFICATION);

        if (isTraceOn())
            trace("Constructor: exiting", null);
	return;
    }

    //
    // Accessors
    //

    /**
     * Disables any MBeanServerNotification (all ObjectNames are
     * deselected).
     */
    public synchronized void disableAllObjectNames() {

        if (isTraceOn())
            trace("disableAllObjectNames: entering", null);

	selectedNames = new Vector();
	deselectedNames = null;

        if (isTraceOn())
            trace("disableAllObjectNames: exiting", null);
	return;
    }

    /**
     * Disables MBeanServerNotifications concerning given ObjectName.
     *
     * @param theObjName  ObjectName no longer of interest
     *
     * @exception IllegalArgumentException  if the given ObjectName is null
     */
    public synchronized void disableObjectName(ObjectName theObjName)
	throws IllegalArgumentException {

	if (theObjName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
            trace("disableObjectName: entering", theObjName.toString());

	// Removes from selected ObjectNames, if present
	if (selectedNames != null) {
	    if (selectedNames.size() != 0) {
		selectedNames.remove(theObjName);
	    }
	}

	// Adds it in deselected ObjectNames
	if (deselectedNames != null) {
	    // If all are deselected, no need to do anything :)
	    if (!(deselectedNames.contains(theObjName))) {
		// ObjectName was not already deselected
		deselectedNames.add(theObjName);
	    }
	}

        if (isTraceOn())
            trace("disableObjectName: exiting", null);
	return;
    }

    /**
     * Enables all MBeanServerNotifications (all ObjectNames are selected).
     */
    public synchronized void enableAllObjectNames() {

	if (isTraceOn())
            trace("enableAllObjectNames: entering", null);

    	selectedNames = null;
	deselectedNames = new Vector();

        if (isTraceOn())
            trace("enableAllObjectNames: exiting", null);
	return;
    }

    /**
     * Enables MBeanServerNotifications concerning given ObjectName.
     *
     * @param theObjName  ObjectName of interest
     *
     * @exception IllegalArgumentException  if the given ObjectName is null
     */
    public synchronized void enableObjectName(ObjectName theObjName)
	throws IllegalArgumentException {

	if (theObjName == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
            trace("enableObjectName: entering", theObjName.toString());

	// Removes from deselected ObjectNames, if present
	if (deselectedNames != null) {
	    if (deselectedNames.size() != 0) {
		deselectedNames.remove(theObjName);
	    }
	}

	// Adds it in selected ObjectNames
	if (selectedNames != null) {
	    // If all are selected, no need to do anything :)
	    if (!(selectedNames.contains(theObjName))) {
		// ObjectName was not already selected
		selectedNames.add(theObjName);
	    }
	}

        if (isTraceOn())
            trace("enableObjectName: exiting", null);
	return;
    }
		
    /**
     * Gets all the ObjectNames enabled.
     *
     * @return Vector of ObjectNames:
     * <P>- null means all ObjectNames are implicitly selected, except the
     * ObjectNames explicitly deselected
     * <P>- empty means all ObjectNames are deselected, i.e. no ObjectName
     * selected.
     */
    public synchronized Vector getEnabledObjectNames() {
	if (selectedNames != null) {
	    return (Vector)((Vector)selectedNames).clone();
	} else {
	    return null;
	}
    }

    /**
     * Gets all the ObjectNames disabled.
     *
     * @return Vector of ObjectNames:
     * <P>- null means all ObjectNames are implicitly deselected, except the
     * ObjectNames explicitly selected
     * <P>- empty means all ObjectNames are selected, i.e. no ObjectName
     * deselected.
     */
    public synchronized Vector getDisabledObjectNames() {
	if (deselectedNames != null) {
	    return (Vector)((Vector)deselectedNames).clone();
	} else {
	    return null;
	}
    }

    //
    // NotificationFilter interface
    //

    /**
     * Invoked before sending the specified notification to the listener.
     * <P>If:
     * <P>- the ObjectName of the concerned MBean is selected (explicitly OR
     * (implicitly and not explicitly deselected))
     * <P>AND
     * <P>- the type of the operation (registration or unregistration) is
     * selected
     * <P>then the notification is sent to the listener.
     *
     * @param theNtf  The notification to be sent.
     *
     * @return true if the notification has to be sent to the listener, false
     * otherwise.
     *
     * @exception IllegalArgumentException  if null parameter
     */
    public synchronized boolean isNotificationEnabled(Notification theNtf)
	throws IllegalArgumentException {

	if (theNtf == null) {
	    // Revisit [cebro] Localize message
	    String excMsg = "Invalid parameter.";
	    throw new IllegalArgumentException(excMsg);
	}

	if (isTraceOn())
            trace("isNotificationEnabled: entering", theNtf.toString());

	// Checks the type first
	String ntfType = theNtf.getType();
	Vector enabledTypes = getEnabledTypes();
	if (!(enabledTypes.contains(ntfType))) {

	    if (isTraceOn())
		trace("isNotificationEnabled: type not selected, exiting", null);
	    return false;
	}

	// We have a MBeanServerNotification: downcasts it
	MBeanServerNotification mbsNtf = (MBeanServerNotification)theNtf;

	// Checks the ObjectName
	ObjectName objName = mbsNtf.getMBeanName();
	// Is it selected?
	boolean isSelectedFlg = false;
	if (selectedNames != null) {
	    // Not all are implicitly selected:
	    // checks for explicit selection
	    if (selectedNames.size() == 0) {
		// All are explicitly not selected
		if (isTraceOn())
		    trace("isNotificationEnabled: no ObjectNames selected, exiting", null);
		return false;
	    }

	    isSelectedFlg = selectedNames.contains(objName);
	    if (!isSelectedFlg) {
		// Not in the explicit selected list
		if (isTraceOn())
		    trace("isNotificationEnabled: ObjectName not in selected list, exiting", null);
		return false;
	    }
	}

	if (!isSelectedFlg) {
	    // Not explicitly selected: is it deselected?

	    if (deselectedNames == null) {
		// All are implicitly deselected and it is not explicitly
		// selected
		if (isTraceOn())
		    trace("isNotificationEnabled: ObjectName not selected and all deselectedm, exiting", null);
		return false;

	    } else if (deselectedNames.contains(objName)) {
		// Explicitly deselected
		if (isTraceOn())
		    trace("isNotificationEnabled: ObjectName explicitly not selected, exiting", null);
		return false;
	    }
	}

	if (isTraceOn())
	    trace("isNotificationEnabled: ObjectName selected, exiting", null);
	return true;
    }

    // stuff for Tracing

    private static String localClassName = "MBeanServerNotificationFilter";

    // trace level
    private boolean isTraceOn() {
        return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_RELATION);
    }

//    private void trace(String className, String methodName, String info) {
//        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, className, methodName, info);
//    }

    private void trace(String methodName, String info) {
        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, localClassName, methodName, info);
	Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, "", "", "\n");
    }

//    private void trace(String className, String methodName, Exception e) {
//        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, className, methodName, e);
//    }

//    private void trace(String methodName, Exception e) {
//        Trace.send(Trace.LEVEL_TRACE, Trace.INFO_RELATION, localClassName, methodName, e);
//    }

    // debug level
    private boolean isDebugOn() {
        return Trace.isSelected(Trace.LEVEL_DEBUG, Trace.INFO_RELATION);
    }

//    private void debug(String className, String methodName, String info) {
//        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, className, methodName, info);
//    }

    private void debug(String methodName, String info) {
        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, localClassName, methodName, info);
	Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, "", "", "\n");
    }

//    private void debug(String className, String methodName, Exception e) {
//        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, className, methodName, e);
//    }

//    private void debug(String methodName, Exception e) {
//        Trace.send(Trace.LEVEL_DEBUG, Trace.INFO_RELATION, localClassName, methodName, e);
//    }

    /**
     * Deserializes an {@link MBeanServerNotificationFilter} from an {@link ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
      if (compat)
      {
        // Read an object serialized in the old serial form
        //
        ObjectInputStream.GetField fields = in.readFields();
	selectedNames = (List) fields.get("mySelectObjNameList", null);
	if (fields.defaulted("mySelectObjNameList"))
        {
          throw new NullPointerException("mySelectObjNameList");
        }
	deselectedNames = (List) fields.get("myDeselectObjNameList", null);
	if (fields.defaulted("myDeselectObjNameList"))
        {
          throw new NullPointerException("myDeselectObjNameList");
        }
      }
      else
      {
        // Read an object serialized in the new serial form
        //
        in.defaultReadObject();
      }
    }


    /**
     * Serializes an {@link MBeanServerNotificationFilter} to an {@link ObjectOutputStream}.
     */
    private void writeObject(ObjectOutputStream out)
	    throws IOException {
      if (compat)
      {
        // Serializes this instance in the old serial form
        //
        ObjectOutputStream.PutField fields = out.putFields();
	fields.put("mySelectObjNameList", (Vector)selectedNames);
	fields.put("myDeselectObjNameList", (Vector)deselectedNames);
	out.writeFields();
      }
      else
      {
        // Serializes this instance in the new serial form
        //
        out.defaultWriteObject();
      }
    }
}
