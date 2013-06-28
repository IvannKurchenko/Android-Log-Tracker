package com.logtracking.lib.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ZipArchiveHelper {

    private static final String ZIP_FILE_EXTENSION = ".zip";

    private File mArchiveFile;

    public ZipArchiveHelper(String path) {
        mArchiveFile = new File(path + ZIP_FILE_EXTENSION);
    }

    public File getArchiveFile() {
        return mArchiveFile;
    }

    public void packFiles(List<File> files) throws IOException {
        ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(mArchiveFile));
        for (File file : files) {

            FileInputStream fileInput = null;
            try {
                if (!file.exists())
                    continue;

                fileInput = new FileInputStream(file);
                zipOutput.putNextEntry(new ZipEntry(file.getName()));

                byte[] b = new byte[1024];
                int count;

                while ((count = fileInput.read(b)) > 0) {
                    zipOutput.write(b, 0, count);
                }
            } finally {
                if(fileInput!=null)
                    fileInput.close();
            }

        }
        zipOutput.close();
    }

}
