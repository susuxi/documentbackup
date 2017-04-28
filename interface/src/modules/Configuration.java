package modules;

import java.io.Serializable;

/**
 * Created by CJY on 2017/4/22.
 */
public class Configuration implements Serializable{
    //type的值只能是Ftp，SFtp，Local
    public String type;
    public String username;
    public String password;
    public int port;
    public String ip;
    public boolean connected = false;
    public String backupstrategy = "manual";
    public String savestrategy = "forver";
    public String timinginterval;
    public String savingtime;
    public String watchingpath = System.getProperty("user.dir");//默认监控根目录

    public Configuration(String type, String ip) {
        this(type,ip,null,null,0);
    }

    public Configuration(String type, String ip, String username, String password) {
        this(type,ip,username,password,0);
    }

    public Configuration(String type, String ip, int port) {
        this(type,ip,null,null,port);
    }

    public Configuration(String type, String ip, String username, String password, int port) {
        if (port==0){
            if (type.equals("FTP")){
                port = 20;
            }else if (type.equals("SFTP")){
                port = 22;
            }
        }
        this.type = type;
        this.username = username;
        this.password = password;
        this.port = port;
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "type='" + type + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", ip='" + ip + '\'' +
                ", connected="+connected+
                ", watchingpath="+watchingpath+
                '}';
    }
    @Override
    public boolean equals(Object conf){
    	if(this.toString().equals(conf.toString()))
    		return true;
    	else return false;
    }
}
