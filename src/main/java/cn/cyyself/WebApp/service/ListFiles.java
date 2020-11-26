package cn.cyyself.WebApp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ListFiles {
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
}
