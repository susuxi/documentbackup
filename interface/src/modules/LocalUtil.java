package modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by CJY on 2017/4/15.
 */
public class LocalUtil implements ServerUtil{
    String workDir;
    boolean connect = false;

    public LocalUtil(String remotePath) {
        this.workDir = remotePath;
    }

    @Override
    public boolean connect() throws IOException {
        connect = true;
        return true;
    }

    @Override
    public boolean disconnect() {
        connect = false;
        return true;
    }

    @Override
    public boolean upload(String localPath, String remotePath) throws IOException {
        if(!connect) return false;
        boolean result = true;
        File local = new File(localPath);
        if (!local.exists())    return false;
        if (local.isDirectory()){
            File remotDir = new File(workDir+"/"+remotePath);
            try {
                remotDir.mkdir();
            }catch (SecurityException e){
            }
            File[] filelist = local.listFiles();
            for (File file:filelist){
                String localpath = file.getAbsolutePath();
                String remotepath = remotePath+"/"+file.getName();
                result&=upload(localpath,remotepath);
            }
            return result;
        }else{
            return uploadFile(localPath,remotePath);
        }
    }

    @Override
    public boolean download(String remotePath, String localPath) throws IOException {
        if(!connect) return false;
        boolean success = true;
        File downloadFile = new File(workDir+"/"+remotePath);
        if (downloadFile.isDirectory()){
            File localDir = new File(localPath);
            try {
                localDir.mkdir();
            }catch (SecurityException e){
            }
            File[] downloadList = downloadFile.listFiles();
            for (File f:downloadList){
                String localpath = localPath+"/"+f.getName();
                String remotepath = remotePath+"/"+f.getName();
                success&=download(remotepath,localpath);
            }
            return success;
        }else{
            return downloadFile(remotePath,localPath);
        }
    }


    private boolean uploadFile(String localPath, String remotePath) throws IOException {
        if(!connect) return false;
        remotePath=workDir+'/'+remotePath;
        File source = new File(localPath);
        File target = new File(remotePath);
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            return false;
        } finally {
            inStream.close();
            in.close();
            outStream.close();
            out.close();
        }
        return true;
    }


    private boolean downloadFile(String remotePath, String localPath) throws IOException {
        if(!connect) return false;
        remotePath=workDir+'/'+remotePath;
        File source = new File(remotePath);
        File target = new File(localPath);
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            return false;
        } finally {
            inStream.close();
            in.close();
            outStream.close();
            out.close();
        }
        return true;
    }

    @Override
    public boolean deleteFile(String remotePath) throws IOException {
        if(!connect) return false;
        boolean flag = false;
        if (!remotePath.startsWith(workDir))
            remotePath = workDir+"/"+remotePath;
        File file = new File(remotePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean rename(String remotePath, String newname) throws IOException {
        if(!connect) return false;
        boolean flag = false;
        remotePath=workDir+"/"+remotePath;
        File oldfile = new File(remotePath);
        File newfile = new File(workDir+"/"+newname);
        if (oldfile.exists()){
            oldfile.renameTo(newfile);
            flag = true;
        }else
            flag = false;
        return flag;
    }

    @Override
    public boolean deleteDir(String remotePath) throws IOException {
        if(!connect) return false;
        if (!remotePath.startsWith(workDir))
            remotePath = workDir+"/"+remotePath;
        if (!remotePath.endsWith(File.separator)) {
            remotePath = remotePath + File.separator;
        }
        File dirFile = new File(remotePath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                if (files[i].getName().equals(".DS_Store"))
                    continue;
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDir(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean createDir(String remotePath) throws IOException {
        String pathDir = workDir+"/"+remotePath;
        File dir = new File(pathDir);
        dir.mkdirs();
        return true;
    }

    @Override
    public ArrayList<ServerFile> search(String filename) throws IOException {
        if (!this.connect)
            throw new IOException("尚未建立ftp连接或尚未完成登陆");
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
    public ArrayList<ServerFile> ls(String remotePath) throws IOException {
        if(!connect) throw new IOException("尚未建立连接");
        ArrayList<ServerFile> result = new ArrayList<>();
        String pathDir = workDir+"/"+remotePath;
        File dir = new File(pathDir);
        if (dir.isDirectory()){
            File[] files = dir.listFiles();
            for (File f:files){
                ServerFile temp = new ServerFile();
                temp.fileName = f.getName();
                if (f.isDirectory()) temp.fileType=2;
                else temp.fileType=1;
                temp.fullPath= Paths.get(remotePath,f.getName()).toString();
                result.add(temp);
            }
            return result;
        }
        return null;
    }

    public static void main(String[] args){
        LocalUtil test = new LocalUtil("D:/11111");
        try {
            if (test.connect()) {
                //test.download("b", "/Users/CJY/Desktop/localDir/c");
            	
            	ArrayList<ServerFile> l = test.ls("");
            	for(ServerFile c:l){
            		System.out.println(c);
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            test.disconnect();
        }
    }
}