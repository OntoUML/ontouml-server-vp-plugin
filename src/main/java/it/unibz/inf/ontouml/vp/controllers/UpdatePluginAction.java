package it.unibz.inf.ontouml.vp.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.GitHubRelease;
import it.unibz.inf.ontouml.vp.utils.GitHubReleaseAsset;
import it.unibz.inf.ontouml.vp.utils.GitHubUtils;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

public class UpdatePluginAction implements VPActionController {

	private static final int BUFFER_SIZE = 4096;

	@Override
	public void performAction(VPAction arg0) {

		try {
			GitHubUtils.lookupUpdates();
			GitHubRelease selectedRelease = ViewUtils.updateDialog();
			GitHubReleaseAsset pluginAsset = selectedRelease != null ? selectedRelease.getPluginAsset() : null;

			if (selectedRelease == null || pluginAsset == null) {
				return;
			}

			File downloadedFile = GitHubUtils.downloadReleaseAsset(pluginAsset);

			String destinationDirName = pluginAsset.getName().replace(".zip", "");
			File pluginDir = ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID).getPluginDir();
			// TODO: remove downloadsDir
//			File downloadsDir = new File(pluginDir.getParentFile(), "downloads");
//			File destinationDir = new File(downloadsDir, destinationDirName);
			File destinationDir = new File(pluginDir.getParentFile(), destinationDirName);

			System.out.println("DESTINATION: " + destinationDir);

			UpdatePluginAction.unzip(downloadedFile, destinationDir);
			deleteFolderContents(pluginDir.getParentFile(),
					content -> content.isDirectory() && content.getName().contains("ontouml-vp-plugin")
					&& !content.getName().equals(destinationDirName));
			ViewUtils.updateSuccessDialog();
		} catch (Exception e) {
			ViewUtils.updateErrorDialog();
			e.printStackTrace();
		}
	}

	@Override
	public void update(VPAction arg0) {
	}

	private static void unzip(File zipFile, File destDirectory) throws IOException {
		if (!destDirectory.exists()) {
			destDirectory.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		File file = new File(filePath);
		file.createNewFile();
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	private void deleteFolderContents(File folder, Predicate<File> condition) {
		final File[] contents = folder.listFiles();

		if (contents == null) {
			return;
		}

		for (File content : contents) {
			if (condition == null || condition.test(content)) {
				if (content.isDirectory()) {
					deleteFolderContents(content, null);
				}
				content.delete();
			}
		}
	}

}
