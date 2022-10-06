/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

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
   * @param {string} key
   * @param {unknown} evaluation
   * @return {string}
   */
  private generateQualifiedTooltipString(key, evaluation) {
    return `<div>
      <div class="tooltip-qualified-key">${key}:</div>
      ${evaluation.available ?
        `<div class="tooltip-qualified-value ${evaluation.qualified ?
        'good' : 'bad'}">${evaluation.qualified ? 'PASS' : 'FAIL'}</div>` :
        `<div class="tooltip-qualified-value">N/A</div>`}
    </div>`;
  }

  /**
   * return tooltip string with metrics.
   * @param {unknown} tooltipInfo
   * @return {string}
   */
  private qualifiedTooltip(tooltipInfo) {
    return `
    <div class="tooltip-column">
      <div class="tooltip-section">Build System</div>
      ${this.generateQualifiedTooltipString('Premirror Cache',
      tooltipInfo.premirrorCache)}
      ${this.generateQualifiedTooltipString('Shared State Cache',
      tooltipInfo.sharedStateCache)}
      ${this.generateQualifiedTooltipString('Recipe Violations',
      tooltipInfo.recipeViolations)}
      </div>
    <div class="tooltip-column-gap"></div>
    <div class="tooltip-column">
      <div class="tooltip-section">Static Analysis</div>
      ${this.generateQualifiedTooltipString('Comments',
      tooltipInfo.comments)}
      ${this.generateQualifiedTooltipString('Code Violations',
      tooltipInfo.codeViolations)}
      ${this.generateQualifiedTooltipString('Complexity',
      tooltipInfo.complexity)}
      ${this.generateQualifiedTooltipString('Duplications',
      tooltipInfo.duplications)}
      </div>
    <div class="tooltip-column-gap"></div>
    <div class="tooltip-column">
      <div class="tooltip-section">Dynamic Testing</div>
      ${this.generateQualifiedTooltipString('Unit Tests',
      tooltipInfo.unitTests)}
      ${this.generateQualifiedTooltipString('Statement Coverage',
      tooltipInfo.statementCoverage)}
      ${this.generateQualifiedTooltipString('Branch Coverage',
      tooltipInfo.branchCoverage)}
      ${this.generateQualifiedTooltipString('Mutation Tests',
      tooltipInfo.mutationTests)}
    </div>`;
  }
}
