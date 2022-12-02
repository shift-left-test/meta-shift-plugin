package com.lge.plugins.metashift.ui.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.lge.plugins.metashift.fixture.FakeRecipe;
import com.lge.plugins.metashift.fixture.FakeReportBuilder;
import com.lge.plugins.metashift.fixture.FakeScript;
import com.lge.plugins.metashift.fixture.FakeSource;
import com.lge.plugins.metashift.ui.project.MetaShiftPublisher;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.io.File;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

public class BuildActionChildTest {

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

  private JSONObject makeTreeMapModel(JSONArray treeMap, JSONArray tooltip) {
    JSONObject model = new JSONObject();
    model.put("data", treeMap);
    model.put("tooltipInfo", tooltip);

    return model;
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
    List<BuildActionChild> children = buildAction.getActions(BuildActionChild.class);

    // premirror_cache
    BuildActionChild premirrorCacheAction = children.stream()
        .filter(o -> o.getUrlName() == "premirror_cache")
        .findFirst().orElse(null);
    assertNotNull(premirrorCacheAction);
    assertEquals("Premirror Cache", premirrorCacheAction.getDisplayName());
    assertEquals(buildAction.getReport().getPremirrorCache(), premirrorCacheAction.getGroup());
    assertEquals("80%", premirrorCacheAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getPremirrorCache().getTreemap(),
            buildAction.getReport().getPremirrorCache().getSummaries()),
        premirrorCacheAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getPremirrorCache().getSummaries(),
        premirrorCacheAction.getRecipesTableModel());

