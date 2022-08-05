package com.lge.plugins.metashift.ui.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.build.BuildAction;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;

public class RecipeActionChildTest {
  @Rule
  public final JenkinsRule jenkins = new JenkinsRule();

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private FreeStyleProject project;
  private File report;
  private FakeReportBuilder builder;
  private FakeRecipe fakeRecipe;

  @Before
  public void setUp() throws Exception {
    File workspace = new File(folder.getRoot(), "workspace");
    report = new File(workspace, "report");
    builder = new FakeReportBuilder();
    fakeRecipe = new FakeRecipe(new File(workspace, "source"));

    project = jenkins.createFreeStyleProject();
    project.setCustomWorkspace(workspace.getAbsolutePath());
    MetaShiftPublisher publisher = new MetaShiftPublisher(report.getName());
    project.getPublishersList().add(publisher);
  }

  @Test
  public void testCreate() throws Exception {
    fakeRecipe
    .add(new FakeScript(10, 1, 2, 3))
    .add(new FakeSource(10, 4, 5, 6)
        .setComplexity(10, 5, 6)
        .setCodeViolations(1, 2, 3)
        .setTests(1, 2, 3, 4)
        .setStatementCoverage(1, 2)
        .setBranchCoverage(3, 4)
        .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction = run.getAction(BuildAction.class);

    RecipeAction recipeAction = buildAction.getAction(RecipeAction.class);

    List<RecipeActionChild> children = recipeAction.getActions(RecipeActionChild.class);

    // premirror_cache
    RecipeActionChild premirrorCacheAction = children.stream().filter(o -> o.getUrlName() == "premirror_cache")
        .findFirst().orElse(null);
    assertNotNull(premirrorCacheAction);
    assertEquals("Premirror Cache", premirrorCacheAction.getDisplayName());
    assertEquals("0%", premirrorCacheAction.getScale());
    assertEquals(recipeAction.getReport().getPremirrorCache(), premirrorCacheAction.getGroup());
    assertEquals("80%", premirrorCacheAction.getThresholdString());
    assertEquals(recipeAction.getReport().getPremirrorCache().getSummaries(),
        premirrorCacheAction.getTableModel());

    // shared_state_cache
    RecipeActionChild sharedStateCacheAction = children.stream().filter(o -> o.getUrlName() == "shared_state_cache")
        .findFirst().orElse(null);
    assertNotNull(sharedStateCacheAction);
    assertEquals("Shared State Cache", sharedStateCacheAction.getDisplayName());
    assertEquals("0%", sharedStateCacheAction.getScale());
    assertEquals(recipeAction.getReport().getSharedStateCache(), sharedStateCacheAction.getGroup());
    assertEquals("80%", sharedStateCacheAction.getThresholdString());
    assertEquals(recipeAction.getReport().getSharedStateCache().getSummaries(),
        sharedStateCacheAction.getTableModel());

    // recipe_violations
    RecipeActionChild recipeViolationsAction = children.stream().filter(o -> o.getUrlName() == "recipe_violations")
        .findFirst().orElse(null);
    assertNotNull(recipeViolationsAction);
    assertEquals("Recipe Violations", recipeViolationsAction.getDisplayName());
    assertEquals("0.60", recipeViolationsAction.getScale());
    assertEquals(recipeAction.getReport().getRecipeViolations(), recipeViolationsAction.getGroup());
    assertEquals("0.10", recipeViolationsAction.getThresholdString());
    assertEquals(recipeAction.getReport().getRecipeViolations().getSummaries(),
        recipeViolationsAction.getTableModel());

    // comments
    RecipeActionChild commentsAction = children.stream().filter(o -> o.getUrlName() == "comments")
        .findFirst().orElse(null);
    assertNotNull(commentsAction);
    assertEquals("Comments", commentsAction.getDisplayName());
    assertEquals("50%", commentsAction.getScale());
    assertEquals(recipeAction.getReport().getComments(), commentsAction.getGroup());
    assertEquals("20%", commentsAction.getThresholdString());
    assertEquals(recipeAction.getReport().getComments().getSummaries(),
        commentsAction.getTableModel());

    // code_violations
    RecipeActionChild codeViolationsAction = children.stream().filter(o -> o.getUrlName() == "code_violations")
        .findFirst().orElse(null);
    assertNotNull(codeViolationsAction);
    assertEquals("Code Violations", codeViolationsAction.getDisplayName());
    assertEquals("0.60", codeViolationsAction.getScale());
    assertEquals(recipeAction.getReport().getCodeViolations(), codeViolationsAction.getGroup());
    assertEquals("0.10", codeViolationsAction.getThresholdString());
    assertEquals(recipeAction.getReport().getCodeViolations().getSummaries(),
        codeViolationsAction.getTableModel());

    // complexity
    RecipeActionChild complexityAction = children.stream().filter(o -> o.getUrlName() == "complexity")
        .findFirst().orElse(null);
    assertNotNull(complexityAction);
    assertEquals("Complexity", complexityAction.getDisplayName());
    assertEquals("45%", complexityAction.getScale());
    assertEquals(recipeAction.getReport().getComplexity(), complexityAction.getGroup());
    assertEquals("10%", complexityAction.getThresholdString());
    assertEquals(recipeAction.getReport().getComplexity().getSummaries(),
        complexityAction.getTableModel());

    // duplications
    RecipeActionChild duplicationsAction = children.stream().filter(o -> o.getUrlName() == "duplications")
        .findFirst().orElse(null);
    assertNotNull(duplicationsAction);
    assertEquals("Duplications", duplicationsAction.getDisplayName());
    assertEquals("0%", duplicationsAction.getScale());
    assertEquals(recipeAction.getReport().getDuplications(), duplicationsAction.getGroup());
    assertEquals("10%", duplicationsAction.getThresholdString());
    assertEquals(recipeAction.getReport().getDuplications().getSummaries(),
        duplicationsAction.getTableModel());

    // unit_tests
    RecipeActionChild unitTestsAction = children.stream().filter(o -> o.getUrlName() == "unit_tests")
        .findFirst().orElse(null);
    assertNotNull(unitTestsAction);
    assertEquals("Unit Tests", unitTestsAction.getDisplayName());
    assertEquals("10%", unitTestsAction.getScale());
    assertEquals(recipeAction.getReport().getUnitTests(), unitTestsAction.getGroup());
    assertEquals("95%", unitTestsAction.getThresholdString());
    assertEquals(recipeAction.getReport().getUnitTests().getSummaries(),
        unitTestsAction.getTableModel());

    // statement_coverage
    RecipeActionChild statementCoverageAction = children.stream().filter(o -> o.getUrlName() == "statement_coverage")
        .findFirst().orElse(null);
    assertNotNull(statementCoverageAction);
    assertEquals("Statement Coverage", statementCoverageAction.getDisplayName());
    assertEquals("40%", statementCoverageAction.getScale());
    assertEquals(recipeAction.getReport().getStatementCoverage(), statementCoverageAction.getGroup());
    assertEquals("80%", statementCoverageAction.getThresholdString());
    assertEquals(recipeAction.getReport().getStatementCoverage().getSummaries(),
        statementCoverageAction.getTableModel());

    // branch_coverage
    RecipeActionChild branchCoverageAction = children.stream().filter(o -> o.getUrlName() == "branch_coverage")
        .findFirst().orElse(null);
    assertNotNull(branchCoverageAction);
    assertEquals("Branch Coverage", branchCoverageAction.getDisplayName());
    assertEquals("42%", branchCoverageAction.getScale());
    assertEquals(recipeAction.getReport().getBranchCoverage(), branchCoverageAction.getGroup());
    assertEquals("40%", branchCoverageAction.getThresholdString());
    assertEquals(recipeAction.getReport().getBranchCoverage().getSummaries(),
        branchCoverageAction.getTableModel());

    // mutation_tests
    RecipeActionChild mutationTestsAction = children.stream().filter(o -> o.getUrlName() == "mutation_tests")
        .findFirst().orElse(null);
    assertNotNull(mutationTestsAction);
    assertEquals("Mutation Tests", mutationTestsAction.getDisplayName());
    assertEquals("16%", mutationTestsAction.getScale());
    assertEquals(recipeAction.getReport().getMutationTests(), mutationTestsAction.getGroup());
    assertEquals("85%", mutationTestsAction.getThresholdString());
    assertEquals(recipeAction.getReport().getMutationTests().getSummaries(),
        mutationTestsAction.getTableModel());
  }
}
