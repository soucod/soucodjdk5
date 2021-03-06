/*
 * @(#)WIExplorerBrowserService.java	1.40 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.plugin.services;

import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import com.sun.deploy.services.WPlatformService;


/** 
 * WIExplorerBrowserService is a class that encapsulates the browser service
 * in Internet Explorer
 */
public final class WIExplorerBrowserService extends WPlatformService implements BrowserService
{
    /**
     * Return cookie handler.
     */
    public com.sun.deploy.net.cookie.CookieHandler getCookieHandler()
    {
	return new com.sun.deploy.net.cookie.IExplorerCookieHandler();
    }

    /**
     * Return proxy config.
     */
    public com.sun.deploy.net.proxy.BrowserProxyConfig getProxyConfig()
    {
	return new com.sun.deploy.net.proxy.WIExplorerProxyConfig();
    }

    /**
     * Return auto proxy handler.
     */
    public com.sun.deploy.net.proxy.ProxyHandler getAutoProxyHandler()
    {
	return new sun.plugin.net.proxy.PluginAutoProxyHandler();
    }

    /**
     * Return browser proxy handler.
     */
    public com.sun.deploy.net.proxy.ProxyHandler getBrowserProxyHandler()
    {
	return null;
    }

    /**
     * Return browser signing root certificate store. 
     */
    public com.sun.deploy.security.CertStore getBrowserSigningRootCertStore()
    {
	// System default signing root cert store is in Internet Explorer
	return new com.sun.deploy.security.WIExplorerSigningRootCertStore();
    }

    /**
     * Return browser SSL root certificate store. 
     */
    public com.sun.deploy.security.CertStore getBrowserSSLRootCertStore()
    {
	// System default SSL root cert store is in Internet Explorer
	return new com.sun.deploy.security.WIExplorerSSLRootCertStore();
    }

    /**
     * Return browser trusted signing certificate store. 
     */
    public com.sun.deploy.security.CertStore getBrowserTrustedCertStore()
    {
	// System default trusted signing cert store is in Internet Explorer
	return new com.sun.deploy.security.WIExplorerSigningCertStore();
    }

    /**
     * Return browser client authentication key store. 
     */
    public java.security.KeyStore getBrowserClientAuthKeyStore()
    {
	KeyStore ks = (KeyStore) AccessController.doPrivileged(new PrivilegedAction()
	{
	    public Object run() 
	    {
		try
		{
		    // System default client authentication key store is in Internet Explorer
		    return KeyStore.getInstance("WIExplorerMy");
		}
		catch (KeyStoreException e)
		{
		    e.printStackTrace();
		    return null;
		}
	    }
	});    
    
	return ks;
    }

    /**
     * Return applet context
     */
    public sun.plugin.viewer.context.PluginAppletContext getAppletContext()
    {
	return new sun.plugin.viewer.context.IExplorerAppletContext();
    }

    /**
     * Return beans context
     */
    public sun.plugin.viewer.context.PluginBeansContext getBeansContext()
    {
	sun.plugin.viewer.context.PluginBeansContext pbc = new sun.plugin.viewer.context.PluginBeansContext();
	pbc.setPluginAppletContext(new sun.plugin.viewer.context.IExplorerAppletContext());

	return pbc;
    }

    /**
     * Check if browser is IE.
     */
    public boolean isIExplorer()
    {
	return true;
    }

    /**
     * Check if browser is NS.
     */
    public boolean isNetscape()
    {
	return false;
    }

    /**
     * Return browser version.
     */
    public float getBrowserVersion()
    {
	return 5.0f;
    }

    /**
     * Check if console should be iconified on close.
     */
    public boolean isConsoleIconifiedOnClose()
    {
	return false;
    }

    /**
     * Install browser event listener
     * @since 1.4.1
     */
    public native boolean installBrowserEventListener();

    /**
     * Return browser authenticator.
     */
    public com.sun.deploy.security.BrowserAuthenticator getBrowserAuthenticator() 
    {
	// System default authenticator is Internet Explorer 	    
	return new com.sun.deploy.security.WIExplorerBrowserAuthenticator();
    }

    /**
     * Browser element mapping
     * @since 1.4.2
     */
    public String mapBrowserElement(String rawName) {
	return rawName;
    }
    
}