    // shared_state_cache
    BuildActionChild sharedStateCacheAction = children.stream()
        .filter(o -> o.getUrlName() == "shared_state_cache")
        .findFirst().orElse(null);
    assertNotNull(sharedStateCacheAction);
    assertEquals("Shared State Cache", sharedStateCacheAction.getDisplayName());
    assertEquals(buildAction.getReport().getSharedStateCache(), sharedStateCacheAction.getGroup());
    assertEquals("80%", sharedStateCacheAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getSharedStateCache().getTreemap(),
            buildAction.getReport().getSharedStateCache().getSummaries()),
        sharedStateCacheAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getSharedStateCache().getSummaries(),
        sharedStateCacheAction.getRecipesTableModel());

    // recipe_violations
    BuildActionChild recipeViolationsAction = children.stream()
        .filter(o -> o.getUrlName() == "recipe_violations")
        .findFirst().orElse(null);
    assertNotNull(recipeViolationsAction);
    assertEquals("Recipe Violations", recipeViolationsAction.getDisplayName());
    assertEquals(buildAction.getReport().getRecipeViolations(), recipeViolationsAction.getGroup());
    assertEquals("0.10", recipeViolationsAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getRecipeViolations().getTreemap(),
            buildAction.getReport().getRecipeViolations().getSummaries()),
        recipeViolationsAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getRecipeViolations().getSummaries(),
        recipeViolationsAction.getRecipesTableModel());

    // comments
    BuildActionChild commentsAction = children.stream().filter(o -> o.getUrlName() == "comments")
        .findFirst().orElse(null);
    assertNotNull(commentsAction);
    assertEquals("Comments", commentsAction.getDisplayName());
    assertEquals(buildAction.getReport().getComments(), commentsAction.getGroup());
    assertEquals("20%", commentsAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getComments().getTreemap(),
            buildAction.getReport().getComments().getSummaries()),
        commentsAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getComments().getSummaries(),
        commentsAction.getRecipesTableModel());

    // code_violations
    BuildActionChild codeViolationsAction = children.stream()
        .filter(o -> o.getUrlName() == "code_violations")
        .findFirst().orElse(null);
    assertNotNull(codeViolationsAction);
    assertEquals("Code Violations", codeViolationsAction.getDisplayName());
    assertEquals(buildAction.getReport().getCodeViolations(), codeViolationsAction.getGroup());
    assertEquals("0.10", codeViolationsAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getCodeViolations().getTreemap(),
            buildAction.getReport().getCodeViolations().getSummaries()),
        codeViolationsAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getCodeViolations().getSummaries(),
        codeViolationsAction.getRecipesTableModel());

    // complexity
    BuildActionChild complexityAction = children.stream()
        .filter(o -> o.getUrlName() == "complexity")
        .findFirst().orElse(null);
    assertNotNull(complexityAction);
    assertEquals("Complexity", complexityAction.getDisplayName());
    assertEquals(buildAction.getReport().getComplexity(), complexityAction.getGroup());
    assertEquals("10%", complexityAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getComplexity().getTreemap(),
            buildAction.getReport().getComplexity().getSummaries()),
        complexityAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getComplexity().getSummaries(),
        complexityAction.getRecipesTableModel());

    // duplications
    BuildActionChild duplicationsAction = children.stream()
        .filter(o -> o.getUrlName() == "duplications")
        .findFirst().orElse(null);
    assertNotNull(duplicationsAction);
    assertEquals("Duplications", duplicationsAction.getDisplayName());
    assertEquals(buildAction.getReport().getDuplications(), duplicationsAction.getGroup());
    assertEquals("10%", duplicationsAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getDuplications().getTreemap(),
            buildAction.getReport().getDuplications().getSummaries()),
        duplicationsAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getDuplications().getSummaries(),
        duplicationsAction.getRecipesTableModel());

    // unit_tests
    BuildActionChild unitTestsAction = children.stream().filter(o -> o.getUrlName() == "unit_tests")
        .findFirst().orElse(null);
    assertNotNull(unitTestsAction);
    assertEquals("Unit Tests", unitTestsAction.getDisplayName());
    assertEquals(buildAction.getReport().getUnitTests(), unitTestsAction.getGroup());
    assertEquals("95%", unitTestsAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getUnitTests().getTreemap(),
            buildAction.getReport().getUnitTests().getSummaries()),
        unitTestsAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getUnitTests().getSummaries(),
        unitTestsAction.getRecipesTableModel());

    // statement_coverage
    BuildActionChild statementCoverageAction = children.stream()
        .filter(o -> o.getUrlName() == "statement_coverage")
        .findFirst().orElse(null);
    assertNotNull(statementCoverageAction);
    assertEquals("Statement Coverage", statementCoverageAction.getDisplayName());
    assertEquals(buildAction.getReport().getStatementCoverage(),
        statementCoverageAction.getGroup());
    assertEquals("80%", statementCoverageAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getStatementCoverage().getTreemap(),
            buildAction.getReport().getStatementCoverage().getSummaries()),
        statementCoverageAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getStatementCoverage().getSummaries(),
        statementCoverageAction.getRecipesTableModel());

    // branch_coverage
    BuildActionChild branchCoverageAction = children.stream()
        .filter(o -> o.getUrlName() == "branch_coverage")
        .findFirst().orElse(null);
    assertNotNull(branchCoverageAction);
    assertEquals("Branch Coverage", branchCoverageAction.getDisplayName());
    assertEquals(buildAction.getReport().getBranchCoverage(), branchCoverageAction.getGroup());
    assertEquals("40%", branchCoverageAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getBranchCoverage().getTreemap(),
            buildAction.getReport().getBranchCoverage().getSummaries()),
        branchCoverageAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getBranchCoverage().getSummaries(),
        branchCoverageAction.getRecipesTableModel());

    // mutation_tests
    BuildActionChild mutationTestsAction = children.stream()
        .filter(o -> o.getUrlName() == "mutation_tests")
        .findFirst().orElse(null);
    assertNotNull(mutationTestsAction);
    assertEquals("Mutation Tests", mutationTestsAction.getDisplayName());
    assertEquals(buildAction.getReport().getMutationTests(), mutationTestsAction.getGroup());
    assertEquals("85%", mutationTestsAction.getThresholdString());
    assertEquals(makeTreeMapModel(buildAction.getReport().getMutationTests().getTreemap(),
            buildAction.getReport().getMutationTests().getSummaries()),
        mutationTestsAction.getRecipesTreemapModel());
    assertEquals(buildAction.getReport().getMutationTests().getSummaries(),
        mutationTestsAction.getRecipesTableModel());
  }
}
