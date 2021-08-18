import {customElement} from 'lit/decorators.js';
import {RecipeTreemap} from '../common/recipe-treemap';

@customElement('build-treemap')
/**
 * build recipe treemap.
 */
export class BuildTreemap extends RecipeTreemap {
  /**
   * tooltip generator.
   * @param {unknown} info
   * @return {unknown}
   */
  generateTooltip(info) {
    if (this.tooltipInfos !== undefined) {
      const tooltipInfo = this.tooltipInfos.find(
          (o) => o.name == info.data.name);

      if (tooltipInfo !== undefined) {
        return `<div class="tooltip-title">
          ${info.name}</div>
          <div class="tooltip-body">
          ${this.qualifiedTooltip(tooltipInfo)}<div>`;
      }
    }
    return '';
  }

  /**
   * return qualified string.
   * @param {unknown}qualified
   * @param {string} key
   * @return {string}
   */
  private generateQualifiedTooltipString(qualified, key) {
    if (qualified != undefined) {
      return `<div><div class="tooltip-qualified-key">${key}:</div>
      <div class="tooltip-qualified-value ${qualified ? 'good' : 'bad'}">
        ${qualified ? 'PASS' : 'FAIL'}</div>
      </div>`;
    } else {
      return `<div><div class="tooltip-qualified-key">${key}:</div>
      <div class="tooltip-qualified-value">N/A</div>
      </div>`;
    }
  }

  /**
   * return tooltip string with metrics.
   * @param {unknown} tooltipInfo
   * @return {string}
   */
  private qualifiedTooltip(tooltipInfo) {
    let tooltip = '';
    // build performance
    tooltip += '<div class="tooltip-column">';
    tooltip += '<div class="tooltip-section">Build Performance</div>';
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.premirrorCache.qualified, 'Premirror Cache');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.sharedStateCache.qualified, 'Shared State Cache');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.recipeViolations.qualified, 'Recipe Violations');
    tooltip += '</div>';

    tooltip += '<div class="tooltip-column-gap"></div>';

    // code quality
    tooltip += '<div class="tooltip-column">';
    tooltip += '<div class="tooltip-section">Code Quality</div>';
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.comments.qualified, 'Comments');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.codeViolations.qualified, 'Code Violations');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.complexity.qualified, 'Complexity');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.duplications.qualified, 'Duplications');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.unitTests.qualified, 'Unit Tests');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.statementCoverage.qualified, 'Statement Coverage');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.branchCoverage.qualified, 'Branch Coverage');
    tooltip += this.generateQualifiedTooltipString(
        tooltipInfo.mutationTests.qualified, 'Mutation Tests');
    tooltip += '</div>';

    return tooltip;
  }
}
