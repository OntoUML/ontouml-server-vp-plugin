package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import java.util.Iterator;

public class ApplicationManagerUtils {

  public static ApplicationManager getApplicationManager() {
    return ApplicationManager.instance();
  }

  public static ProjectManager getProjectManager() {
    return getApplicationManager().getProjectManager();
  }

  public static DiagramManager getDiagramManager() {
    return getApplicationManager().getDiagramManager();
  }

  public static IProject getCurrentProject() {
    return getProjectManager().getProject();
  }

  public static IDiagramUIModel getActiveDiagram() {
    return getDiagramManager().getActiveDiagram();
  }

  public static Iterator<?> getAllLevelModelElements() {
    return getCurrentProject().allLevelModelElementIterator();
  }

  public static Iterator<?> getAllLevelModelElements(String[] typesFilter) {
    return getCurrentProject().allLevelModelElementIterator(typesFilter);
  }

  public static Iterator<?> getAllLevelModelElements(String typeFilter) {
    final String[] filter = {typeFilter};
    return getCurrentProject().allLevelModelElementIterator(filter);
  }
}
