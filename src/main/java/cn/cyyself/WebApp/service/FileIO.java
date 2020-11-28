package cn.cyyself.WebApp.service;

import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileIO {
    private static final String root_path = new File(System.getProperty("javalab2.root_path")).getAbsolutePath();
    public static File[] listFile(String path) throws IOException {
        File dir = new File(root_path,path);
        if (dir.getCanonicalPath().startsWith(root_path) && dir.isDirectory()) {//avoid path traversal hack
            return dir.listFiles();
        }
        else return null;
    }
    public static String getPath(String path) throws IOException{
        File dir = new File(root_path,path);
        if (dir.getCanonicalPath().startsWith(root_path)) {
            if (dir.isDirectory()) {
                String res = dir.getAbsolutePath().substring(root_path.length());
                return res.endsWith("/")?res:res+"/";
            }
            else if (dir.isFile()) {
                return dir.getAbsolutePath().substring(root_path.length());
            }
            else return "/";
        }
        else return "/";
    }
    public static byte[] getFile(String path) throws IOException{
        File file = new File(root_path,path);
        if (file.getCanonicalPath().startsWith(root_path) && file.isFile()) {
                FileInputStream readdata = new FileInputStream(file);
                return readdata.readAllBytes();
        }
        else return null;
    }
    public static boolean mkdir(String path,String dir) throws IOException {
        if (dir.contains("/") || dir.contains("\\")) return false;
        else {
            File file = new File(root_path+path,dir);
            return file.mkdir();
        }
    }
    public static boolean upload(String path, FileItem upload_file) {
        if (upload_file.getName().contains("/") || upload_file.getName().contains("\\")) return false;
        else {
            File file = new File(root_path+path,upload_file.getName());
            try {
                upload_file.write(file);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
