package com.lge.plugins.metashift.analysis;

import static org.junit.Assert.assertEquals;

import com.lge.plugins.metashift.models.CodeSizeData;
import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.FailedTestData;
import com.lge.plugins.metashift.models.PositiveEvaluation;
import com.lge.plugins.metashift.models.Recipe;
import com.lge.plugins.metashift.models.Recipes;
import com.lge.plugins.metashift.models.StatementCoverageData;
import org.junit.Before;
import org.junit.Test;

public class TestedRecipeEvaluatorTest {

  private Evaluator evaluator;
  private Recipes recipes;
  private Recipe recipe1;
  private Recipe recipe2;
  private Evaluation evaluation;

  @Before
  public void setUp() {
    evaluator = new TestedRecipeEvaluator();
    recipe1 = new Recipe("A-A-A");
    recipe2 = new Recipe("B-B-B");
    recipes = new Recipes();
    recipes.add(recipe1);
    recipes.add(recipe2);
    evaluation = new PositiveEvaluation(false, 0, 0, 0.0);
  }

  private void assertValues(long denominator, long numerator, double ratio) {
    evaluation = evaluator.parse(recipes);
    assertEquals(denominator, evaluation.getDenominator());
    assertEquals(numerator, evaluation.getNumerator());
    assertEquals(ratio, evaluation.getRatio(), 0.01);
  }

  @Test
  public void testParseEmptyRecipes() {
    assertValues(0, 0, 0.0);
  }

  @Test
  public void testParseRecipesNoMatchingData() {
    recipe1.add(new StatementCoverageData("A-A-A", "a.file", 1, true));

    assertValues(0, 0, 0.0);
  }

  @Test
  public void testParseRecipesWithNoTestData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    assertValues(1, 0, 0.0);
  }

  @Test
  public void testParseSingRecipe() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    recipe1.add(new FailedTestData("A-A-A", "A", "A", "A"));
    assertValues(1, 1, 1.0);
  }

  @Test
  public void testParseMultipleRecipesWithUnqualifiedData() {
    recipe1.add(new CodeSizeData("A-A-A", "a.file", 1, 1, 1));
    recipe1.add(new FailedTestData("A-A-A", "A", "A", "A"));
    recipe2.add(new CodeSizeData("B-B-B", "b.file", 1, 1, 1));
    assertValues(2, 1, 0.5);
  }
}
