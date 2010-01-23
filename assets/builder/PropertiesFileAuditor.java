package org.art_core.dev.cinder.builder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import org.art_core.dev.cinder.CinderLog;
import org.art_core.dev.cinder.CinderPlugin;

/**
 * The properties auditor is implemented as a builder and cross-references
 * property keys in the plugin.xml with entries in the plugin.properties file.
 * Markers are used to report problems that the auditor finds; keys in the
 * plugin.xml that are not declared in the plugin.properties file are marked as
 * missing, while keys in the plugin.properties file that are not referenced in
 * the plugin.xml file are marked as unused.
 */
public class PropertiesFileAuditor extends IncrementalProjectBuilder {
	public static final int MISSING_KEY_VIOLATION = 1;
	public static final int UNUSED_KEY_VIOLATION = 2;

	/**
	 * The unique identifier for this builder
	 */
	public static final String BUILDER_ID = CinderPlugin.PLUGIN_ID
			+ ".propertiesFileAuditor";

	/**
	 * The unique identifier for the audit marker
	 */
	private static final String MARKER_ID = CinderPlugin.PLUGIN_ID
			+ ".auditmarker";

	// auditMarker attributes
	public static final String KEY = "key";
	public static final String VIOLATION = "violation";

	/**
	 * Simple data structure class containing a key and the location of that key
	 * in a file
	 */
	private class Location {
		IFile file;
		String key;
		int charStart;
		int charEnd;
	}

