package modules;
/**
 * Created by CJY on 2017/3/26.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.*;
import org.apache.commons.net.ftp.*;
import org.apache.commons.net.SocketClient;

public interface ServerUtil {

    boolean connect()throws IOException;
    boolean disconnect();
    boolean upload(String localPath, String remotePath)throws IOException;
    boolean download(String remotePath, String localPath)throws IOException;
    boolean deleteFile(String remotePath)throws IOException;
    boolean rename(String remotePath, String newname) throws IOException;

    /**
     * 实现递归删除目录下的所有文件和文件夹（中文处理有问题）
     */
    boolean deleteDir(String remotePath) throws IOException;

    /**
     * 支持创建多级目录
     */
    boolean createDir(String remotePath) throws IOException;
    ArrayList<ServerFile> search(String filename) throws IOException;
    ArrayList<ServerFile> ls(String remotePath) throws IOException;
}