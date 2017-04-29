package cn.edu.scau.cmi.oop.modules;
/**
 * Created by CJY on 2017/4/23.
 */
public class ServerFile {
    public String fileName;
    public String fullPath;
    public int fileType;

    public ServerFile(String fileName, String fullPath, int fileType) {
        this.fileName = fileName;
        this.fullPath = fullPath;
        this.fileType = fileType;
    }

    public ServerFile() {
    }

    @Override
    public String toString() {
        return "ServerFile{" +
                "fileName='" + fileName + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", fileType=" + fileType +
                '}';
    }
}
