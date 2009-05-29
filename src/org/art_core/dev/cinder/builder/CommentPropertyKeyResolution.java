package org.art_core.dev.cinder.builder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

public class CommentPropertyKeyResolution
      implements IMarkerResolution2
{
   private static final String DESCRIPTION =
         "Comment the existing property key/value pair in the plugin.properties file";

   public String getDescription() {
      return DESCRIPTION;
   }

   public Image getImage() {
      return null;
   }

   public String getLabel() {
      return "Comment the property key";
   }

   public void run(IMarker marker) {
      MessageDialog.openConfirm(null, "Delete property", DESCRIPTION
            + "... not implemented yet");
   }
}
