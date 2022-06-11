package main.game.MVC;
import main.game.enemy.boss.Boss;
import main.game.enemy.chickenGroups.ChickenGroup;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class FeatureAdder {
    private File file;
//    private URL classPathURL;
//    private String classPath="./out/Production/Project";
    private String fullClassName;
    private static final FeatureAdder featureAdder=new FeatureAdder();
    public static FeatureAdder getInstance(){
        return featureAdder;
    }
//    private void copyFile(File src,File dest){
//        try {
//            FileChannel srcChannel=new FileInputStream(src).getChannel();
//            FileChannel destChannel=new FileOutputStream(dest).getChannel();
//            destChannel.transferFrom(srcChannel,0,srcChannel.size());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public void loadClass(){
        synchronized (this) {
            try {
//                File dir=new File(classPath+"/"+toDirectory(fullClassName));
//                if(!dir.exists())
//                    Files.createDirectory(dir.toPath());
//                        copyFile(file, new File(dir,file.getName()));
                URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
                Class clazz = classLoader.loadClass(fullClassName);
                if (Modifier.isAbstract(clazz.getModifiers()))
                    return;
                if (ChickenGroup.class.isAssignableFrom(clazz))
                    ChickenGroup.addNewChickenGroup(clazz);
                else if (Boss.class.isAssignableFrom(clazz))
                    Boss.addNewBoss(clazz);
                classLoader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void loadClass(File file,String fullClassName){
        setArguments(file,fullClassName);
        loadClass();
    }
    void setArguments(File file,String fullClassName){
        synchronized (this) {
            this.file = file;
            this.fullClassName = fullClassName;
        }
    }
    public File getClassFile(){
        return file;
    }
    public String getFullClassName(){
        return fullClassName;
    }
}
