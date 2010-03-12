package pl.pronux.sokker.launcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import pl.pronux.sokker.actions.SettingsManager;
import pl.pronux.sokker.bean.SynchronizerConfiguration;
import pl.pronux.sokker.data.properties.PropertiesDatabase;
import pl.pronux.sokker.data.sql.SQLQuery;
import pl.pronux.sokker.data.sql.SQLSession;
import pl.pronux.sokker.downloader.Synchronizer;
import pl.pronux.sokker.handlers.SettingsHandler;
import pl.pronux.sokker.model.ProxySettings;
import pl.pronux.sokker.model.SokkerViewerSettings;
import pl.pronux.sokker.ui.Viewer;
import pl.pronux.sokker.ui.widgets.custom.Monitor;
import pl.pronux.sokker.ui.widgets.shells.BugReporter;
import pl.pronux.sokker.utils.Log;
import pl.pronux.sokker.utils.file.PropertiesChecker;

public class Launcher {

	private static SettingsManager settingsManager = SettingsManager.instance();

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {

		try {
			SokkerViewerSettings settings = settingsManager.getSettings();
			if (settings.isCheckProperties()) {
				new PropertiesChecker().checkAll();
				PropertiesDatabase.reload();
				settings = settingsManager.getSettings();
				settings.setCheckProperties(false);
				settingsManager.updateSettings(settings);
			}

			SettingsHandler.setSokkerViewerSettings(settings);

			// base directory settings
			settings.setBaseDirectory(System.getProperty("user.dir"));
			ProxySettings proxySettings = SettingsHandler.getSokkerViewerSettings().getProxySettings();
			setProxy(proxySettings);

			if (args.length == 1 && args[0].equals("--download-only")) {
				SQLQuery.setSettings(settings);
				SynchronizerConfiguration synchronizerConfiguration = new SynchronizerConfiguration();
				synchronizerConfiguration.checkDownloadAll();
				new Synchronizer(settings, synchronizerConfiguration).run(new Monitor());
			} else if (args.length == 0) {
				Display display = new Display();
				try {
					new Viewer(display, SWT.SHELL_TRIM).open();
				} catch (Exception e) {
					new BugReporter(display).openErrorMessage("SokkerViewer", e);
				} finally {
					display.dispose();
				}

			} else {
				System.out.println(showHelp());
			}

		} catch (Exception e) {
			Log.error("Error Viewer", e); //$NON-NLS-1$
		} finally {
			Log.close();
			try {
				SQLSession.close();
			} catch (SQLException e) {
			}
		}
	}

	private static String showHelp() {
		return "--donwload-only\r\n" + //$NON-NLS-1$
			   "--help"; //$NON-NLS-1$
	}

	private static void setProxy(ProxySettings proxySettings) {
		if (proxySettings.isEnabled()) {
			System.setProperty("proxySet", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			System.setProperty("http.proxyHost", proxySettings.getHostname()); //$NON-NLS-1$
			System.setProperty("http.proxyPort", String.valueOf(proxySettings.getPort())); //$NON-NLS-1$
			System.setProperty("http.proxyUser", proxySettings.getUsername()); //$NON-NLS-1$
			System.setProperty("http.proxyPassword", proxySettings.getPassword()); //$NON-NLS-1$
		}
	}
}
