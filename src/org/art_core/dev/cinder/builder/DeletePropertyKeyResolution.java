package org.art_core.dev.cinder.builder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

public class DeletePropertyKeyResolution
      implements IMarkerResolution2
{
   private static final String DESCRIPTION =
         "Delete the existing property key/value pair from the plugin.properties file";

   public String getDescription() {
      return DESCRIPTION;
   }

   public Image getImage() {
      return null;
   }

   public String getLabel() {
      return "Delete the property key";
   }

   public void run(IMarker marker) {
      MessageDialog.openConfirm(null, "Delete property", DESCRIPTION
            + "... not implemented yet");
   }
}
