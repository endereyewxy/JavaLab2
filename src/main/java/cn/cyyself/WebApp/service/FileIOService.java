package cn.cyyself.WebApp.service;

import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.IOException;

public interface FileIOService {
    File[] listFile(String path) throws IOException;

    String getPath(String path) throws IOException;

    byte[] getFile(String path) throws IOException;

    boolean mkdir(String path, String dir) throws IOException;

    boolean upload(String path, FileItem upload_file);
}
