/*
 * @(#)DnsContextFactory.java	1.11 04/07/16
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jndi.dns;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.*;
import javax.naming.spi.*;

import com.sun.jndi.toolkit.url.UrlUtil;
import sun.net.dns.ResolverConfiguration;	// available since 1.4.1


/**
 * A DnsContextFactory serves as the initial context factory for DNS.
 *
 * <p> When an initial context is being created, the environment
 * property "java.naming.provider.url" should contain a DNS pseudo-URL
 * (see DnsUrl) or a space-separated list of them.  Multiple URLs must
 * all have the same domain value.
 * If the property is not set, the default "dns:" is used.
 *
 * @author Scott Seligman
 * @version 1.11 04/07/16
 */


public class DnsContextFactory implements InitialContextFactory {

    private static final String DEFAULT_URL = "dns:";


    public Context getInitialContext(Hashtable<?,?> env) throws NamingException {
	if (env == null) {
	    env = new Hashtable(5);
	}
	return urlToContext(getInitCtxUrl(env), env);
    }

    public static DnsContext getContext(String domain,
					String[] servers, Hashtable<?,?> env)
	    throws NamingException {
	return new DnsContext(domain, servers, env);
    }

    /*
     * "urls" are used to determine the servers, but any domain
     * components are overridden by "domain".
     */
    public static DnsContext getContext(String domain,
					DnsUrl[] urls, Hashtable env)
	    throws NamingException {

	String[] servers = serversForUrls(urls);
	DnsContext ctx = getContext(domain, servers, env);
	if (platformServersUsed(urls)) {
	    ctx.setProviderUrl(constructProviderUrl(domain, servers));
	}
	return ctx;
    }

    /*
     * Public for use by product test suite.
     */
    public static boolean platformServersAvailable() {
	return !ResolverConfiguration.open().nameservers().isEmpty();
    }

    private static Context urlToContext(String url, Hashtable env)
	    throws NamingException {

	DnsUrl[] urls;
	try {
	    urls = DnsUrl.fromList(url);
	} catch (MalformedURLException e) {
	    throw new ConfigurationException(e.getMessage());
	}
	if (urls.length == 0) {
	    throw new ConfigurationException(
		    "Invalid DNS pseudo-URL(s): " + url);
	}
	String domain = urls[0].getDomain();

	// If multiple urls, all must have the same domain.
	for (int i = 1; i < urls.length; i++) {
	    if (!domain.equalsIgnoreCase(urls[i].getDomain())) {
		throw new ConfigurationException(
			"Conflicting domains: " + url);
	    }
	}
	return getContext(domain, urls, env);
    }

    /*
     * Returns all the servers specified in a set of URLs.
     * If a URL has no host (or port), the servers configured on the
     * underlying platform are used if possible.  If no configured
     * servers can be found, then fall back to the old behavior of
     * using "localhost".
     * There must be at least one URL.
     */
    private static String[] serversForUrls(DnsUrl[] urls)
	    throws NamingException {
  
	if (urls.length == 0) {
	    throw new ConfigurationException("DNS pseudo-URL required");
	}

	List servers = new ArrayList();

	for (int i = 0; i < urls.length; i++) {
	    String server = urls[i].getHost();
	    int port = urls[i].getPort();

	    if (server == null && port < 0) {
		// No server or port given, so look to underlying platform.
		// ResolverConfiguration does some limited caching, so the
		// following is reasonably efficient even if called rapid-fire.
		List platformServers =
		    ResolverConfiguration.open().nameservers();
		if (!platformServers.isEmpty()) {
		    servers.addAll(platformServers);
		    continue;  // on to next URL (if any, which is unlikely)
		}
	    }

	    if (server == null) {
		server = "localhost";
	    }
	    servers.add((port < 0)
			? server
			: server + ":" + port);
	}
	return (String[]) servers.toArray(
					new String[servers.size()]);
    }

    /*
     * Returns true if serversForUrls(urls) would make use of servers
     * from the underlying platform.
     */
    private static boolean platformServersUsed(DnsUrl[] urls) {
	if (!platformServersAvailable()) {
	    return false;
	}
	for (int i = 0; i < urls.length; i++) {
	    if (urls[i].getHost() == null &&
		urls[i].getPort() < 0) {
		return true;
	    }
	}
	return false;
    }

    /*
     * Returns a value for the PROVIDER_URL property (space-separated URL
     * Strings) that reflects the given domain and servers.
     * Each server is of the form "server[:port]".
     * There must be at least one server.
     * IPv6 literal host names include delimiting brackets.
     */
    private static String constructProviderUrl(String domain,
					       String[] servers) {
	String path = "";
	if (!domain.equals(".")) {
	    try {
		path = "/" + UrlUtil.encode(domain, "ISO-8859-1");
	    } catch (java.io.UnsupportedEncodingException e) {
		// assert false : "ISO-Latin-1 charset unavailable";
	    }
	}

	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < servers.length; i++) {
	    if (i > 0) {
		buf.append(' ');
	    }
	    buf.append("dns://").append(servers[i]).append(path);
	}
	return buf.toString();
    }

    /*
     * Reads environment to find URL(s) of initial context.
     * Default URL is "dns:".
     */
    private static String getInitCtxUrl(Hashtable env) {
	String url = (String) env.get(Context.PROVIDER_URL);
	return ((url != null) ? url : DEFAULT_URL);
    }
}
