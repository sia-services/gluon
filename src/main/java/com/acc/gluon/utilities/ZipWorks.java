package com.acc.gluon.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipWorks {

	public static class FileItem {
		public final String filename;
		public final byte[] source;

		public FileItem(String filename, byte[] source) {
			this.filename = filename;
			this.source = source;
		}
	}

	//convert file to zip for save to database
	public static byte[] zip(List<FileItem> source) throws IOException {
		var fout = new ByteArrayOutputStream(1024 * 50);

		try (var zipOut = new ZipOutputStream(fout)) {
			for (var src : source) {
				ZipEntry zipEntry = new ZipEntry(src.filename);
				zipOut.putNextEntry(zipEntry);

				var fis = new ByteArrayInputStream(src.source);
				fis.transferTo(zipOut);
			}
		}

		return fout.toByteArray();
	}
}
