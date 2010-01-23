package org.art_core.dev.cinder.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * Provides possible resolution for the user for any
 * com.qualityeclipse.favorites.auditmarker marker.
 */
public class ViolationResolutionGenerator implements
		IMarkerResolutionGenerator2 {
	/**
	 * Returns whether there are any resolutions for the given marker.
	 * 
	 * @return <code>true</code> if there are resolutions for the given marker,
	 *         <code>false</code> if not
	 */
	public boolean hasResolutions(final IMarker marker) {
		boolean bStatus;
		switch (getViolation(marker)) {
		case PropertiesFileAuditor.MISSING_KEY_VIOLATION:
			bStatus = true;
			break;
		case PropertiesFileAuditor.UNUSED_KEY_VIOLATION:
			bStatus = true;
			break;
		default:
			bStatus = false;
		}
		return bStatus;
	}

	/**
	 * Returns resolutions for the given marker (may be empty).
	 * 
	 * @return resolutions for the given marker
	 */
	public IMarkerResolution[] getResolutions(final IMarker marker) {
		final List<IMarkerResolution2> resolutions = new ArrayList<IMarkerResolution2>();
		switch (getViolation(marker)) {
		case PropertiesFileAuditor.MISSING_KEY_VIOLATION:
			resolutions.add(new CreatePropertyKeyResolution());
			break;
		case PropertiesFileAuditor.UNUSED_KEY_VIOLATION:
			resolutions.add(new DeletePropertyKeyResolution());
			resolutions.add(new CommentPropertyKeyResolution());
			break;
		default:
			break;
		}
		return (IMarkerResolution[]) resolutions
				.toArray(new IMarkerResolution[resolutions.size()]);
	}

	/**
	 * Answer the violation attribute for the specified marker
	 * 
	 * @param marker
	 *            the marker to test
	 * @return the violation attribute or <code>0</code> if none
	 */
	private int getViolation(final IMarker marker) {
		return marker.getAttribute(PropertiesFileAuditor.VIOLATION, 0);
	}
}
