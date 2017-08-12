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

public class StartOSGi {

	public static void main(String[] args) {
		// Start OSGi environment
		FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class)
				.iterator().next();
		Map<String, String> osgiConfig = new HashMap<>();

		// Exposing Application Packages
		osgiConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.adempiere,org.compiere,org.eevolution");

		//TODO: Add some osgiConfig properties
		Framework osgiFramework = null;
		osgiFramework = frameworkFactory.newFramework( osgiConfig );

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
				bundle.start();
			} catch (BundleException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}