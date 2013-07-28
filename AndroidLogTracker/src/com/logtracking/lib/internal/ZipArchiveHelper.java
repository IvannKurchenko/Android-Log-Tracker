package com.logtracking.lib.internal;

import java.io.*;
import java.util.Arrays;
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
        packFiles(files,zipOutput);
        zipOutput.close();
    }

    private void packFiles(List<File> files, ZipOutputStream zipOutput) throws IOException {
        for (File file : files) {

            if (!file.exists())
                continue;

            if(file.isDirectory()){
                packFiles(Arrays.asList(file.listFiles()),zipOutput);
                continue;
            }

            FileInputStream fileInput = null;
            try {


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
    }
}
