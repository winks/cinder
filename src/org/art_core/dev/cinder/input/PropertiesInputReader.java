package org.art_core.dev.cinder.input;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Properties;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class PropertiesInputReader implements IInputHandler {
	private String sFilename;
	//private final String basename = "cinder.properties";
	private LinkedList items;
	
	public PropertiesInputReader(String file) {
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		IPath ipath = new Path(file);
		CinderLog.logInfo("ipath: "+ipath.toString());
		
		IPath rootpath = root.getLocation().makeAbsolute();
		CinderLog.logInfo("root: "+rootpath.toString());
		
		IFile ifile = root.getFile(rootpath.append(ipath));
		CinderLog.logInfo("ifile: "+ifile.getFullPath().toString());
		
		IResource x = root.findMember(ipath);
		IFile[] fi = root.findFilesForLocation(rootpath.append(ipath));
		IContainer[] fo = root.findContainersForLocation(rootpath.append(ipath));
		IProject[] pro = root.getProjects();
		
		if (fi.length > 0) {
			for (int i = 0; i < fi.length; i++)
				CinderLog.logInfo("fi :"+fi[i].getLocation().toString());
		} else {
			for (int i = 0; i < fo.length; i++)
				CinderLog.logInfo("fo: "+fo[i].getLocation().toString());
		}
		if (pro.length > 0) {
			for (int i = 0; i < pro.length; i++) {
				if (pro[i].isOpen())
					CinderLog.logInfo("pro: "+pro[i].getName());
			}
		}

		
		if (x == null) {
			CinderLog.logInfo("x0");
		} else if (x.exists()) {
			CinderLog.logInfo("x+");
		} else {
			CinderLog.logInfo("x-");
		}
		
		if (ifile.exists()) {
			CinderLog.logInfo("ipath+");
			sFilename	= file;
			this.read(ifile);
		} else {
			CinderLog.logInfo("ipath-");
			items = new LinkedList();
		}
		
		
		
	}

	@Override
	public boolean isReadable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void read(IFile ifile) {
		PropertiesItem pi;
		String sPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		System.out.println(sPath);
		
		Properties prop = new Properties();
		try {
			FileInputStream stream = new FileInputStream(sFilename);
			prop.load(stream);
			stream.close();
			String n = prop.getProperty("name");
			String s = prop.getProperty("status");
			CinderLog.logInfo(n.toString());
			CinderLog.logInfo(s.toString());
			pi = new PropertiesItem(n, s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
