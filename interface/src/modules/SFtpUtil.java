package modules;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by CJY on 2017/4/15.
 */
public class SFtpUtil implements ServerUtil{
    private String ip;
    private String username;
    private String password;
    private int port = 22;
    Session session = null;
    Channel channel = null;
    ChannelSftp chSftp = null;
    private String separator;

    public SFtpUtil(String ip) {
        this(ip,null,null,22);
    }

    public SFtpUtil(String ip, int port) {
        this(ip,null,null,port);
    }

    public SFtpUtil(String ip, String username, String password) {
        this(ip,username,password,22);
    }

    public SFtpUtil(String ip, String username, String password, int port) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.port = port;
        separator = File.separator;
    }

    public boolean connect() {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(username, ip, port);
            if (password != null) {
                session.setPassword(password); // 设置密码
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config); // 为Session对象设置properties
            session.setTimeout(10000); // 设置timeout时间
            session.connect(); // 通过Session建立链接
            channel = session.openChannel("sftp"); // 打开SFTP通道
            channel.connect(); // 建立SFTP通道的连接
            chSftp = (ChannelSftp) channel;
        }catch (JSchException e){
            disconnect();
            return false;
        }
        return true;
    }
    public boolean disconnect(){
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        return true;
    }

    public boolean upload(String localPath, String remotePath) {
        if (remotePath.startsWith("/"))
            remotePath=remotePath.substring(1);
        File file = new File(localPath);
        if (!file.exists()) return false;
        if (file.isDirectory()){
            if(!createDir(remotePath))  return false;
            File[] files = file.listFiles();
            for (File f:files){
                String filename = f.getName();
                if (!filename.startsWith("."))
                    upload(localPath+"/"+filename,remotePath+"/"+filename);
            }
        }else{
            if(!uploadFile(localPath,remotePath))
                return false;
        }
        return true;
    }

    private boolean uploadFile(String localPath, String remotePath){

        try {

//            chSftp.put(localPath, remotePath, ChannelSftp.OVERWRITE);
            chSftp.put(localPath, remotePath);
        } catch (SftpException e) {
            return false;
        }
        return true;
    }

    public boolean download(String remoteFile, String dst){
        String dir = ".";
        if (remoteFile.startsWith("/")){
            remoteFile=remoteFile.substring(1);
        }
        String filename = remoteFile.substring(remoteFile.lastIndexOf('/')+1);
        if (remoteFile.contains("/")){
            dir = remoteFile.substring(0,remoteFile.lastIndexOf('/'));
        }
        boolean success = true;
        try {
            if(isDir(dir,filename)){
                if(!downloadDir(remoteFile, dst))
                    success = false;
            }else{
                if(!downloadFile(remoteFile, dst))
                    success = false;
            }
        } catch (SftpException e) {
            return false;
        }
        return success;
    }

    private boolean downloadFile(String remotePath, String localPath){
        try {
            chSftp.get(remotePath, localPath);
        } catch (SftpException e) {
            return false;
        }
        return  true;
    }

    private boolean downloadDir(String remotePath, String localPath){
//        File dir = new File(localPath);
//        dir.mkdir();
//        String[] chilFile = ls(remotePath);
//        boolean success = true;
//        for (int i=0;i<chilFile.length;i++){
//            chilFile[i]=remotePath+"/"+chilFile[i];
//        }
//        for (String filename:chilFile){
//            if(!download(filename,localPath+"/"+filename.substring(filename.lastIndexOf('/')+1)))
//                success = false;
//        }
//        return success;
        File dir = new File(localPath);
        dir.mkdir();
        ArrayList<ServerFile> chilFiles = ls(remotePath);
        boolean success = true;
        for (ServerFile f:chilFiles){
            f.fileName=remotePath+"/"+f.fileName;
        }
        for (ServerFile f:chilFiles){
            String filename = f.fileName;
            if(!download(filename,localPath+"/"+filename.substring(filename.lastIndexOf('/')+1)))
                success = false;
        }
        return success;
    }

    public boolean deleteFile(String filename){
        if (filename.startsWith("/"))
            filename=filename.substring(1);
        try {
            chSftp.rm(filename);
        } catch (SftpException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean rename(String remotePath, String newname){
        try {
            chSftp.rename(remotePath,newname);
        } catch (SftpException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteDir(String remotePath) {
        if (remotePath.startsWith("/"))
            remotePath=remotePath.substring(1);
//        chSftp.
        try {
            Vector lsMessages = chSftp.ls(remotePath);
            if (lsMessages.size()>2){
                for (Object lsMessage : lsMessages){
                    if (lsMessage.toString().substring(56).equals(".")||lsMessage.toString().substring(56).equals(".."))
                        continue;
                    if(isDir(lsMessage.toString())){
                        if(!deleteDir(remotePath+"/"+getFilename(lsMessage.toString())))
                            return false;
                    }else {
                        if(!deleteFile(remotePath+"/"+getFilename(lsMessage.toString())))
                            return false;
                    }
                }
            }
            chSftp.rmdir(remotePath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    @Override
    public boolean createDir(String remotePath) {
        String workdir="";
        try {
            workdir = chSftp.pwd();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        String[] folders = remotePath.split( "/" );
        for ( String folder : folders ) {
            if ( folder.length() > 0 ) {
                try {
                    chSftp.cd( folder );
                }
                catch ( SftpException e ) {
                    try {
                        chSftp.mkdir( folder );
                        chSftp.cd( folder );
                    } catch (SftpException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        try {
            chSftp.cd(workdir);
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public ArrayList<ServerFile> search(String filename) throws IOException {
        ArrayList<ServerFile> result = new ArrayList<>();
        result.addAll(search(filename,"."));
        return result;
    }

    private ArrayList<ServerFile> search(String filename, String path) throws IOException {
        ArrayList<ServerFile> result = new ArrayList<>();
        ArrayList<ServerFile> l = ls(path);
        for (ServerFile f:l){
            if (f.fileName.contains(filename))
                result.add(f);
            if (f.fileType == 2)
                result.addAll(search(filename,f.fullPath));
        }
        return result;
    }

    @Override
    public ArrayList<ServerFile> ls(String remotePath) {
        if (remotePath.equals("")||remotePath.equals(null))
            remotePath="./";
        ArrayList<ServerFile> result = new ArrayList<>();
        try {
            Vector fn = chSftp.ls(remotePath);
//            String[] filenames = new String[fn.size()-2];
            ArrayList<String> filenames2 = new ArrayList();
            for (int i=0;i<fn.size();i++){
                String filename=getFilename(fn.get(i).toString());
                ServerFile temp = new ServerFile();
                if (!filename.startsWith(".")) {
                    filenames2.add(filename);
                    temp.fileName = filename;
                    if (remotePath.equals("./")){
                        temp.fullPath = filename;
                    }else{
                        temp.fullPath = remotePath+"/"+filename;
                    }
                    if (isDir(fn.get(i).toString())){
                        temp.fileType = 2;
                    }else{
                        temp.fileType = 1;
                    }
                    result.add(temp);
                }
            }
//            String[] filenames = new String[filenames2.size()];
//            int i=0;
//            for(String fs:filenames2){
//                filenames[i++]=fs;
//            }
            return result;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isDir(String ls_message){
        return ls_message.charAt(0) == 'd';
    }

    private boolean isDir(String dir, String checkname) throws SftpException {

        Vector fn = chSftp.ls(dir);
        for (int i=0;i<fn.size();i++){
            String filename=getFilename(fn.get(i).toString());
            if (filename.equals(checkname)){
                if(isDir(fn.get(i).toString()))
                    return true;
                else return false;
            }
        }
        return false;
    }

    private String getFilename(String ls_message){
        return ls_message.substring(56);
    }

    public static void main(String[] args) {
        SFtpUtil test = new SFtpUtil("172.16.84.113","test","anonymous");
        if (!test.connect())
            System.out.print("error");
//        try {
////            System.out.print(Arrays.toString(test.search("D").toArray()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.print(test.upload("/Users/CJY/Desktop/remoteDir","SFTPDIRtest"));
        test.deleteDir("/test");
        test.disconnect();
    }
}