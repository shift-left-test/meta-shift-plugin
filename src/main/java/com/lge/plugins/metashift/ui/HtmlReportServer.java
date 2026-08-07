/*
 * Copyright (c) 2026 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import com.lge.plugins.metashift.utils.PathUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

/**
 * Serves the raw meta-shift HTML reports (gcovr coverage, sentinel mutation testing, gtest unit
 * test) archived alongside the build's parsed data, so users no longer need the HTML Publisher
 * plugin and a Resource Root URL to view them styled.
 *
 * <p>The archived reports are self-contained pages that render themselves with inline
 * JavaScript/CSS, so they are served under a sandboxing Content-Security-Policy that allows
 * their own inline scripts/styles while isolating them in an opaque origin ({@code sandbox}
 * without {@code allow-same-origin}): the report cannot reach Jenkins cookies, the DOM, or any
 * API. This is deliberately more permissive than Jenkins' own
 * {@code DirectoryBrowserSupport.DEFAULT_CSP_VALUE}, which targets arbitrary workspace files and
 * would otherwise render these reports unstyled.</p>
 *
 * @author Sung Gon Kim
 */
public class HtmlReportServer {

  /**
   * Directory holding the archived HTML, relative to the build's root directory.
   */
  public static final String ARCHIVE_RELATIVE_PATH = "meta-shift-report/html";

  /**
   * System property that overrides {@link #REPORT_CSP} for this feature only. Checked before
   * the Jenkins-wide {@link #CSP_PROPERTY}, so tightening the Jenkins-wide policy for workspace
   * browsing does not silently blank these reports, while an administrator who wants one policy
   * everywhere can still set only the Jenkins-wide property.
   */
  private static final String PLUGIN_CSP_PROPERTY = "com.lge.plugins.metashift.reportCsp";

  /**
   * Jenkins-wide system property that overrides {@link #REPORT_CSP}. Hardcoded instead of
   * referencing {@code DirectoryBrowserSupport.CSP_PROPERTY_NAME} because that constant is
   * {@code @Restricted} and the access-modifier checker fails the build on its use; the literal
   * matches Jenkins core.
   */
  private static final String CSP_PROPERTY = "hudson.model.DirectoryBrowserSupport.CSP";

  /**
   * Default Content-Security-Policy for the served HTML report.
   */
  private static final String REPORT_CSP =
      "sandbox allow-scripts; default-src 'none'; "
          + "img-src 'self' data:; "
          + "style-src 'unsafe-inline'; "
          + "script-src 'unsafe-inline';";

  private final File root;

  /**
   * Default constructor.
   *
   * @param buildRootDir the build's root directory ({@code Run.getRootDir()})
   */
  public HtmlReportServer(final File buildRootDir) {
    this.root = archiveDir(buildRootDir);
  }

  /**
   * Returns the archive directory for the given build root, without requiring it to exist.
   *
   * @param buildRootDir the build's root directory
   * @return the archive directory
   */
  public static File archiveDir(final File buildRootDir) {
    return new File(buildRootDir, ARCHIVE_RELATIVE_PATH);
  }

  /**
   * Tests whether the given archive-relative path resolves to a servable file.
   *
   * @param relativePath archive-relative path, e.g. {@code "recipe-1.0-r0/coverage/index.html"}
   * @return true when the file exists and its extension is servable
   */
  public boolean hasFile(final String relativePath) {
    File file = resolve(relativePath);
    return file != null && file.isFile() && contentTypeOf(file.getName()) != null;
  }

  /**
   * Serves the file at {@link StaplerRequest2#getRestOfPath()}, resolved against the archive
   * root. Responds 404 when the path escapes the root, does not exist, or has a disallowed
   * extension.
   *
   * @param req stapler request
   * @param rsp stapler response
   * @throws IOException if serving fails
   */
  public void serve(final StaplerRequest2 req, final StaplerResponse2 rsp) throws IOException {
    File file = resolve(req.getRestOfPath());
    String contentType = file == null ? null : contentTypeOf(file.getName());
    if (file == null || !file.isFile() || contentType == null) {
      rsp.sendError(StaplerResponse2.SC_NOT_FOUND, "HTML report not found");
      return;
    }
    applyContentSecurityPolicy(rsp);
    rsp.setContentType(contentType);
    try (InputStream is = Files.newInputStream(file.toPath())) {
      IOUtils.copy(is, rsp.getOutputStream());
    }
  }

  /**
   * Resolves an archive-relative path to a file under {@link #root}, rejecting traversal
   * attempts both by segment inspection ({@link PathUtils#escapesRoot}) and, defensively, by
   * canonical-path containment (this method serves attacker-controlled request paths, a higher
   * risk surface than report-driven paths).
   *
   * @param relativePath archive-relative path from the request or a caller
   * @return the resolved file, or null when the path is invalid
   */
  private File resolve(final String relativePath) {
    if (relativePath == null) {
      return null;
    }
    String trimmed = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
    if (trimmed.isEmpty() || PathUtils.escapesRoot(trimmed)) {
      return null;
    }
    try {
      String rootCanonical = root.getCanonicalPath() + File.separator;
      File candidate = new File(root, trimmed);
      String candidateCanonical = candidate.getCanonicalPath();
      if (!candidateCanonical.startsWith(rootCanonical)) {
        return null;
      }
      return candidate;
    } catch (IOException ignored) {
      return null;
    }
  }

  /**
   * Maps a file name to its content type, or null when the extension is not servable.
   *
   * @param name file name
   * @return content type, or null when unsupported
   */
  private static String contentTypeOf(final String name) {
    String lower = name.toLowerCase(Locale.ROOT);
    if (lower.endsWith(".html") || lower.endsWith(".htm")) {
      return "text/html;charset=UTF-8";
    }
    if (lower.endsWith(".css")) {
      return "text/css;charset=UTF-8";
    }
    if (lower.endsWith(".js")) {
      return "text/javascript;charset=UTF-8";
    }
    if (lower.endsWith(".png")) {
      return "image/png";
    }
    if (lower.endsWith(".svg")) {
      return "image/svg+xml";
    }
    return null;
  }

  private static void applyContentSecurityPolicy(final StaplerResponse2 rsp) {
    // REPORT_CSP is a non-null constant, so neither lookup returns null; an explicitly
    // empty value disables CSP.
    String csp = System.getProperty(PLUGIN_CSP_PROPERTY, System.getProperty(CSP_PROPERTY,
        REPORT_CSP));
    if (!csp.isEmpty()) {
      rsp.setHeader("Content-Security-Policy", csp);
      rsp.setHeader("X-WebKit-CSP", csp);
      rsp.setHeader("X-Content-Security-Policy", csp);
    }
  }
}
