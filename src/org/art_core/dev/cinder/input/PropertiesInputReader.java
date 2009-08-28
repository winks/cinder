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

public class PropertiesInputReader implements IInputHandler {
	private String sFilename;
	private final Collection<IItem> items = new ArrayList<IItem>();

	public PropertiesInputReader() {
	}

	@Override
	public boolean isReadable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Reads a properties file from the workspace.
	 * @param String the file name
	 */
	@Override
	public void readFromWorkspaceFile(final String sWorkspaceFile) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final String sPath = root.getLocation().toString();
		String sFilename;
		sFilename = sPath + "/" + sWorkspaceFile;
		CinderLog.logInfo("PIR:RFW:" + sPath + "_" + sWorkspaceFile);

		this.readFromFile(sFilename, false);
	}

	/**
	 * Reads a properties file.
	 * @param String the file name
	 * @param boolean whether the file name is a URI
	 */
	protected void readFromFile(final String sFile, boolean bRemote) {
		this.sFilename = sFile;

		PropertiesItem pItem;
		final Properties prop = new Properties();

		try {
			final FileInputStream stream = new FileInputStream(sFile);
			prop.load(stream);
			stream.close();
			final String snx = prop.getProperty("name");
			final String ssx = prop.getProperty("status");
			final String stx = prop.getProperty("type");
			CinderLog.logInfo("PIR::read:[" + snx + "][" + ssx + "][" + stx
					+ "]");
			// TODO use PropertiesItem(snx, ssx, stx) if t can be be converted
			pItem = new PropertiesItem(snx, ssx);
			CinderLog.logInfo("PIR::read:" + pItem.toString());
			items.add(pItem);
		} catch (Exception e) {
			CinderLog.logError(e);
		}
	}

	/**
	 * Shows a list of the items read from the properties.
	 * @return Collection<IItem> the list
	 */
	@Override
	public Collection<IItem> getItems() {
		return this.items;
	}

	/**
	 * Shows the filename that was last accessed.
	 * @return String the file name or URI
	 */
	@Override
	public String getFilename() {
		return this.sFilename;
	}

	@Override
	public void readFromLocalFile(String sFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromUri(String sFile) {
		// TODO Auto-generated method stub

	}
}