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

package com.lge.plugins.metashift.persistence;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.metrics.Criteria;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import hudson.FilePath;
import java.io.IOException;
import java.io.PrintStream;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Unit tests for the Parser interface.
 *
 * @author Sung Gon Kim
 */
public class ParserTest {

  @Rule
  public final TemporaryFolder folder = new TemporaryFolder();
  private DataSource dataSource;
  private final Parser parser = new Parser() {

    @Override
    public void parse(Recipes recipes, Criteria criteria, DataSource dataSource,
        PrintStream logger) {
      recipes.forEach(recipe -> parse(recipe, dataSource, logger));
    }

    private void parse(Recipe recipe, DataSource dataSource, PrintStream logger) {
      try {
        logger.printf("%s: processing", recipe.getRecipe());
        JSONObject object = new JSONObject();
        object.put("recipe", recipe.getRecipe());
        String[] tokens = recipe.getRecipe().split("-");
        dataSource.put(object, tokens[0], tokens[1], tokens[2]);
      } catch (IOException ignored) {
        logger.printf("%s: failed to process", recipe.getRecipe());
      }
    }
  };

  @Before
  public void setUp() throws IOException, InterruptedException {
    dataSource = new DataSource(new FilePath(folder.newFolder()));
  }

  @Test
  public void testFakeParser() {
    Recipes recipes = new Recipes();
    recipes.add(new Recipe("A-1.0.0-r0"));
    recipes.add(new Recipe("B-1.0.0-r0"));
    recipes.add(new Recipe("C-1.0.0-r0"));
    Criteria criteria = new Criteria();
    PrintStream logger = Mockito.mock(PrintStream.class);

    parser.parse(recipes, criteria, dataSource, logger);

    Mockito.verify(logger, Mockito.times(3))
        .printf(Mockito.eq("%s: processing"), Mockito.anyString());
    assertEquals(3, dataSource.size());
    assertEquals("A-1.0.0-r0",
        ((JSONObject) dataSource.get("A", "1.0.0", "r0")).getString("recipe"));
  }
}
