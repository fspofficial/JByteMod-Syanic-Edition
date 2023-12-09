package de.xbrowniecodez.jbytemod.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.SneakyThrows;
import me.grax.jbytemod.JByteMod;

public class UpdateChecker {

	public UpdateChecker() {
		JByteMod.LOGGER.log("Checking for updates...");
		JsonObject releaseInfo = fetchLatestReleaseInfo();

		if (releaseInfo != null) {
			String latestVersion = releaseInfo.get("name").getAsString();

			if (!latestVersion.equals(JByteMod.version)) {
				showUpdateDialog(latestVersion);
			}
		}
	}

	@SneakyThrows
	private JsonObject fetchLatestReleaseInfo() {
		URL url = new URL("https://api.github.com/repos/xBrownieCodezV2/JByteMod-Remastered/releases/latest");
		URLConnection connection = url.openConnection();

		try (InputStream inputStream = connection.getInputStream();
			 InputStreamReader reader = new InputStreamReader(inputStream)) {
			return JsonParser.parseReader(reader).getAsJsonObject();
		}
	}

	private void showUpdateDialog(String latestVersion) {
		int result = JOptionPane.showConfirmDialog(null,
				String.format("Version %s is available, would you like to download it?", latestVersion),
				"Update available", JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			openDownloadLink(latestVersion);
		}
	}

	@SneakyThrows
	private void openDownloadLink(String version) {
		URI downloadUri = new URI(String.format(
				"https://github.com/xBrownieCodezV2/JByteMod-Remastered/releases/download/%s/JByteMod-Remastered-%s.jar",
				version, version));

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			Desktop.getDesktop().browse(downloadUri);
		}
	}
}
