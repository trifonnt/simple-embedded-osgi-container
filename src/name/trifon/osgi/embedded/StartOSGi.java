package name.trifon.osgi.embedded;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

// This is Apache Felix specific class!
//import org.apache.felix.framework.FrameworkFactory;


public class StartOSGi {

	public static void main(String[] args) {
		// Start OSGi environment
		FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class)
				.iterator().next();
		Map<String, String> osgiConfigMap = new HashMap<>();

		// Expose Application Packages
		osgiConfigMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.adempiere,org.compiere,org.eevolution");

		// Control where OSGi stores its persistent data:
//		osgiConfigMap.put(Constants.FRAMEWORK_STORAGE, "/Users/neil/osgidata");

		// Request OSGi to clean its storage area on startup
		osgiConfigMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");

		// Provide the Java 1.5 execution environment
//		osgiConfigMap.put(Constants.FRAMEWORK_EXECUTIONENVIRONMENT, "J2SE-1.5");

		//TODO: Add some osgiConfig properties
		Framework osgiFramework = null;
		osgiFramework = frameworkFactory.newFramework( osgiConfigMap );

		try {
			osgiFramework.start();
			System.out.println("ADempiere OSGiMonitorServlet - OSGi Framework started!");
		} catch (BundleException e) {
			e.printStackTrace();
			return;
		}

		// Install and start Felix Shell bundles
		BundleContext bundleContext = osgiFramework.getBundleContext();
		List<Bundle> installedBundles = new LinkedList<Bundle>();

		try {
			installedBundles.add(bundleContext.installBundle("file:lib/org.apache.felix.shell-1.4.3.jar"));
		} catch (BundleException e) {
			e.printStackTrace();
			return;
		}

		try {
			installedBundles.add(bundleContext.installBundle("file:lib/org.apache.felix.shell.tui-1.4.1.jar"));
		} catch (BundleException e) {
			e.printStackTrace();
			return;
		}

		// Start bundles
		for (Bundle bundle : installedBundles) {
			try {
				// Check if this is fragment bundle
				if (bundle.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
					bundle.start();
				}
			} catch (BundleException e) {
				e.printStackTrace();
				return;
			}
		}

		// When we want to shutdown OSGi framework
		try {
			osgiFramework.waitForStop(0);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			System.exit(0);
		}

	}

}