package br.com.zenix.core.proxy.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.google.common.base.Preconditions;

import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class FileManager extends SimpleHandler {

	public static final File MANAGER_DIRECTORY = new File("/root/servers/manager/");

	public FileManager(ProxyManager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public static final String DIRECTORY = "plugins/QueueSystem/";

	public Configuration getConfiguration(String name) {
		File file = getFile(name);
		if (file == null) {
			return null;
		}
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save(Configuration configuration, String name) {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, getFile(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getFile(String name) {
		File file = new File("plugins/Proxy/", name);
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public boolean copyFolder(File src, File dest) {
		try {
			if (src.isDirectory()) {
				if (!dest.exists())
					dest.mkdir();

				String files[] = src.list();

				for (String file : files) {
					File srcFile = new File(src, file);
					File destFile = new File(dest, file);
					copyFolder(srcFile, destFile);
				}
			} else {
				InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dest);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0)
					out.write(buffer, 0, length);

				in.close();
				out.close();
			}
			return true;
		} catch (Exception e) {
			getLogger().error("Error when the plugin is trying to copy the path " + src.getAbsolutePath() + " to "
					+ dest.getAbsolutePath() + ".", e);
		}
		return false;
	}

	public boolean deleteFile(Path path) {
		Preconditions.checkNotNull(path);
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFileFailed(Path file, IOException e) {
					return handleException(e);
				}

				private FileVisitResult handleException(IOException e) {
					e.printStackTrace();
					return FileVisitResult.TERMINATE;
				}

				public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
					if (e != null)
						return handleException(e);
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
			return true;
		} catch (IOException e) {
			getLogger().error("Error when the plugin is trying to delete the path " + path, e);
			return false;
		}
	}

}
