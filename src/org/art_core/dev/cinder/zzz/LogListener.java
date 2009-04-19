package org.art_core.dev.cinder.zzz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

public class LogListener implements ILogListener {

    private File logFile;
   
    public LogListener(){
        String path = System.getProperty("user.dir");
        logFile = new File(path+"/log.log");
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    @Override
    public void logging(IStatus status, String plugin) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(logFile,true));
            StringBuffer str = new StringBuffer(plugin);
            str.append(": ");
            str.append(status.getMessage());
            Exception ex = (Exception) status.getException();
            String stackTrace = ex.toString();
            str.append(stackTrace);
            bos.write(str.toString());
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
