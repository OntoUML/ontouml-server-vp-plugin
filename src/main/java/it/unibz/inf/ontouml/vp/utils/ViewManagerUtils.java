package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.listeners.IssueLogMenuListener;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.GitHubRelease;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.model.ServiceIssue;
import it.unibz.inf.ontouml.vp.model.VerificationServiceResult;
import it.unibz.inf.ontouml.vp.views.HTMLEnabledMessage;
import java.io.File;
import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * Class responsible for facilitating display of messages on Visual Paradigm's log.
 *
 * @author Claudenir Fonseca
 * @author Victor Viola
 */
public class ViewManagerUtils {

  public static final String SCOPE_PLUGIN = "OntoUML";

  public static final String SIMPLE_LOGO = "simple_logo";
  public static final String SIMPLE_LOGO_FILENAME = "ontouml-icon-43x55.png";
  public static final String NAVIGATION_LOGO = "navigation";
  public static final String NAVIGATION_LOGO_FILENAME = "navigation-black-18x18.png";
  public static final String MORE_HORIZ_LOGO = "more_horiz";
  public static final String MORE_HORIZ_LOGO_FILENAME = "more_horiz-black-18x18.png";
  public static final String PACKAGE_LOGO = "package";
  public static final String PACKAGE_LOGO_FILENAME = "package-black-18x18.png";
  public static final String CLASS_LOGO = "class";
  public static final String CLASS_LOGO_FILENAME = "class-black-20x15.png";
  public static final String ASSOCIATION_LOGO = "association";
  public static final String ASSOCIATION_LOGO_FILENAME = "association-black-20x15.png";
  public static final String GENERALIZATION_LOGO = "generalization";
  public static final String GENERALIZATION_LOGO_FILENAME = "generalization-black-18x18.png";
  public static final String GENERALIZATION_SET_LOGO = "generalizationSet";
  public static final String GENERALIZATION_SET_LOGO_FILENAME = "generalizationset-black-18x18.png";
  public static final String DIAGRAM_LOGO = "diagram";
  public static final String DIAGRAM_LOGO_FILENAME = "diagram-black-18x18.png";
  public static final String DATATYPE_LOGO = "datatype";
  public static final String DATATYPE_LOGO_FILENAME = "datatype-black-18x18.png";
  public static final String ATTRIBUTE_LOGO = "attribute";
  public static final String ATTRIBUTE_LOGO_FILENAME = "attribute-black-18x18.png";

  public static void simpleDialog(String message) {
    ApplicationManager.instance()
        .getViewManager()
        .showConfirmDialog(
            null,
            message,
            OntoUMLPlugin.PLUGIN_NAME,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            new ImageIcon(getFilePath(SIMPLE_LOGO)));
  }

  public static boolean warningDialog(String message) {
    final ViewManager vm = ApplicationManager.instance().getViewManager();
    int selectedOption =
        vm.showConfirmDialog(
            vm.getRootFrame(),
            message,
            SCOPE_PLUGIN,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            new ImageIcon((getFilePath(SIMPLE_LOGO))));
    return JOptionPane.YES_OPTION == selectedOption;
  }

  public static void cleanAndShowMessage(String message) {
    ApplicationManager.instance()
        .getViewManager()
        .removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);

