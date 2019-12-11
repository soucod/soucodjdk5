/*
 * @(#)Resources.java	1.20 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.security.util;

/**
 * <p> This class represents the <code>ResourceBundle</code>
 * for javax.security.auth and sun.security.
 *
 * @version 1.20, 12/19/03
 */
public class Resources extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

	// shared (from jarsigner)
	{" ", " "},
	{"  ", "  "},
	{"      ", "      "},
	{", ", ", "},
	// shared (from keytool)
	{"\n", "\n"},
	{"*******************************************",
		"*******************************************"},
	{"*******************************************\n\n",
		"*******************************************\n\n"},
      
	// keytool
	{"keytool error: ", "keytool error: "},
	{"Illegal option:  ", "Illegal option:  "},
	{"-keystore must be NONE if -storetype is PKCS11",
		"-keystore must be NONE if -storetype is PKCS11"},
	{"-storepasswd and -keypasswd commands not supported if -storetype is PKCS11",
		"-storepasswd and -keypasswd commands not supported if -storetype is PKCS11"},
	{"-keypass and -new can not be specified if -storetype is PKCS11",
		"-keypass and -new can not be specified if -storetype is PKCS11"},
	{"if -protected is specified, then -storepass, -keypass, and -new must not be specified",
		"if -protected is specified, then -storepass, -keypass, and -new must not be specified"},
	{"Validity must be greater than zero",
		"Validity must be greater than zero"},
	{"provName not a provider", "{0} not a provider"},
	{"Must not specify both -v and -rfc with 'list' command",
		"Must not specify both -v and -rfc with 'list' command"},
	{"Key password must be at least 6 characters",
		"Key password must be at least 6 characters"},
	{"New password must be at least 6 characters",
		"New password must be at least 6 characters"},
	{"Keystore file exists, but is empty: ",
		"Keystore file exists, but is empty: "},
	{"Keystore file does not exist: ",
		"Keystore file does not exist: "},
	{"Must specify destination alias", "Must specify destination alias"},
	{"Must specify alias", "Must specify alias"},
	{"Keystore password must be at least 6 characters",
		"Keystore password must be at least 6 characters"},
	{"Enter keystore password:  ", "Enter keystore password:  "},
	{"Keystore password is too short - must be at least 6 characters",
	 "Keystore password is too short - must be at least 6 characters"},
	{"Too many failures - try later", "Too many failures - try later"},
	{"Certification request stored in file <filename>",
		"Certification request stored in file <{0}>"},
	{"Submit this to your CA", "Submit this to your CA"},
	{"Certificate stored in file <filename>",
		"Certificate stored in file <{0}>"},
	{"Certificate reply was installed in keystore",
		"Certificate reply was installed in keystore"},
	{"Certificate reply was not installed in keystore",
		"Certificate reply was not installed in keystore"},
	{"Certificate was added to keystore",
		"Certificate was added to keystore"},
	{"Certificate was not added to keystore",
		"Certificate was not added to keystore"},
	{"[Storing ksfname]", "[Storing {0}]"},
	{"alias has no public key (certificate)",
		"{0} has no public key (certificate)"},
	{"Cannot derive signature algorithm",
		"Cannot derive signature algorithm"},
	{"Alias <alias> does not exist",
		"Alias <{0}> does not exist"},
	{"Alias <alias> has no certificate",
		"Alias <{0}> has no certificate"},
	{"Key pair not generated, alias <alias> already exists",
		"Key pair not generated, alias <{0}> already exists"},
	{"Cannot derive signature algorithm",
		"Cannot derive signature algorithm"},
	{"Generating keysize bit keyAlgName key pair and self-signed certificate (sigAlgName)\n\tfor: x500Name",
		"Generating {0} bit {1} key pair and self-signed certificate ({2})\n\tfor: {3}"},
	{"Enter key password for <alias>", "Enter key password for <{0}>"},
	{"\t(RETURN if same as keystore password):  ",
		"\t(RETURN if same as keystore password):  "},
	{"Key password is too short - must be at least 6 characters",
		"Key password is too short - must be at least 6 characters"},
	{"Too many failures - key not added to keystore",
		"Too many failures - key not added to keystore"},
	{"Destination alias <dest> already exists",
		"Destination alias <{0}> already exists"},
	{"Password is too short - must be at least 6 characters",
		"Password is too short - must be at least 6 characters"},
	{"Too many failures. Key entry not cloned",
		"Too many failures. Key entry not cloned"},
	{"key password for <alias>", "key password for <{0}>"},
	{"Keystore entry for <id.getName()> already exists",
		"Keystore entry for <{0}> already exists"},
	{"Creating keystore entry for <id.getName()> ...",
		"Creating keystore entry for <{0}> ..."},
	{"No entries from identity database added",
		"No entries from identity database added"},
	{"Alias name: alias", "Alias name: {0}"},
	{"Creation date: keyStore.getCreationDate(alias)",
		"Creation date: {0,date}"},
	{"alias, keyStore.getCreationDate(alias), ",
		"{0}, {1,date}, "},
	{"alias, ", "{0}, "},
	{"Entry type: keyEntry", "Entry type: keyEntry"},
	{"keyEntry,", "keyEntry,"},
	{"Certificate chain length: ", "Certificate chain length: "},
	{"Certificate[(i + 1)]:", "Certificate[{0,number,integer}]:"},
	{"Certificate fingerprint (MD5): ", "Certificate fingerprint (MD5): "},
	{"Entry type: trustedCertEntry\n", "Entry type: trustedCertEntry\n"},
	{"trustedCertEntry,", "trustedCertEntry,"},
	{"Keystore type: ", "Keystore type: "},
	{"Keystore provider: ", "Keystore provider: "},
	{"Your keystore contains keyStore.size() entry",
		"Your keystore contains {0,number,integer} entry"},
	{"Your keystore contains keyStore.size() entries",
		"Your keystore contains {0,number,integer} entries"},
	{"Failed to parse input", "Failed to parse input"},
	{"Empty input", "Empty input"},
	{"Not X.509 certificate", "Not X.509 certificate"},
	{"Cannot derive signature algorithm",
		"Cannot derive signature algorithm"},
	{"alias has no public key", "{0} has no public key"},
	{"alias has no X.509 certificate", "{0} has no X.509 certificate"},
	{"New certificate (self-signed):", "New certificate (self-signed):"},
	{"Reply has no certificates", "Reply has no certificates"},
	{"Certificate not imported, alias <alias> already exists",
		"Certificate not imported, alias <{0}> already exists"},
	{"Input not an X.509 certificate", "Input not an X.509 certificate"},
	{"Certificate already exists in keystore under alias <trustalias>",
		"Certificate already exists in keystore under alias <{0}>"},
	{"Do you still want to add it? [no]:  ",
		"Do you still want to add it? [no]:  "},
	{"Certificate already exists in system-wide CA keystore under alias <trustalias>",
		"Certificate already exists in system-wide CA keystore under alias <{0}>"},
	{"Do you still want to add it to your own keystore? [no]:  ",
		"Do you still want to add it to your own keystore? [no]:  "},
	{"Trust this certificate? [no]:  ", "Trust this certificate? [no]:  "},
	{"YES", "YES"},
	{"New prompt: ", "New {0}: "},
	{"Passwords must differ", "Passwords must differ"},
	{"Re-enter new prompt: ", "Re-enter new {0}: "},
	{"They don't match; try again", "They don't match; try again"},
	{"Enter prompt alias name:  ", "Enter {0} alias name:  "},
	{"Enter alias name:  ", "Enter alias name:  "},
	{"\t(RETURN if same as for <otherAlias>)",
		"\t(RETURN if same as for <{0}>)"},
	{"*PATTERN* printX509Cert",
		"Owner: {0}\nIssuer: {1}\nSerial number: {2}\nValid from: {3} until: {4}\nCertificate fingerprints:\n\t MD5:  {5}\n\t SHA1: {6}"},
	{"What is your first and last name?",
		"What is your first and last name?"},
	{"What is the name of your organizational unit?",
		"What is the name of your organizational unit?"},
	{"What is the name of your organization?",
		"What is the name of your organization?"},
	{"What is the name of your City or Locality?",
		"What is the name of your City or Locality?"},
	{"What is the name of your State or Province?",
		"What is the name of your State or Province?"},
	{"What is the two-letter country code for this unit?",
		"What is the two-letter country code for this unit?"},
	{"Is <name> correct?", "Is {0} correct?"},
	{"no", "no"},
	{"yes", "yes"},
	{"y", "y"},
	{"  [defaultValue]:  ", "  [{0}]:  "},
	{"Alias <alias> has no (private) key",
		"Alias <{0}> has no (private) key"},
	{"Recovered key is not a private key",
		"Recovered key is not a private key"},
	{"*****************  WARNING WARNING WARNING  *****************",
	    "*****************  WARNING WARNING WARNING  *****************"},
	{"* The integrity of the information stored in your keystore  *",
	    "* The integrity of the information stored in your keystore  *"},
	{"* has NOT been verified!  In order to verify its integrity, *",
	    "* has NOT been verified!  In order to verify its integrity, *"},
	{"* you must provide your keystore password.                  *",
	    "* you must provide your keystore password.                  *"},
	{"Certificate reply does not contain public key for <alias>",
		"Certificate reply does not contain public key for <{0}>"},
	{"Incomplete certificate chain in reply",
		"Incomplete certificate chain in reply"},
	{"Certificate chain in reply does not verify: ",
		"Certificate chain in reply does not verify: "},
	{"Top-level certificate in reply:\n",
		"Top-level certificate in reply:\n"},
	{"... is not trusted. ", "... is not trusted. "},
	{"Install reply anyway? [no]:  ", "Install reply anyway? [no]:  "},
	{"NO", "NO"},
	{"Public keys in reply and keystore don't match",
		"Public keys in reply and keystore don't match"},
	{"Certificate reply and certificate in keystore are identical",
		"Certificate reply and certificate in keystore are identical"},
	{"Failed to establish chain from reply",
		"Failed to establish chain from reply"},
	{"n", "n"},
	{"Wrong answer, try again", "Wrong answer, try again"},
	{"keytool usage:\n", "keytool usage:\n"},
	{"-certreq     [-v] [-protected]",
		"-certreq     [-v] [-protected]"},
	{"\t     [-alias <alias>] [-sigalg <sigalg>]",
		"\t     [-alias <alias>] [-sigalg <sigalg>]"},
	{"\t     [-file <csr_file>] [-keypass <keypass>]",
		"\t     [-file <csr_file>] [-keypass <keypass>]"},
	{"\t     [-keystore <keystore>] [-storepass <storepass>]",
		"\t     [-keystore <keystore>] [-storepass <storepass>]"},
	{"\t     [-storetype <storetype>] [-providerName <name>]",
		"\t     [-storetype <storetype>] [-providerName <name>]"},
	{"\t     [-providerClass <provider_class_name> [-providerArg <arg>]] ...",
		"\t     [-providerClass <provider_class_name> [-providerArg <arg>]] ..."},
	{"-delete      [-v] [-protected] -alias <alias>",
		"-delete      [-v] [-protected] -alias <alias>"},
	/** rest is same as -certreq starting from -keystore **/

	{"-export      [-v] [-rfc] [-protected]",
		"-export      [-v] [-rfc] [-protected]"},
	{"\t     [-alias <alias>] [-file <cert_file>]",
		"\t     [-alias <alias>] [-file <cert_file>]"},
	/** rest is same as -certreq starting from -keystore **/

	{"-genkey      [-v] [-protected]",
		"-genkey      [-v] [-protected]"},
	{"\t     [-alias <alias>]", "\t     [-alias <alias>]"},
	{"\t     [-keyalg <keyalg>] [-keysize <keysize>]",
		"\t     [-keyalg <keyalg>] [-keysize <keysize>]"},
	{"\t     [-sigalg <sigalg>] [-dname <dname>]",
		"\t     [-sigalg <sigalg>] [-dname <dname>]"},
	{"\t     [-validity <valDays>] [-keypass <keypass>]",
		"\t     [-validity <valDays>] [-keypass <keypass>]"},
	/** rest is same as -certreq starting from -keystore **/

	{"-help", "-help"},
	{"-identitydb  [-v] [-protected]",
		"-identitydb  [-v] [-protected]"},
	{"\t     [-file <idb_file>]", "\t     [-file <idb_file>]"},
	/** rest is same as -certreq starting from -keystore **/

	{"-import      [-v] [-noprompt] [-trustcacerts] [-protected]",
		"-import      [-v] [-noprompt] [-trustcacerts] [-protected]"},
	{"\t     [-alias <alias>]", "\t     [-alias <alias>]"},
	{"\t     [-file <cert_file>] [-keypass <keypass>]",
		"\t     [-file <cert_file>] [-keypass <keypass>]"},
	/** rest is same as -certreq starting from -keystore **/

	{"-keyclone    [-v] [-protected]",
		"-keyclone    [-v] [-protected]"},
	{"\t     [-alias <alias>] -dest <dest_alias>",
		"\t     [-alias <alias>] -dest <dest_alias>"},
	{"\t     [-keypass <keypass>] [-new <new_keypass>]",
		"\t     [-keypass <keypass>] [-new <new_keypass>]"},
	/** rest is same as -certreq starting from -keystore **/

	{"-keypasswd   [-v] [-alias <alias>]",
		"-keypasswd   [-v] [-alias <alias>]"},
	{"\t     [-keypass <old_keypass>] [-new <new_keypass>]",
		"\t     [-keypass <old_keypass>] [-new <new_keypass>]"},
	/** rest is same as -certreq starting from -keystore **/

	{"-list        [-v | -rfc] [-protected]",
		"-list        [-v | -rfc] [-protected]"},
	{"\t     [-alias <alias>]", "\t     [-alias <alias>]"},
	/** rest is same as -certreq starting from -keystore **/

	{"-printcert   [-v] [-file <cert_file>]",
		"-printcert   [-v] [-file <cert_file>]"},

	{"-selfcert    [-v] [-protected]",
		"-selfcert    [-v] [-protected]"},
	{"\t     [-alias <alias>]", "\t     [-alias <alias>]"},
	{"\t     [-dname <dname>] [-validity <valDays>]",
		"\t     [-dname <dname>] [-validity <valDays>]"},
	{"\t     [-keypass <keypass>] [-sigalg <sigalg>]",
		"\t     [-keypass <keypass>] [-sigalg <sigalg>]"},
	/** rest is same as -certreq starting from -keystore **/

	{"-storepasswd [-v] [-new <new_storepass>]",
		"-storepasswd [-v] [-new <new_storepass>]"},
	/** rest is same as -certreq starting from -keystore **/

	// policytool
	{"Warning: A public key for alias 'signers[i]' does not exist.",
		"Warning: A public key for alias {0} does not exist."},
	{"Warning: Class not found: ",
		"Warning: Class not found: "},
	{"Policy File opened successfully",
		"Policy File opened successfully"},
	{"null Keystore name", "null Keystore name"},
	{"Warning: Unable to open Keystore: ",
		"Warning: Unable to open Keystore: "},
	{"Illegal option: ", "Illegal option: "},
	{"Usage: policytool [options]", "Usage: policytool [options]"},
	{"  [-file <file>]    policy file location",
		"  [-file <file>]    policy file location"},
	{"New", "New"},
	{"Open", "Open"},
	{"Save", "Save"},
	{"Save As", "Save As"},
	{"View Warning Log", "View Warning Log"},
	{"Exit", "Exit"},
	{"Add Policy Entry", "Add Policy Entry"},
	{"Edit Policy Entry", "Edit Policy Entry"},
	{"Remove Policy Entry", "Remove Policy Entry"},
	{"Change KeyStore", "Change KeyStore"},
	{"Add Public Key Alias", "Add Public Key Alias"},
	{"Remove Public Key Alias", "Remove Public Key Alias"},
	{"File", "File"},
	{"Edit", "Edit"},
	{"Policy File:", "Policy File:"},
	{"Keystore:", "Keystore:"},
	{"Error parsing policy file policyFile: pppe.getMessage()",
		"Error parsing policy file {0}: {1}"},
	{"Could not find Policy File: ", "Could not find Policy File: "},
	{"Policy Tool", "Policy Tool"},
	{"Errors have occurred while opening the policy configuration.  View the Warning Log for more information.",
		"Errors have occurred while opening the policy configuration.  View the Warning Log for more information."},
	{"Error", "Error"},
	{"OK", "OK"},
	{"Status", "Status"},
	{"Warning", "Warning"},
	{"Permission:                                                       ",
		"Permission:                                                       "},
	{"Target Name:                                                    ",
		"Target Name:                                                    "},
	{"library name", "library name"},
	{"package name", "package name"},
	{"property name", "property name"},
	{"provider name", "provider name"},
	{"Actions:                                                             ",
		"Actions:                                                             "},
	{"OK to overwrite existing file filename?",
		"OK to overwrite existing file {0}?"},
	{"Cancel", "Cancel"},
	{"CodeBase:", "CodeBase:"},
	{"SignedBy:", "SignedBy:"},
	{"  Add Permission", "  Add Permission"},
	{"  Edit Permission", "  Edit Permission"},
	{"Remove Permission", "Remove Permission"},
	{"Done", "Done"},
	{"New KeyStore URL:", "New KeyStore URL:"},
	{"New KeyStore Type:", "New KeyStore Type:"},
	{"Permissions", "Permissions"},
	{"  Edit Permission:", "  Edit Permission:"},
	{"  Add New Permission:", "  Add New Permission:"},
	{"Signed By:", "Signed By:"},
	{"Permission and Target Name must have a value",
		"Permission and Target Name must have a value"},
	{"Remove this Policy Entry?", "Remove this Policy Entry?"},
	{"Overwrite File", "Overwrite File"},
	{"Policy successfully written to filename",
		"Policy successfully written to {0}"},
	{"null filename", "null filename"},
	{"filename not found", "{0} not found"},
	{"     Save changes?", "     Save changes?"},
	{"Yes", "Yes"},
	{"No", "No"},
	{"Error: Could not open policy file, filename, because of parsing error: pppe.getMessage()",
		"Error: Could not open policy file, {0}, because of parsing error: {1}"},
	{"Permission could not be mapped to an appropriate class",
		"Permission could not be mapped to an appropriate class"},
	{"Policy Entry", "Policy Entry"},
	{"Save Changes", "Save Changes"},
	{"No Policy Entry selected", "No Policy Entry selected"},
	{"Keystore", "Keystore"},
	{"KeyStore URL must have a valid value",
		"KeyStore URL must have a valid value"},
	{"Invalid value for Actions", "Invalid value for Actions"},
	{"No permission selected", "No permission selected"},
	{"Warning: Invalid argument(s) for constructor: ",
		"Warning: Invalid argument(s) for constructor: "},
	{"Add Principal", "Add Principal"},
	{"Edit Principal", "Edit Principal"},
	{"Remove Principal", "Remove Principal"},
	{"Principal Type:", "Principal Type:"},
        {"Principal Name:", "Principal Name:"},
	{"Illegal Principal Type", "Illegal Principal Type"},
	{"No principal selected", "No principal selected"},
	{"Principals:", "Principals:"},
	{"Principals", "Principals"},
	{"  Add New Principal:", "  Add New Principal:"},
	{"  Edit Principal:", "  Edit Principal:"},
	{"name", "name"},
	{"Cannot Specify Principal with a Wildcard Class without a Wildcard Name",
	    "Cannot Specify Principal with a Wildcard Class without a Wildcard Name"},
	{"Cannot Specify Principal without a Class",
	    "Cannot Specify Principal without a Class"},

        {"Cannot Specify Principal without a Name",
            "Cannot Specify Principal without a Name"},

	// javax.security.auth.PrivateCredentialPermission
	{"invalid null input(s)", "invalid null input(s)"},
	{"actions can only be 'read'", "actions can only be 'read'"},
	{"permission name [name] syntax invalid: ",
		"permission name [{0}] syntax invalid: "},
	{"Credential Class not followed by a Principal Class and Name",
		"Credential Class not followed by a Principal Class and Name"},
	{"Principal Class not followed by a Principal Name",
		"Principal Class not followed by a Principal Name"},
	{"Principal Name must be surrounded by quotes",
		"Principal Name must be surrounded by quotes"},
	{"Principal Name missing end quote",
		"Principal Name missing end quote"},
	{"PrivateCredentialPermission Principal Class can not be a wildcard (*) value if Principal Name is not a wildcard (*) value",
		"PrivateCredentialPermission Principal Class can not be a wildcard (*) value if Principal Name is not a wildcard (*) value"},
	{"CredOwner:\n\tPrincipal Class = class\n\tPrincipal Name = name",
		"CredOwner:\n\tPrincipal Class = {0}\n\tPrincipal Name = {1}"},

	// javax.security.auth.x500
	{"provided null name", "provided null name"},

	// javax.security.auth.Subject
	{"invalid null AccessControlContext provided",
		"invalid null AccessControlContext provided"},
	{"invalid null action provided", "invalid null action provided"},
	{"invalid null Class provided", "invalid null Class provided"},
	{"Subject:\n", "Subject:\n"},
	{"\tPrincipal: ", "\tPrincipal: "},
	{"\tPublic Credential: ", "\tPublic Credential: "},
	{"\tPrivate Credentials inaccessible\n",
		"\tPrivate Credentials inaccessible\n"},
	{"\tPrivate Credential: ", "\tPrivate Credential: "},
	{"\tPrivate Credential inaccessible\n",
		"\tPrivate Credential inaccessible\n"},
	{"Subject is read-only", "Subject is read-only"},
	{"attempting to add an object which is not an instance of java.security.Principal to a Subject's Principal Set",
		"attempting to add an object which is not an instance of java.security.Principal to a Subject's Principal Set"},
	{"attempting to add an object which is not an instance of class",
		"attempting to add an object which is not an instance of {0}"},

	// javax.security.auth.login.AppConfigurationEntry
	{"LoginModuleControlFlag: ", "LoginModuleControlFlag: "},

	// javax.security.auth.login.LoginContext
	{"Invalid null input: name", "Invalid null input: name"},
	{"No LoginModules configured for name",
	 "No LoginModules configured for {0}"},
	{"invalid null Subject provided", "invalid null Subject provided"},
	{"invalid null CallbackHandler provided",
		"invalid null CallbackHandler provided"},
	{"null subject - logout called before login",
		"null subject - logout called before login"},
	{"unable to instantiate LoginModule, module, because it does not provide a no-argument constructor",
		"unable to instantiate LoginModule, {0}, because it does not provide a no-argument constructor"},
	{"unable to instantiate LoginModule",
		"unable to instantiate LoginModule"},
	{"unable to find LoginModule class: ",
		"unable to find LoginModule class: "},
	{"unable to access LoginModule: ",
		"unable to access LoginModule: "},
	{"Login Failure: all modules ignored",
		"Login Failure: all modules ignored"},

	// sun.security.provider.PolicyFile

	{"java.security.policy: error parsing policy:\n\tmessage",
		"java.security.policy: error parsing {0}:\n\t{1}"},
	{"java.security.policy: error adding Permission, perm:\n\tmessage",
		"java.security.policy: error adding Permission, {0}:\n\t{1}"},
	{"java.security.policy: error adding Entry:\n\tmessage",
		"java.security.policy: error adding Entry:\n\t{0}"},
	{"alias name not provided (pe.name)", "alias name not provided ({0})"},
	{"unable to perform substitution on alias, suffix",
		"unable to perform substitution on alias, {0}"},
	{"substitution value, prefix, unsupported",
		"substitution value, {0}, unsupported"},
	{"(", "("},
	{")", ")"},
	{"type can't be null","type can't be null"},

	// sun.security.provider.PolicyParser
	{"keystorePasswordURL can not be specified without also specifying keystore",
		"keystorePasswordURL can not be specified without also specifying keystore"},
	{"expected keystore type", "expected keystore type"},
	{"expected keystore provider", "expected keystore provider"},
	{"multiple Codebase expressions",
	        "multiple Codebase expressions"},
        {"multiple SignedBy expressions","multiple SignedBy expressions"},
	{"SignedBy has empty alias","SignedBy has empty alias"},
	{"can not specify Principal with a wildcard class without a wildcard name",
		"can not specify Principal with a wildcard class without a wildcard name"},
	{"expected codeBase or SignedBy or Principal",
		"expected codeBase or SignedBy or Principal"},
	{"expected permission entry", "expected permission entry"},
	{"number ", "number "},
	{"expected [expect], read [end of file]",
		"expected [{0}], read [end of file]"},
	{"expected [;], read [end of file]",
		"expected [;], read [end of file]"},
	{"line number: msg", "line {0}: {1}"},
	{"line number: expected [expect], found [actual]",
		"line {0}: expected [{1}], found [{2}]"},
	{"null principalClass or principalName",
		"null principalClass or principalName"},

	// sun.security.pkcs11.SunPKCS11
	{"PKCS11 Token [providerName] Password: ",
		"PKCS11 Token [{0}] Password: "},

	/* --- DEPRECATED --- */
	// javax.security.auth.Policy
	{"unable to instantiate Subject-based policy",
		"unable to instantiate Subject-based policy"}
    };


    /**
     * Returns the contents of this <code>ResourceBundle</code>.
     *
     * <p>
     *
     * @return the contents of this <code>ResourceBundle</code>.
     */
    public Object[][] getContents() {
	return contents;
    }
}