	/**
	 * When called by Eclipse, this builder should perform an audit as
	 * necessary. If the build kind is <code>INCREMENTAL_BUILD</code> or
	 * <code>AUTO_BUILD</code>, the <code>getDelta</code> method can be used
	 * during the invocation of this method to obtain information about what
	 * changes have occurred since the last invocation of this method. After
	 * completing a build, this builder may return a list of projects for which
	 * it requires a resource delta the next time it is run.
	 * 
	 * @param kind
	 *            the kind of build being requested. Valid values are
	 *            <ul>
	 *            <li><code>FULL_BUILD</code>- indicates a full build.</li>
	 *            <li><code>INCREMENTAL_BUILD</code>- indicates an incremental
	 *            build.</li>
	 *            <li><code>AUTO_BUILD</code>- indicates an automatically
	 *            triggered incremental build (autobuilding on).</li>
	 *            </ul>
	 * @param args
	 *            a table of builder-specific arguments keyed by argument name
	 *            (key type: <code>String</code>, value type:
	 *            <code>String</code> ); <code>null</code> is equivalent to an
	 *            empty map
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return the list of projects for which this builder would like deltas the
	 *         next time it is run or <code>null</code> if none
	 * @exception CoreException
	 *                if this build fails.
	 * @see IProject#build(int, String, Map, IProgressMonitor)
	 */
	@SuppressWarnings("unchecked")
	protected IProject[] build(final int kind, final Map args,
			final IProgressMonitor monitor) throws CoreException {

		if (shouldAudit(kind)) {
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(final IProgressMonitor monitor)
						throws CoreException {
					auditPluginManifest(monitor);
				}
			}, monitor);
		}
		return new IProject[0];
	}

	/**
	 * Clean is an opportunity for a builder to discard any additional state
	 * that has been computed as a result of previous builds. It is recommended
	 * that builders override this method to delete all derived resources
	 * created by previous builds, and to remove all markers of type
	 * <code>IMarker.PROBLEM</code> that were created by previous invocations of
	 * the builder. The platform will take care of discarding the builder's last
	 * built state (there is no need to call <code>forgetLastBuiltState</code>).
	 * 
	 * In our case, we already scrub the project as part of the FULL_BUILD so no
	 * additional work needed.
	 */
	protected void clean(final IProgressMonitor monitor) throws CoreException {
		// no additional work needed here
	}

	/**
	 * Determines whether files should be audited by checking for FULL_BUILD, or
	 * if the plugin.xml or plugin.properties files of the project have changed.
	 * 
	 * @param kind
	 *            the kind of build
	 * @return <code>true</code> if files should be audited, else
	 *         <code>false</code>.
	 */

	private boolean shouldAudit(final int kind) {
		boolean bResult = false;
		if (kind == FULL_BUILD) {
			bResult = true;
		}
		final IResourceDelta delta = getDelta(getProject());
		if (delta == null) {
			bResult = false;
		}
		final IResourceDelta[] children = delta.getAffectedChildren();
		for (int i = 0; i < children.length; i++) {
			final IResourceDelta child = children[i];
			final String sFileName = child.getProjectRelativePath()
					.lastSegment();
			if ("HelloWorldOutput.java".equals(sFileName)
					|| "HelloWorldASD.java".equals(sFileName)) {
				bResult = true;
			}
		}
		return bResult;
	}

	/**
	 * Scan the plugin.xml and plugin.properties files and correlate the
	 * key/value pairs; any keys appearing in plugin.xml should have a
	 * corresponding key/value pair in plugin.properties. Before each lengthy
	 * operation, we check to see if the build has been interrupted or canceled.
	 * After each lengthy operation, we report progress to the user; while this
	 * is not strictly necessary, it is certainly polite. If you do prematurely
	 * exit your build process, you may need to call forgetLastBuildState()
	 * before exiting so that a full rebuild will be performed the next time.
	 * 
	 * @param monitor
	 *            the progress monitor
	 */
	private void auditPluginManifest(final IProgressMonitor monitor) {
		monitor.beginTask("Audit plugin manifest", 4);

		if (!deleteAuditMarkers(getProject())) {
			return;
		}

		if (checkCancel(monitor)) {
			return;
		}
		final Map<String, Location> pluginKeys = scanPlugin(getProject()
				.getFile("HelloWorldOutput.java"));
		monitor.worked(1);

		if (checkCancel(monitor)) {
			return;
		}
		final Map<String, Location> propertyKeys = scanProperties(getProject()
				.getFile("HelloWorldASD.java"));
		monitor.worked(1);

		if (checkCancel(monitor)) {
			return;
		}
		Iterator<Map.Entry<String, Location>> iter = pluginKeys.entrySet()
				.iterator();
		while (iter.hasNext()) {
			final Map.Entry<String, Location> entry = iter.next();
			if (!propertyKeys.containsKey(entry.getKey())) {
				reportProblem("Missing property key", ((Location) entry
						.getValue()), MISSING_KEY_VIOLATION, true);
			}
		}
		monitor.worked(1);

		if (checkCancel(monitor)) {
			return;
		}
		iter = propertyKeys.entrySet().iterator();
		while (iter.hasNext()) {
			final Map.Entry<String, Location> entry = iter.next();
			if (!pluginKeys.containsKey(entry.getKey())) {
				reportProblem("Unused property key", ((Location) entry
						.getValue()), UNUSED_KEY_VIOLATION, false);
			}
		}
		monitor.done();
	}

	/**
	 * Check to see if the build operation in progress was canceled by the user
	 * or should be canceled because another builder needs access to the
	 * workspace.
	 * 
	 * @param monitor
	 *            the progress monitor
	 * @return <code>true</code> if the build operation should stop
	 */
	private boolean checkCancel(final IProgressMonitor monitor) {
		boolean bCheck = false;
		if (monitor.isCanceled()) {
			// Discard build state if necessary.
			throw new OperationCanceledException();
		}

		if (isInterrupted()) {
			// Discard build state if necessary.
			bCheck = true;
		}
		return bCheck;
	}

	/**
	 * Scan the specified plugin.xml file and build a mapping of keys to
	 * location of those keys in the file.
	 * 
	 * @param file
	 *            the plugin.xml file to be scanned
	 * @return a mapping of keys (String) to location (Location)
	 */
	private Map<String, Location> scanPlugin(final IFile file) {
		final Map<String, Location> keys = new HashMap<String, Location>();
		String content = readFile(file);
		int start = 0;
		Location loc;
		while (true) {
			start = content.indexOf("\"%", start);
			if (start < 0) {
				break;
			}
			final int end = content.indexOf('"', start + 2);
			if (end < 0) {
				break;
			}
			loc = new Location();
			loc.file = file;
			loc.key = content.substring(start + 2, end);
			loc.charStart = start + 1;
			loc.charEnd = end;
			keys.put(loc.key, loc);
			start = end + 1;
		}
		return keys;
	}

	/**
	 * Scan the specified plugin.properties file and build a mapping of keys to
	 * location of those keys in the file.
	 * 
	 * @param file
	 *            the plugin.properties file to be scanned
	 * @return a mapping of keys (String) to location (Location)
	 */
	private Map<String, Location> scanProperties(final IFile file) {
		final Map<String, Location> keys = new HashMap<String, Location>();
		final String content = readFile(file);
		int end = 0;
		Location loc;
		String found;
		while (true) {
			end = content.indexOf('=', end);
			if (end < 0) {
				break;
			}
			int start = end - 1;
			char cXX;
			while (start >= 0) {
				cXX = content.charAt(start);
				if (cXX == '\r' || cXX == '\n') {
					break;
				}
				start--;
			}
			start++;
			found = content.substring(start, end).trim();
			if (found.length() == 0 || found.charAt(0) == '#'
					|| found.indexOf('=') != -1) {
				continue;
			}
			loc = new Location();
			loc.file = file;
			loc.key = found;
			loc.charStart = start;
			loc.charEnd = end;
			keys.put(loc.key, loc);
			end++;
		}
		return keys;
	}

	/**
	 * Read the content of the specified file into memory.
	 * 
	 * @param file
	 *            the file to be read
	 * @return the file content as a string
	 */
	private String readFile(final IFile file) {
		String sResult = "";
		InputStream stream = null;
		try {
			if (!file.exists()) {
				throw new FileNotFoundException(file.toString());
			}
			stream = file.getContents();
			final Reader reader = new BufferedReader(new InputStreamReader(
					stream));
			final StringBuffer result = new StringBuffer(2048);
			final char[] buf = new char[2048];
			int iCount;
			while (true) {
				iCount = reader.read(buf);
				if (iCount < 0) {
					break;
				}
				result.append(buf, 0, iCount);
			}
			sResult = result.toString();
			if (stream != null) {
				stream.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			CinderLog.logError(e);
			sResult = "";
		} catch (IOException e) {
			CinderLog.logError(e);
			sResult = "";
		} catch (Exception e) {
			CinderLog.logError(e);
			sResult = "";
		}

		return sResult;
	}

	/**
	 * Report the specified problem to the user.
	 */
	private void reportProblem(final String msg, final Location loc,
			final int violation, final boolean isError) {
		try {
			final IMarker marker = loc.file.createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, msg + ": " + loc.key);
			marker.setAttribute(IMarker.CHAR_START, loc.charStart);
			marker.setAttribute(IMarker.CHAR_END, loc.charEnd);
			marker
					.setAttribute(IMarker.SEVERITY,
							isError ? IMarker.SEVERITY_ERROR
									: IMarker.SEVERITY_WARNING);
			marker.setAttribute(KEY, loc.key);
			marker.setAttribute(VIOLATION, violation);

			CinderLog.logInfo("rP: " + marker.toString());
			CinderLog.logInfo("rP: "
					+ marker.getAttribute(IMarker.SEVERITY, 666));
		} catch (CoreException e) {
			CinderLog.logError(e);
			return;
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Marker methods
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Delete all audit markers in the specified project
	 * 
	 * @param project
	 *            the project to be modified
	 * @return <code>true</code> if successful, else <code>false</code>
	 */
	public static boolean deleteAuditMarkers(final IProject project) {
		boolean bStatus = false;
		try {
			project.deleteMarkers(MARKER_ID, false, IResource.DEPTH_INFINITE);
			bStatus = true;
		} catch (CoreException e) {
			CinderLog.logError(e);
			bStatus = false;
		}
		return bStatus;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Utility methods
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Add this builder to the specified project if possible. Do nothing if the
	 * builder has already been added.
	 * 
	 * @param project
	 *            the project (not <code>null</code>)
	 */
	public static void addBuilderToProject(final IProject project) {

		// Cannot modify closed projects.
		if (!project.isOpen()) {
			return;
		}

		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			CinderLog.logError(e);
			return;
		}

		// Look for builder already associated.
		final ICommand[] cmds = description.getBuildSpec();
		for (int j = 0; j < cmds.length; j++) {
			if (cmds[j].getBuilderName().equals(BUILDER_ID)) {
				return;
			}
		}

		// Associate builder with project.
		final ICommand newCmd = description.newCommand();
		newCmd.setBuilderName(BUILDER_ID);
		final List<ICommand> newCmds = new ArrayList<ICommand>();
		newCmds.addAll(Arrays.asList(cmds));
		newCmds.add(newCmd);
		description.setBuildSpec((ICommand[]) newCmds
				.toArray(new ICommand[newCmds.size()]));
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			CinderLog.logError(e);
		}
	}

	/**
	 * Determine if the specified project has the receiver associated with it.
	 * 
	 * @param project
	 *            the project to test
	 * @return <code>true</code> if the specified project is open and accessible
	 *         and has the builder associated with it, else <code>false</code>
	 */
	public static boolean hasBuilder(final IProject project) {

		// Cannot modify closed projects.
		if (!project.isOpen()) {
			return false;
		}

		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			CinderLog.logError(e);
			return false;
		}

		// Look for builder already associated.
		final ICommand[] cmds = description.getBuildSpec();
		for (int j = 0; j < cmds.length; j++) {
			if (cmds[j].getBuilderName().equals(BUILDER_ID)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove this builder from the specified project if possible. Do nothing if
	 * the builder has already been removed.
	 * 
	 * @param project
	 *            the project (not <code>null</code>)
	 */
	public static void removeBuilderFromProject(final IProject project) {

		// Cannot modify closed projects.
		if (!project.isOpen()) {
			return;
		}

		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			CinderLog.logError(e);
			return;
		}

		// Look for builder.
		int index = -1;
		final ICommand[] cmds = description.getBuildSpec();
		for (int j = 0; j < cmds.length; j++) {
			if (cmds[j].getBuilderName().equals(BUILDER_ID)) {
				index = j;
				break;
			}
		}
		if (index == -1) {
			return;
		}

		// Remove builder from project.
		final List<ICommand> newCmds = new ArrayList<ICommand>();
		newCmds.addAll(Arrays.asList(cmds));
		newCmds.remove(index);
		description.setBuildSpec((ICommand[]) newCmds
				.toArray(new ICommand[newCmds.size()]));
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			CinderLog.logError(e);
		}
	}
}