    ArrayList<String> messageList = new ArrayList<>();
    messageList.add(timestamp() + message);
    JList<Object> list = new JList<>(messageList.toArray());
    log(list);
  }

  public static void clean() {
    ApplicationManager.instance()
        .getViewManager()
        .removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);
  }

  private static String timestamp() {
    return "[" + (new Timestamp(System.currentTimeMillis())) + "] ";
  }

  public static String getFilePath(String imageName) {

    final File pluginDir =
        ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID).getPluginDir();

    switch (imageName) {
      case SIMPLE_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "ontouml", SIMPLE_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case NAVIGATION_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", NAVIGATION_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case MORE_HORIZ_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", MORE_HORIZ_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case PACKAGE_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", PACKAGE_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case CLASS_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", CLASS_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case ASSOCIATION_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", ASSOCIATION_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case GENERALIZATION_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", GENERALIZATION_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case GENERALIZATION_SET_LOGO:
        return Paths.get(
                pluginDir.getAbsolutePath(), "icons", "misc", GENERALIZATION_SET_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case DIAGRAM_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", DIAGRAM_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case DATATYPE_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", DATATYPE_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      case ATTRIBUTE_LOGO:
        return Paths.get(pluginDir.getAbsolutePath(), "icons", "misc", ATTRIBUTE_LOGO_FILENAME)
            .toFile()
            .getAbsolutePath();
      default:
        return null;
    }
  }

  public static void log(VerificationServiceResult result) {
    final List<ServiceIssue> verificationIssues =
        result != null ? result.getResult() : new ArrayList<>();
    final List<String> issuesList = new ArrayList<>();
    final List<String> ontoumlElementIdList = new ArrayList<>();
    final int errorCount = verificationIssues.size();

    if (errorCount == 0) {
      issuesList.add("No syntactical issues were found.");
    }

    for (ServiceIssue issue : verificationIssues) {
      final String message =
          timestamp()
              + issue.getSeverity().toUpperCase()
              + ": "
              + issue.getTitle()
              + " "
              + issue.getDescription();
      issuesList.add(message);
      ontoumlElementIdList.add(issue.getSource().getId());
    }

    JList<Object> list = new JList<>(issuesList.toArray());
    IssueLogMenuListener listener = new IssueLogMenuListener(ontoumlElementIdList, list);
    list.addMouseListener(listener);
    list.addMouseMotionListener(listener);

    log(list);
  }

  public static void log(JList<?> list) {
    JScrollPane parentContainer = new JScrollPane(list);
    ApplicationManager.instance()
        .getViewManager()
        .showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID, SCOPE_PLUGIN, parentContainer);
  }

  public static void log(List<?> list) {
    JList<?> jList = new JList<>(list.toArray());
    log(jList);
  }

  public static void log(String message) {
    log(List.of(message));
  }

  public static void exportToGUFOIssueDialog(String msg) {
    ApplicationManager.instance()
        .getViewManager()
        .showConfirmDialog(
            null,
            msg,
            "Export to gUFO",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE,
            new ImageIcon(getFilePath(SIMPLE_LOGO)));
  }

  public static boolean exportToGUFOIssueDialogWithOption(String msg, int httpCode) {
    final ProjectConfigurations configurations =
        Configurations.getInstance().getProjectConfigurations();

    if (configurations.isCustomServerEnabled()
        && (httpCode == HttpURLConnection.HTTP_NOT_FOUND
            || httpCode == HttpURLConnection.HTTP_INTERNAL_ERROR)) {

      int option =
          ApplicationManager.instance()
              .getViewManager()
              .showConfirmDialog(
                  null,
                  msg + "\nDo you want to retry using the default server?",
                  "Export to gUFO",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.INFORMATION_MESSAGE,
                  new ImageIcon(getFilePath(SIMPLE_LOGO)));

      if (option == JOptionPane.OK_OPTION) {
        configurations.setCustomServerEnabled(false);
        return true;
      } else {
        return false;
      }

    } else {
      exportToGUFOIssueDialog(msg);
      return false;
    }
  }

  public static boolean isElementInAnyDiagram(String elementId) {
    final IProject project = ApplicationManager.instance().getProjectManager().getProject();
    final IModelElement element = project.getModelElementById(elementId);
    final IDiagramElement[] diagramElements = element != null ? element.getDiagramElements() : null;

    return diagramElements != null && diagramElements.length > 0;
  }

  public static void openSpecDiagramElement(String elementId) {
    final ApplicationManager app = ApplicationManager.instance();
    final IModelElement element =
        app.getProjectManager().getProject().getModelElementById(elementId);
    final ViewManager viewManager = app.getViewManager();

    viewManager.openSpec(element, viewManager.getRootFrame());
  }

  public static void highlightDiagramElement(String modelElementId) {
    final ApplicationManager app = ApplicationManager.instance();
    final IProject project = app.getProjectManager().getProject();
    final IModelElement modelElement = project.getModelElementById(modelElementId);

    if (modelElement == null) {
      return;
    }

    final DiagramManager diagramManager = app.getDiagramManager();
    final IDiagramElement[] diagramElements = modelElement.getDiagramElements();
    IDiagramElement activeView = null;
    IDiagramElement masterView = null;
    IDiagramElement firstView = null;

    for (int i = 0; diagramElements != null && i < diagramElements.length; i++) {
      IDiagramElement diagramElement = diagramElements[i];

      if (diagramElement == null) {
        continue;
      }

      firstView = firstView == null ? diagramElement : firstView;
      activeView = diagramElement.getDiagramUIModel().isOpened() ? diagramElement : null;
      masterView = diagramElement.isMasterView() ? diagramElement : masterView;

      if (activeView != null) {
        break;
      }
    }

    if (activeView != null) {
      diagramManager.highlight(activeView);
    } else if (masterView != null) {
      diagramManager.highlight(masterView);
    } else if (firstView != null) {
      diagramManager.highlight(firstView);
    }
  }

  public static void reportBugErrorDialog(boolean isOperationNotAllowed) {
    final ViewManager vm = ApplicationManager.instance().getViewManager();
    final String body =
        isOperationNotAllowed
            ? "Unable to open the browser. Please visit <a href=\""
                + OntoUMLPlugin.PLUGIN_REPO
                + "\">"
                + OntoUMLPlugin.PLUGIN_REPO
                + "</a> to submit bugs."
            : "Something went wrong. Please visit <a href=\""
                + OntoUMLPlugin.PLUGIN_REPO
                + "\">"
                + OntoUMLPlugin.PLUGIN_REPO
                + "</a> to submit bugs.";

    vm.showConfirmDialog(
        null,
        new HTMLEnabledMessage(body),
        "Report Error",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.ERROR_MESSAGE,
        new ImageIcon(getFilePath(SIMPLE_LOGO)));
  }

  public static GitHubRelease updateDialog() {
    final Configurations config = Configurations.getInstance();
    final GitHubRelease lastestRelease = config.getLatestRelease();

    final boolean isUpToDate =
        lastestRelease.getTagName().equals(OntoUMLPlugin.PLUGIN_VERSION_RELEASE);
    String[] options;
    String initialSelection;
    StringBuilder msg = new StringBuilder();

    if (isUpToDate) {
      options = new String[2];
      options[0] = "Cancel";
      options[1] = "Select release";

      initialSelection = options[1];

      msg.append("Your plugin is up to date with the latest stable release.");
      msg.append(
          "\n"
              + "If you desire to install a different version of the plugin, click on \"Select"
              + " release\".");
      msg.append(
          "\n"
              + "Be aware that \"alpha\" releases provide experimental new features that may not"
              + " be fully integrated to the plugin.");
    } else {
      options = new String[3];
      options[0] = "Cancel";
      options[1] = "Select release";
      options[2] = "Install latest release";

      initialSelection = options[2];

      msg.append("The latest stable release of the plugin is the version ")
          .append(lastestRelease.getTagName())
          .append(".");
      msg.append(
          "\n"
              + "To install this update, click on  \"Install latest release\", or click on"
              + " \"Select a release\" to install a different version.");
      msg.append(
          "\n"
              + "Be aware that \"alpha\" releases are provide experimental new features that may"
              + " present some issues.");
    }
    msg.append("\n\nThe procedure may take a couple of seconds.");

    final ViewManager vm = ApplicationManager.instance().getViewManager();
    int selectedOption =
        vm.showOptionDialog(
            vm.getRootFrame(),
            msg.toString(),
            "Plugin Update",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            new ImageIcon(getFilePath(SIMPLE_LOGO)),
            options,
            initialSelection);

    switch (selectedOption) {
      case 2:
        return lastestRelease;
      case 1:
        return selectReleaseToInstall();
      default:
        return null;
    }
  }

  public static void updateSuccessDialog() {
    final ViewManager vm = ApplicationManager.instance().getViewManager();
    final String msg =
        "Plugin successfully updated.\n"
            + "Please restart the application for the changes to take effect.";

    vm.showConfirmDialog(
        null,
        msg,
        "Plugin Update",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        new ImageIcon(getFilePath(SIMPLE_LOGO)));
  }

  public static void updateErrorDialog() {
    final ViewManager vm = ApplicationManager.instance().getViewManager();

    String message =
        "Something went wrong during the update. Please verify your connection.<br>"
            + "In case your plugin becomes unavailable, you may find instructions at <a href=\""
            + OntoUMLPlugin.PLUGIN_REPO
            + "\">"
            + OntoUMLPlugin.PLUGIN_REPO
            + "</a>.<br>"
            + "In this page you can also report this error and help us to improve our plugin.";
    vm.showConfirmDialog(
        null,
        new HTMLEnabledMessage(message),
        "Plugin Update Error",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.ERROR_MESSAGE,
        new ImageIcon(getFilePath(SIMPLE_LOGO)));
  }

  private static GitHubRelease selectReleaseToInstall() {
    final Map<String, GitHubRelease> map = new HashMap<>();
    final Configurations config = Configurations.getInstance();
    final GitHubRelease installedRelease = config.getInstalledRelease();
    final String installedReleaseTagName =
        installedRelease != null ? installedRelease.getTagName() : null;

    config
        .getReleases()
        .forEach(
            release -> {
              String releaseTagName = release.getTagName();

              releaseTagName =
                  releaseTagName.equals(installedReleaseTagName)
                      ? releaseTagName + " (installed version)"
                      : releaseTagName;

              map.put(releaseTagName, release);
            });

    ViewManager vm = ApplicationManager.instance().getViewManager();
    List<String> keys = new ArrayList<>(map.keySet());
    keys.sort(Comparator.reverseOrder());
    Object[] keysArray = new String[keys.size()];
    keys.toArray(keysArray);
    Object selectedValue =
        vm.showInputDialog(
            vm.getRootFrame(),
            "Select the desired version of the OntoUML Plugin:",
            "Plugin Versions",
            JOptionPane.QUESTION_MESSAGE,
            new ImageIcon(getFilePath(SIMPLE_LOGO)),
            keysArray,
            keysArray[0]);

    return map.get(selectedValue);
  }

  public static boolean showInvertAssociationWarningDialog() {
    final Configurations config = Configurations.getInstance();
    final ProjectConfigurations projectConfig = config.getProjectConfigurations();

    if (projectConfig.ignoreAssociationInversionWarning()) {
      return true;
    }

    final ViewManager vm = ApplicationManager.instance().getViewManager();
    final JPanel _pMessagePane = new JPanel();
    final JLabel _lLineOne =
        new JLabel(
            "<html>This action will invert the direction of some<br>"
                + "of the selected associations in all diagrams<br>"
                + "they appear. We advise checking whether their<br>"
                + "names remain consistent.<br><br>"
                + "Do you wish to continue?</html>");
    final JCheckBox _chkHideWarnings = new JCheckBox("Do not show this dialog again");

    _pMessagePane.setLayout(new BoxLayout(_pMessagePane, BoxLayout.Y_AXIS));
    _pMessagePane.add(_lLineOne);
    _pMessagePane.add(_chkHideWarnings);
    _chkHideWarnings.setBorder(new EmptyBorder(15, 0, 0, 0));

    int selectedOption =
        vm.showConfirmDialog(
            vm.getRootFrame(),
            _pMessagePane,
            "Association Inversion Warning",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            new ImageIcon(getFilePath(SIMPLE_LOGO)));

    if (_chkHideWarnings.isSelected()) {
      projectConfig.setIgnoreAssociationInversionWarning(true);
      config.save();
    }

    return JOptionPane.YES_OPTION == selectedOption;
  }

  public static boolean showFixStereotypesWarningDialog() {
    final ViewManager vm = ApplicationManager.instance().getViewManager();
    final JLabel message =
        new JLabel(
            "<html>This action will affect all elements in your model<br>"
                + "changing stereotypes and inverting associations<br>"
                + "whenever inconsistencies are detected. Please<br>"
                + "beware of these changes and save your project<br>"
                + "before proceeding.<br><br>"
                + "Do you wish to continue?</html>");

    int selectedOption =
        vm.showConfirmDialog(
            vm.getRootFrame(),
            message,
            "Fix OntoUML Stereotypes Warning",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            new ImageIcon(getFilePath(SIMPLE_LOGO)));

    return JOptionPane.YES_OPTION == selectedOption;
  }
}
