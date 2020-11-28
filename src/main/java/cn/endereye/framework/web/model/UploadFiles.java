package cn.endereye.framework.web.model;

import org.apache.commons.fileupload.FileItem;

import java.util.List;

public class UploadFiles {
    public List<FileItem> FileList;
    public UploadFiles(List<FileItem> FileList) {
        this.FileList = FileList;
    }
}
