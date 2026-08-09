/*
 * Copyright (c) 2026 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lge.plugins.metashift.utils.TemporaryFileUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

/**
 * Unit tests for the RawReportArchive class.
 *
 * @author Sung Gon Kim
 */
public class RawReportArchiveTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();

  private TemporaryFileUtils utils;
  private File buildRoot;
  private RawReportArchive server;
  private ByteArrayOutputStream captured;
  private StaplerRequest2 req;
  private StaplerResponse2 rsp;

  @Before
  public void setUp() throws Exception {
    utils = new TemporaryFileUtils(folder);
    buildRoot = utils.createDirectory("build");
    server = new RawReportArchive(buildRoot);
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
    return FileUtils.getFile(RawReportArchive.archiveDir(buildRoot), names);
  }

  private void write(File file, String content) throws Exception {
    FileUtils.forceMkdirParent(file);
    FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
  }

  @Test
  public void testHasDirectoryForArchivedRecipe() throws Exception {
    write(archived("recipe-1.0.0-r0", "coverage", "index.html"), "<html/>");
    assertTrue(server.hasDirectory("recipe-1.0.0-r0"));
    assertFalse(server.hasDirectory("no-such-recipe"));
    // a file is not a downloadable directory
    assertFalse(server.hasDirectory("recipe-1.0.0-r0/coverage/index.html"));
  }

  @Test
  public void testHasDirectoryRejectsTraversal() {
    assertFalse(server.hasDirectory(".."));
    assertFalse(server.hasDirectory("../.."));
  }

  @Test
  public void testServeZipStreamsEveryArchivedFile() throws Exception {
    write(archived("recipe-1.0.0-r0", "coverage", "index.html"), "<html/>");
    write(archived("recipe-1.0.0-r0", "coverage", "gcov.css"), "body{}");
    write(archived("recipe-1.0.0-r0", "checktest", "mutations.xml"), "<mutations/>");

    when(req.getRestOfPath()).thenReturn("/recipe-1.0.0-r0");

    server.serveZip(req, rsp);

    verify(rsp).setContentType("application/zip");
    verify(rsp).setHeader("Content-Disposition",
        "attachment; filename=\"recipe-1.0.0-r0.zip\"");
    Set<String> entries = new HashSet<>();
    try (ZipInputStream zip =
        new ZipInputStream(new ByteArrayInputStream(captured.toByteArray()))) {
      for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
        entries.add(entry.getName());
      }
    }
    // Not just the HTML: the stylesheet the page needs and the raw data are in there too.
    assertTrue(entries.contains("coverage/index.html"));
    assertTrue(entries.contains("coverage/gcov.css"));
    assertTrue(entries.contains("checktest/mutations.xml"));
  }

  @Test
  public void testServeZipRejectsUnknownDirectory() throws Exception {
    when(req.getRestOfPath()).thenReturn("/no-such-recipe");
    server.serveZip(req, rsp);
    verify(rsp).sendError(StaplerResponse2.SC_NOT_FOUND, "Raw report not found");
  }

}
