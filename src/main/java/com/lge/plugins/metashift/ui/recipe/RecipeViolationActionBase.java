/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lge.plugins.metashift.ui.recipe;

import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.ViolationData;
import com.lge.plugins.metashift.ui.models.StatisticsItemList;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * violation detail view action class base.
 */
public abstract class RecipeViolationActionBase<T extends ViolationData<?>>
    extends RecipeActionChild {

  static final String STORE_KEY_FILEVIOLATIONLIST = "FileViolationList";

  // statistics data
  private final long majorCount;
  private final long minorCount;
  private final long infoCount;

  /**
   * constructor.
   *
   * @param parent   parent action
   * @param listener logger
   * @param recipe   recipe
   * @param metadata metadata
   */
  public RecipeViolationActionBase(
      RecipeAction parent, VirtualChannel channel, JSONObject metadata,
      String name, String url, boolean percentScale,
      TaskListener listener, Recipe recipe, Class<T> type) {
    super(parent, channel, metadata, name, url, percentScale);
    List<T> violationDataList =
        recipe.objects(type).collect(Collectors.toList());

    // get data for statistics
    this.majorCount = violationDataList.stream().filter(
        o -> o.getLevel().equals("MAJOR")).count();
    this.minorCount = violationDataList.stream().filter(
        o -> o.getLevel().equals("MINOR")).count();
    this.infoCount = violationDataList.stream().filter(
        o -> o.getLevel().equals("INFO")).count();

    HashMap<String, List<T>> fileViolationList = new HashMap<>();

    for (T violationData : violationDataList) {
      String file = violationData.getFile();

      if (!fileViolationList.containsKey(file)) {
        fileViolationList.put(file, new ArrayList<>());
      }

      fileViolationList.get(file).add(violationData);
    }

    JSONArray fileViolationArray = new JSONArray();

    fileViolationList.forEach((file, violationList) -> {
      JSONObject fileViolation = new JSONObject();
      fileViolation.put("file", file);
      fileViolation.put("major",
          violationList.stream().filter(o -> o.getLevel().equals("MAJOR")).count());
      fileViolation.put("minor",
          violationList.stream().filter(o -> o.getLevel().equals("MINOR")).count());
      fileViolation.put("info",
          violationList.stream().filter(o -> o.getLevel().equals("INFO")).count());
      fileViolationArray.add(fileViolation);

      try {
        this.saveFileContents(file);
        this.getDataSource().put(violationList,
            this.getParentAction().getName(), this.getDisplayName(),
            file, STORE_KEY_FILEVIOLATIONLIST);
      } catch (IOException | InterruptedException e) {
        listener.getLogger().println(e.getMessage());
        e.printStackTrace(listener.getLogger());
      }
    });

    try {
      this.setTableModelJson(fileViolationArray);
    } catch (IOException e) {
      listener.getLogger().println(e.getMessage());
      e.printStackTrace(listener.getLogger());
    }
  }

  @Override
  public JSONArray getStatistics() {
    long allCount = majorCount + minorCount + infoCount;

    StatisticsItemList stats = new StatisticsItemList();
    stats.addItem("Major", "major",
        allCount > 0 ? majorCount * 100 / allCount : 0,
        majorCount);
    stats.addItem("Minor", "minor",
        allCount > 0 ? minorCount * 100 / allCount : 0,
        minorCount);
    stats.addItem("Info", "informational",
        allCount > 0 ? infoCount * 100 / allCount : 0,
        infoCount);

    return stats.toJsonArray();
  }

  /**
   * return file contents and violation list for that file.
   *
   * @param file file path
   * @return json object
   */
  @JavaScriptMethod
  public JSONObject getFileViolationDetail(String file) {
    JSONObject result = new JSONObject();

    List<T> violationDataList = this.getDataSource().get(
        this.getParentAction().getName(), this.getDisplayName(),
        file, STORE_KEY_FILEVIOLATIONLIST);

    result.put("dataList", violationDataList);
    result.put("content", this.readFileContents(file));

    return result;
  }
}
