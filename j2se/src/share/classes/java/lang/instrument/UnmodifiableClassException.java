/*
 * @(#)UnmodifiableClassException.java	1.2 04/03/01
 *
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package java.lang.instrument;

/**
 * Thrown by an implementation of
 * {@link java.lang.instrument.Instrumentation#redefineClasses Instrumentation.redefineClasses}
 * when one of the specified classes cannot be modified.
 *
 * @see     java.lang.instrument.Instrumentation#redefineClasses
 * @since   JDK1.5
 */
public class UnmodifiableClassException extends Exception {
    /**
     * Constructs an <code>UnmodifiableClassException</code> with no 
     * detail message. 
     */
    public
    UnmodifiableClassException() {
        super();
    }

    /**
     * Constructs an <code>UnmodifiableClassException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public
    UnmodifiableClassException(String s) {
        super(s);
    }
}
