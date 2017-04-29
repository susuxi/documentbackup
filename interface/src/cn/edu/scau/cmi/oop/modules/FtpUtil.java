package cn.edu.scau.cmi.oop.modules;

import org.apache.commons.net.ftp.*;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by CJY on 2017/4/15.
 */
public class FtpUtil implements ServerUtil {
        FTPClient ftp;
        private String ip;
        private String user;
        private String password;
        private int port;

        public FtpUtil(String ip) {
            this(ip, null, null, 21);
        }

        public FtpUtil(String ip, int port) {
            this(ip, null, null, port);
        }

        public FtpUtil(String ip, String user, String password) {
            this(ip, user, password, 21);
        }

        public FtpUtil(String ip, String user, String password, int port) {
            this.ip = ip;
            this.user = user;
            this.password = password;
            this.port = port;
        }

        public boolean connect() throws IOException {
            if (port == 0)
                port = 21;
            ftp = new FTPClient();
            ftp.connect(ip, port);
            ftp.setControlEncoding("UTF-8");
            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
            conf.setServerLanguageCode("zh");
            ftp.login(user, password);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }
            return true;
        }

        public boolean disconnect() {
            if (ftp.isAvailable()) {
                try {
                    ftp.logout();
                } catch (IOException e) {
                    return false;
                }
            }
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    return false;
                }
            }
            return true;
        }

    @Override
    public boolean upload(String localPath, String remotePath) throws IOException {
        if (!(ftp.isAvailable() && ftp.isConnected())) {
            throw new IOException("尚未建立ftp连接或尚未完成登陆");
        }
        boolean success = true;
        File localFile = new File(localPath);
        if (localFile.isDirectory()){
            createDir(remotePath);
            File[] files = localFile.listFiles();
            for (File file:files){
                if (!file.getName().startsWith(".")) {
                    String localpath = file.getAbsolutePath();
                    String remotepath;
                    if (!remotePath.endsWith("/"))
                        remotepath = remotePath + file.getName();
                    else
                        remotepath = remotePath + "/" + file.getName();
                    success&=upload(localpath, remotepath);
                }
            }
            return success;
        }else{
            return uploadFile(localPath,remotePath);
        }
    }

    @Override
    public boolean download(String remotePath, String localPath) throws IOException {
        if (!(ftp.isAvailable() && ftp.isConnected())) {
            throw new IOException("尚未建立ftp连接或尚未完成登陆");
        }
        boolean success = true;
        if (!remotePath.startsWith("/")) remotePath="/"+remotePath;
        String dirPath = remotePath.substring(0,remotePath.lastIndexOf('/'));
        String filename = remotePath.substring(remotePath.lastIndexOf('/')+1);
        ArrayList<ServerFile> files = ls(dirPath);
        boolean isDir=false;
        for (ServerFile file:files){
            if (file.fileName.equals(filename)){
                if (file.fileType==2)
                    isDir=true;
                else    isDir=false;
            }
        }
        //get type
        if (isDir) {
            File localDir = new File(localPath);
            try {
                localDir.mkdir();
            } catch (SecurityException e) {
            }
            FTPFile[] fileslist = ftp.listFiles(remotePath);
            for (FTPFile file : fileslist) {
                if (file.getName().startsWith(".")) continue;
                String remotepath,localpath;
                if (remotePath.endsWith("/")) {
                    remotepath = remotePath + file.getName();
                }else {
                    remotepath = remotePath + "/" + file.getName();
                }
                if (localPath.endsWith("/")){
                    localpath = localPath + file.getName();
                }else {
                    localpath = localPath + "/" + file.getName();
                }
                success &= download(remotepath, localpath);
            }
            return success;

        }else{
            return downloadFile(remotePath,localPath);
        }
    }

    private boolean uploadFile(String localPath, String remotePath) throws IOException {
            if (!(ftp.isAvailable() && ftp.isConnected())) {
                throw new IOException("尚未建立ftp连接或尚未完成登陆");
            }
            String wordDir = ftp.printWorkingDirectory();
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            String remoteFileName = remotePath;

            if (remotePath.contains("/")) {
                remoteFileName = remotePath.substring(remotePath.lastIndexOf("/") + 1);
                String directory = remotePath.substring(0, remotePath.lastIndexOf("/") + 1);

                if (!directory.equalsIgnoreCase("/") && !ftp.changeWorkingDirectory(directory)) {

                    // 如果远程目录不存在，则递归创建远程服务器目录
                    int start = 0, end = 0;

                    if (directory.startsWith("/")) {
                        start = 1;
                    } else {
                        start = 0;
                    }

                    end = directory.indexOf("/", start);

                    while (true) {

                        String subDirectory = remotePath.substring(start, end);

                        if (!ftp.changeWorkingDirectory(subDirectory)) {

                            if (ftp.makeDirectory(subDirectory)) {

                                ftp.changeWorkingDirectory(subDirectory);

                            } else {
                                throw new IOException("上传文件失败");
                            }
                        }
                        start = end + 1;
                        end = directory.indexOf("/", start);
                        // 检查所有目录是否创建完毕
                        if (end <= start) {
                            break;
                        }
                    }
                }
            }
            /*****执行文件上传******/
            InputStream input = null;
            boolean success = false;
            try {
                File f = new File(localPath);
                // 得到目录的相应文件列表

//            System.out.print("上传文件个数:" + fs.length + "  ,文件名称:" + localPath);

                input = new FileInputStream(f);
                // 保存文件remoteFileName
                success = ftp.storeFile(remoteFileName, input);
                input.close();
            } catch (IOException e) {
//            System.out.print("上传文件失败:" + e.getMessage());
                if (input != null) {
                    ftp.changeWorkingDirectory(wordDir);
                    input.close();
                }
                return false;
            } finally {
//            System.out.print("保存标识>>>" + success + "文件名称:" + localPath + (success ? "上传成功!" : "上传失败!"));
            }
            ftp.changeWorkingDirectory(wordDir);
            return true;
        }

    public boolean deleteFile(String remotePath) throws IOException {
        if (!(ftp.isAvailable() && ftp.isConnected())) {
                throw new IOException("尚未建立ftp连接或尚未完成登陆");
        }
        String workDir = ftp.printWorkingDirectory();
        ftp.changeWorkingDirectory(remotePath);
        FTPFile[] fs = ftp.listFiles(); // 得到目录的相应文件列表
        if (fs.length > 0) {
            // codeclocalAdr->localAdr
            if (ftp.deleteFile(remotePath)){
                ftp.changeWorkingDirectory(workDir);
                return true;
            } else {
                ftp.changeWorkingDirectory(workDir);
                return false;
            }
        }
        ftp.changeWorkingDirectory(workDir);
        return true;
    }

    private boolean downloadFile(String remotePath, String localPath) throws IOException {
            if (!(ftp.isAvailable() && ftp.isConnected())) {
                throw new IOException("尚未建立ftp连接或尚未完成登陆");
            }

            File f = new File(localPath);
            OutputStream os = new FileOutputStream(f);
            ftp.retrieveFile(remotePath, os);
            os.flush();
            os.close();
            if (ftp.getReplyCode() == 226) {
                return true;
            }
            return false;
        }

    public boolean rename(String remotePath, String newname) throws IOException {
            if (!(ftp.isAvailable() && ftp.isConnected())) {
                throw new IOException("尚未建立ftp连接或尚未完成登陆");
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            return ftp.rename(remotePath, newname);
        }

    @Override
    public boolean deleteDir(String remotePath) throws IOException {
            if (!(ftp.isAvailable() && ftp.isConnected())) {
                throw new IOException("尚未建立ftp连接或尚未完成登陆");
            }
            FTPFile[] fs = ftp.listFiles(remotePath);
            if (!remotePath.endsWith("/"))
                remotePath+="/";
            if (fs.length > 0) {
                for (FTPFile file : fs) {
                    if (file.isDirectory()) {
                        if (file.getName().equals(".")||file.getName().equals(".."))
                            continue;
                        if (!deleteDir(remotePath  + file.getName()))
                            return false;
                    } else {
                        if (!ftp.deleteFile(remotePath  + file.getName()))
                            return false;
                    }
                }
            }
            if (ftp.removeDirectory(remotePath)) {
                return true;
            }
            return false;
        }

    @Override
    public boolean createDir(String remotePath) throws IOException {
        if (!(ftp.isAvailable() && ftp.isConnected())) {
            throw new IOException("尚未建立ftp连接或尚未完成登陆");
        }
        boolean success=true;
        String wordDir = ftp.printWorkingDirectory();
        String[] paths = remotePath.split("/");
        String path="";
        for (int i=0;i<paths.length;i++){
            if (!path.endsWith("/"))
                path+="/";
            path+=paths[i];
//               System.out.println(path);
            boolean result =ftp.makeDirectory(path);
            ftp.changeWorkingDirectory(wordDir);
            if (i==paths.length-1)
                return result;
        }
        ftp.changeWorkingDirectory(wordDir);
        return false;
    }

    @Override
    public ArrayList<ServerFile> search(String filename) throws IOException {
        if (!(ftp.isAvailable() && ftp.isConnected())) {
            throw new IOException("尚未建立ftp连接或尚未完成登陆");
        }
        ArrayList<ServerFile> result = new ArrayList<>();
        result.addAll(search(filename,"."));
        return result;
    }

    private ArrayList<ServerFile> search(String filename, String path) throws IOException {
        ArrayList<ServerFile> result = new ArrayList<>();
        ArrayList<ServerFile> l = ls(path);
        if (l==null)    return null;
        for (ServerFile f:l){
            ArrayList<ServerFile> temp = new ArrayList<>();
            if (f.fileName.contains(filename))
                result.add(f);
            if (f.fileType == 2)
                temp=search(filename,f.fullPath);
                if (temp!=null)
                    result.addAll(temp);
        }
        return result;
    }

    @Override
    public ArrayList<ServerFile> ls(String remotePath) throws IOException {
            ArrayList<ServerFile> result = new ArrayList<>();
            if (!(ftp.isAvailable() && ftp.isConnected())) {
                throw new IOException("尚未建立ftp连接或尚未完成登陆");
            }
            String workingDir = ftp.printWorkingDirectory();
            if (!(remotePath.equals(".") || (remotePath.equals(".."))))
                ftp.changeWorkingDirectory(remotePath);
            try {
                FTPFile[] fs = ftp.listFiles();
                int n = fs.length;
                if (n > 0) {
//                    String[] filenames = new String[n];
                    for (int i = 0; i < n; i++) {
                        ServerFile temp = new ServerFile();
                        if (fs[i].getName().equals(".")||fs[i].getName().equals(".."))
                            continue;
                        temp.fileName = fs[i].getName();
                        temp.fullPath = remotePath+"/"+temp.fileName;
                        if (fs[i].isDirectory()) temp.fileType=2;
                        else if(fs[i].isFile()) temp.fileType=1;
                        else temp.fileType=0;
                        result.add(temp);
                    }
                    ftp.changeWorkingDirectory(workingDir);
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ftp.changeWorkingDirectory(workingDir);
            return null;
        }

    public static void main(String[] args){
        FtpUtil test = new FtpUtil("192.168.33.123","201525050401","201525050401",8021);
        try {
            System.out.println(test.connect());
//            test.deleteDir("/Users");
//              ArrayList<ServerFile> l=test.search("a");
//                for (ServerFile f:l){
//                    System.out.println(f);
//                }
//            System.out.println(test.upload("/Users/CJY/Desktop/localDir/c/j.jpg","asdj.txt"));
//            System.out.print(test.createDir("test_Dir/aaas"));
//            test.download("test_Dir","/Users/CJY/Desktop/tt");

        } catch (IOException e) {
            System.out.print("error");
        }finally {
            test.disconnect();
        }
    }
}