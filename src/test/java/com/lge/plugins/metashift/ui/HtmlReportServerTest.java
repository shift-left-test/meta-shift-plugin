/*
 * Copyright (c) 2026 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

/**
 * Unit tests for the HtmlReportServer class.
 *
 * @author Sung Gon Kim
 */
public class HtmlReportServerTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();

  private TemporaryFileUtils utils;
  private File buildRoot;
  private HtmlReportServer server;
  private ByteArrayOutputStream captured;
  private StaplerRequest2 req;
  private StaplerResponse2 rsp;

  @Before
  public void setUp() throws Exception {
    utils = new TemporaryFileUtils(folder);
    buildRoot = utils.createDirectory("build");
    server = new HtmlReportServer(buildRoot);
    captured = new ByteArrayOutputStream();
    req = mock(StaplerRequest2.class);
    rsp = mock(StaplerResponse2.class);
    ServletOutputStream sos = new ServletOutputStream() {
      @Override
      public void write(int b) {
        captured.write(b);
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setWriteListener(WriteListener writeListener) {
        // not used by IOUtils.copy, which writes synchronously
      }
    };
    when(rsp.getOutputStream()).thenReturn(sos);
  }

  private File archived(String... names) {
    return FileUtils.getFile(HtmlReportServer.archiveDir(buildRoot), names);
  }

  private void write(File file, String content) throws Exception {
    FileUtils.forceMkdirParent(file);
    FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
  }

  @Test
  public void testHasFileForExistingHtml() throws Exception {
    write(archived("recipe-1.0.0-r0", "coverage", "index.html"), "<html/>");
    assertTrue(server.hasFile("recipe-1.0.0-r0/coverage/index.html"));
  }

  @Test
  public void testHasFileForMissingFile() {
    assertFalse(server.hasFile("recipe-1.0.0-r0/coverage/index.html"));
  }

  @Test
  public void testHasFileRejectsTraversal() throws Exception {
    write(new File(buildRoot, "secret.txt"), "secret");
    assertFalse(server.hasFile("../secret.txt"));
    assertFalse(server.hasFile("/etc/passwd"));
  }

  @Test
  public void testHasFileRejectsDisallowedExtension() throws Exception {
    write(archived("recipe-1.0.0-r0", "coverage", "coverage.xml"), "<xml/>");
    assertFalse(server.hasFile("recipe-1.0.0-r0/coverage/coverage.xml"));
  }

  @Test
  public void testServeStreamsFileAndSetsHeaders() throws Exception {
    write(archived("recipe-1.0.0-r0", "coverage", "index.html"), "<html>hi</html>");
    when(req.getRestOfPath()).thenReturn("/recipe-1.0.0-r0/coverage/index.html");

    server.serve(req, rsp);

    verify(rsp).setContentType("text/html;charset=UTF-8");
    verify(rsp).setHeader("Content-Security-Policy",
        "sandbox allow-scripts; default-src 'none'; img-src 'self' data:; "
            + "style-src 'unsafe-inline'; script-src 'unsafe-inline';");
    assertTrue(captured.toString(StandardCharsets.UTF_8).equals("<html>hi</html>"));
  }

  @Test
  public void testServeReturns404ForMissingFile() throws Exception {
    when(req.getRestOfPath()).thenReturn("/recipe-1.0.0-r0/coverage/index.html");

    server.serve(req, rsp);

    verify(rsp).sendError(StaplerResponse2.SC_NOT_FOUND, "HTML report not found");
    verify(rsp, never()).setContentType(org.mockito.ArgumentMatchers.anyString());
  }

  @Test
  public void testServeReturns404ForTraversal() throws Exception {
    write(new File(buildRoot, "secret.txt"), "secret");
    when(req.getRestOfPath()).thenReturn("/../secret.txt");

    server.serve(req, rsp);

    verify(rsp).sendError(StaplerResponse2.SC_NOT_FOUND, "HTML report not found");
  }

  @Test
  public void testServeReturns404ForDisallowedExtension() throws Exception {
    write(archived("recipe-1.0.0-r0", "coverage", "coverage.xml"), "<xml/>");
    when(req.getRestOfPath()).thenReturn("/recipe-1.0.0-r0/coverage/coverage.xml");

    server.serve(req, rsp);

    verify(rsp).sendError(StaplerResponse2.SC_NOT_FOUND, "HTML report not found");
  }

  @Test
  public void testPluginCspPropertyOverridesDefault() throws Exception {
    write(archived("recipe-1.0.0-r0", "coverage", "index.html"), "<html/>");
    when(req.getRestOfPath()).thenReturn("/recipe-1.0.0-r0/coverage/index.html");
    System.setProperty("com.lge.plugins.metashift.reportCsp", "default-src 'self'");
    try {
      server.serve(req, rsp);
      verify(rsp).setHeader("Content-Security-Policy", "default-src 'self'");
    } finally {
      System.clearProperty("com.lge.plugins.metashift.reportCsp");
    }
  }
}
