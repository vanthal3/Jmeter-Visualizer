package hudson.plugins.visualizer;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.util.StreamTaskListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.StaplerProxy;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class JVisualizerBuildAction implements Action, StaplerProxy {
  private final AbstractBuild<?, ?> build;

  /**
   * Configured parsers used to parse reports in this build.
   * For compatibility reasons, this can be null.
   */
  private final List<JVisualizerParser> parsers;

  private transient final PrintStream hudsonConsoleWriter;

  private transient WeakReference<JVisualizerReportMap> performanceReportMap;

  private String buildNum;
  private String buildDate;

  private static final Logger logger = Logger.getLogger(JVisualizerBuildAction.class.getName());


  public JVisualizerBuildAction(EnvVars var, AbstractBuild<?, ?> pBuild, PrintStream logger,
                                List<JVisualizerParser> parsers) {
    build = pBuild;
    hudsonConsoleWriter = logger;
    this.parsers = parsers;
    this.buildNum=var.get("BUILD_NUMBER");
    this.buildDate=var.get("BUILD_ID");
  }

  public JVisualizerParser getParserByDisplayName(String displayName) {
    if (parsers != null)
      for (JVisualizerParser parser : parsers)
        if (parser.getDescriptor().getDisplayName().equals(displayName))
          return parser;
    return null;
  }



  public String getDisplayName() {
     return "Individual Build";
  }

  public String getIconFileName() {
    return "graph.gif";
  }

  public String getUrlName() {
    return "visualizerBA";
  }

  public JVisualizerReportMap getTarget() {
    return getJVisualizerReportMap();
  }

  public AbstractBuild<?, ?> getBuild() {
    return build;
  }

  PrintStream getHudsonConsoleWriter() {
    return hudsonConsoleWriter;
  }

  public JVisualizerReportMap getJVisualizerReportMap() {
    JVisualizerReportMap reportMap = null;
    WeakReference<JVisualizerReportMap> wr = this.performanceReportMap;
    if (wr != null) {
      reportMap = wr.get();
      if (reportMap != null)
        return reportMap;
    }

    try {
      reportMap = new JVisualizerReportMap(this, StreamTaskListener.fromStderr());
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error creating new JVisualizerReportMap()", e);
    }
    this.performanceReportMap = new WeakReference<JVisualizerReportMap>(
        reportMap);
    return reportMap;
  }

  public String getBuildNum(){
    return buildNum;
  }

  public String getBuildDate()
  {
    return buildDate;
  }
  public void setPerformanceReportMap(
      WeakReference<JVisualizerReportMap> performanceReportMap) {
    this.performanceReportMap = performanceReportMap;
  }
}
