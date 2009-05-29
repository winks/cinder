package org.art_core.dev.cinder.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class PropertiesAuditorNature implements IProjectNature {
	private static final String NATURE_ID =
		CinderPlugin.PLUGIN_ID + ".propertiesAuditor";

	private IProject project;
	
	@Override
	public void configure() throws CoreException {
		PropertiesFileAuditor.addBuilderToProject(project);
	      new Job("Properties File Audit") {
	         protected IStatus run(IProgressMonitor monitor) {
	            try {
	               project.build(PropertiesFileAuditor.FULL_BUILD,
	                     PropertiesFileAuditor.BUILDER_ID, null, monitor);
	            }
	            catch (CoreException e) {
	               CinderLog.logError(e);
	            }
	            return Status.OK_STATUS;
	         }
	      }.schedule();
	}

	@Override
	public void deconfigure() throws CoreException {
		PropertiesFileAuditor.removeBuilderFromProject(project);
		PropertiesFileAuditor.deleteAuditMarkers(project);
	}

	@Override
	public IProject getProject() {
		// TODO Auto-generated method stub
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		// TODO Auto-generated method stub
		this.project = project;
	}

	
	// //////////////////////////////////////////////////////////////////////////
	   //
	   // Utility methods
	   //
	   // //////////////////////////////////////////////////////////////////////////
	
	   /**
	* Add the nature to the specified project if it does not already have it.
	* 
	* @param project
	*           the project to be modified
	*/
	   public static void addNature(IProject project) {
	
	      // Cannot modify closed projects.
	  if (!project.isOpen())
	     return;
	
	  // Get the description.
	  IProjectDescription description;
	  try {
	     description = project.getDescription();
	  }
	  catch (CoreException e) {
	     CinderLog.logError(e);
	     return;
	  }
	
	  // Determine if the project already has the nature.
	  List<String> newIds = new ArrayList<String>();
	  newIds.addAll(Arrays.asList(description.getNatureIds()));
	  int index = newIds.indexOf(NATURE_ID);
	  if (index != -1)
	     return;
	
	  // Add the nature
	  newIds.add(NATURE_ID);
	  description.setNatureIds(newIds.toArray(new String[newIds.size()]));
	
	  // Save the description.
	      try {
	         project.setDescription(description, null);
	      }
	      catch (CoreException e) {
	         CinderLog.logError(e);
	      }
	   }
	
	   /**
	* Determine if the specified project has the receiver's nature associated
	* with it.
	* 
	* @param project
	*           the project to be tested
	* @return <code>true</code> if the specified project has the receiver's
	*         nature, else <code>false</code>
	*/
	   public static boolean hasNature(IProject project) {
	      try {
	         return project.isOpen() && project.hasNature(NATURE_ID);
	      }
	      catch (CoreException e) {
	         CinderLog.logError(e);
	         return false;
	      }
	   }
	
	   /**
	* Remove the nature from the specified project if it has the nature
	* associated.
	* 
	* @param project
	*           the project to be modified
	*/
	   public static void removeNature(IProject project) {
	
	      // Cannot modify closed projects.
	  if (!project.isOpen())
	     return;
	
	  // Get the description.
	  IProjectDescription description;
	  try {
	     description = project.getDescription();
	  }
	  catch (CoreException e) {
	     CinderLog.logError(e);
	     return;
	  }
	
	  // Determine if the project has the nature.
	  List<String> newIds = new ArrayList<String>();
	  newIds.addAll(Arrays.asList(description.getNatureIds()));
	  int index = newIds.indexOf(NATURE_ID);
	  if (index == -1)
	     return;
	  
	  // Remove the nature
	  newIds.remove(index);
	  description.setNatureIds(newIds.toArray(new String[newIds.size()]));
	
	  // Save the description.
	      try {
	         project.setDescription(description, null);
	      }
	      catch (CoreException e) {
	         CinderLog.logError(e);
	      }
	   }
}
