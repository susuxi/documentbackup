package modules;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by CJY on 2017/4/22.
 */
public class InfoOperation {

    /**
     * 将序列化文件info.obj保存到参数dirPath值对应的目录下
     *
     * @param dirPath 保存到本地的目录
     * @param info 用Configuration类保存服务器信息的ArrayList
     * @return
     */
    public static boolean dump(String dirPath, ArrayList<Configuration> info){
        String filename = dirPath+"/"+"info.obj";
        File configuration = new File(filename);
        if (!configuration.exists()){
            try {
                configuration.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(info);
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    /**
     * 将序列化文件info.obj中的信息载入并返回一个ArrayList<Configuration>
     *
     * @param dirPath 保存info.obj所在的目录
     * @return
     */
    public static ArrayList<Configuration> load(String dirPath){
        String filename = dirPath+"/"+"info.obj";
        File configuration = new File(filename);
        if (!configuration.exists()) return new ArrayList<Configuration>();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            try {
                ArrayList<Configuration> info = (ArrayList<Configuration>)in.readObject();
                return  info;
            } catch (ClassNotFoundException e) {
                return new ArrayList<Configuration>();
            }
        } catch (IOException e) {
            return new ArrayList<Configuration>();
        }
    }

    public static void main(String[] args){
    	String infopath = System.getProperty("user.dir");
        
        //Configuration test2 = new Configuration("SFtp","172.16.84.113","test","anonymous");
       // ArrayList<Configuration> testList = new ArrayList<>();
       // testList.add(test1);
       // testList.add(test2);

        InfoOperation infoOperator = new InfoOperation();
        //infoOperator.dump(infopath,testList);
        ArrayList<Configuration> result = infoOperator.load(infopath);
        for (Configuration c:result)
            System.out.println(c);
       // System.out.println(result.get(0).equals(result.get(3)));
        Configuration test1 = new Configuration("SFTP","111");
        System.out.println(result.contains(test1));
    }
}

