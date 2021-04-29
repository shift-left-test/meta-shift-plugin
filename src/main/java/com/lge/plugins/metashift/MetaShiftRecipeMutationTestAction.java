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

package com.lge.plugins.metashift;

import com.lge.plugins.metashift.models.factory.MutationTestFactory;
import hudson.model.Action;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * MetaShift recipe's mutation test detail view action class.
 */
public class MetaShiftRecipeMutationTestAction
    extends MetaShiftRecipeActionChild implements Action {
  
  public MetaShiftRecipeMutationTestAction(MetaShiftRecipeAction parent) {
    super(parent);
  }

  @Override
  public String getIconFileName() {
    return "document.png";
  }

  @Override
  public String getDisplayName() {
    return "Mutation Test";
  }

  @Override
  public String getUrlName() {
    return "mutation_test";
  }

  /**
   * return paginated mutation test list.
   *
   * @param pageIndex page index
   * @param pageSize page size
   * @return mutation test list
   * @throws IOException invalid recipe uri
   */
  @JavaScriptMethod
  public JSONObject getMutationTestTableModel(int pageIndex, int pageSize)
      throws IOException {
    if (getParentAction().getMetrics().getMutationTest().isAvailable()) {
      List<?> mutationTestDataList = MutationTestFactory.create(
        new File(this.getParentAction().getRecipeUri()));

      return getPagedDataList(pageIndex, pageSize, mutationTestDataList);
    } else {
      return null;
    }
  }
}
