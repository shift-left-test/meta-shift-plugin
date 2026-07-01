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

  @Test
  public void testCreate() throws Exception {
    fakeRecipe
        .add(new FakeScript(10, 1, 2, 3))
        .add(new FakeSource(10, 4, 5, 6)
            .setTests(1, 2, 3, 4)
            .setStatementCoverage(1, 2)
            .setBranchCoverage(3, 4)
            .setMutationTests(1, 2, 3));
    builder.add(fakeRecipe);
    builder.toFile(report);

    FreeStyleBuild run = jenkins.buildAndAssertStatus(Result.SUCCESS, project);
    BuildAction buildAction = run.getAction(BuildAction.class);
    List<BuildActionChild> children = buildAction.getActions(BuildActionChild.class);

    // unit_tests
    BuildActionChild unitTestsAction = children.stream().filter(o -> o.getUrlName() == "unit_tests")
        .findFirst().orElse(null);
    assertNotNull(unitTestsAction);
    assertEquals("Unit Tests", unitTestsAction.getDisplayName());
    assertEquals(buildAction.getReport().getUnitTests(), unitTestsAction.getGroup());
    assertEquals("95%", unitTestsAction.getThresholdString());
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
    assertEquals(buildAction.getReport().getMutationTests().getSummaries(),
        mutationTestsAction.getRecipesTableModel());
  }
}
