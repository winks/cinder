package org.art_core.dev.cinder.input;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.model.PropertiesItem;
import org.art_core.dev.cinder.model.IItem;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
/*import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;*/


public class PropertiesInputReader implements IInputHandler {
	private String sFilename;
	private Collection<IItem> items = new ArrayList<IItem>();
	
	public PropertiesInputReader(String file) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String sPath = root.getLocation().toString();
		sFilename = sPath + "/" + file;

		CinderLog.logInfo("PIR:path:" + sPath + "_" + sFilename);
		
		readFile(sFilename);
	}

	@Override
	public boolean isReadable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void readFile(String sFilename) {
		PropertiesItem pi;
		
		Properties prop = new Properties();
		try {
			FileInputStream stream = new FileInputStream(sFilename);
			prop.load(stream);
			stream.close();
			String n = prop.getProperty("name");
			String s = prop.getProperty("status");
			String t = prop.getProperty("type");
			CinderLog.logInfo("PIR::read:["+n.toString() + "][" + s.toString() + "][" + t.toString() + "]");
			// TODO use PropertiesItem(n, s, t) if t can be be converted
			pi = new PropertiesItem(n, s);
			CinderLog.logInfo("PIR::read:"+pi.toString());
			items.add(pi);
		} catch (Exception e) {
			// TODO: handle exception
			CinderLog.logError(e);
		}
	}
	
	@Override
	public Collection<IItem> getItems() {
		return this.items;
	}
}
/*
// only 1 open project supported 
IProject[] pro = root.getProjects();
String projectName = "";

if (pro.length > 0) {
	for (int i = 0; i < pro.length; i++) {
		if (pro[i].isOpen())
			projectName = pro[i].getName();
		
			CinderLog.logInfo("pro: "+projectName);
	}
}*/

//IPath p=new Path(file);

//IFile f=ResourcesPlugin.getWorkspace().getRoot().getFile(p);

//if(f.exists()) read(file);
/*IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
String projectName 	= ""; 

IPath ipath = new Path(file);
CinderLog.logInfo("ipath: "+ipath.toString());
try {
	Thread.sleep(200);
} catch (Exception e) {}

IPath rootpath = root.getLocation().makeAbsolute();
CinderLog.logInfo("root: "+rootpath.toString());
try {
	Thread.sleep(200);
} catch (Exception e) {}

IFile ifile = root.getFile(rootpath.append(ipath));
CinderLog.logInfo("ifile: "+ifile.getFullPath().toString());
try {
	Thread.sleep(200);
} catch (Exception e) {}


IFile[] fi = root.findFilesForLocation(rootpath.append(ipath));
IContainer[] fo = root.findContainersForLocation(rootpath.append(ipath));

if (fi.length > 0) {
	for (int i = 0; i < fi.length; i++)
		CinderLog.logInfo("fi :"+fi[i].getLocation().toString());
} else {
	for (int i = 0; i < fo.length; i++)
		CinderLog.logInfo("fo: "+fo[i].getLocation().toString());
}

try {
	Thread.sleep(200);
} catch (Exception e) {}

IResource x = root.findMember(ipath);
if (x == null) {
	CinderLog.logInfo("x0");
} else if (x.exists()) {
	CinderLog.logInfo("x+");
} else {
	CinderLog.logInfo("x-");
}

try {
	Thread.sleep(200);
} catch (Exception e) {}

if (ifile.exists()) {
	CinderLog.logInfo("ipath+");
	sFilename	= file;
	this.read(ifile);
} else {
	CinderLog.logInfo("ipath-");
	items = new LinkedList();
}

try {
	Thread.sleep(200);
} catch (Exception e) {}

// only 1 open project supported
IProject[] pro = root.getProjects();
if (pro.length > 0) {
	for (int i = 0; i < pro.length; i++) {
		if (pro[i].isOpen())
			projectName = pro[i].getName();
		
			CinderLog.logInfo("pro: "+projectName);
	}
}*/
