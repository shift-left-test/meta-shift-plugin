/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {html, LitElement} from 'lit';
import {customElement, property} from 'lit-element';
import {Utils} from './common/utils';

@customElement('summary-metrics-simple-view')
/**
 * summary metrics simple view
 */
export class SummaryMetricsSimpleView extends LitElement {
  @property() name
  @property() evaluation
  /**
   * constructor
   */
  constructor() {
    super();

    this.evaluation = '{"ratio": 0, "threshold": 0}';
  }

  /**
   * create render root.
   * @return {unknown}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * render
   * @return {unknown}
   */
  render() : unknown {
    const evaluator = JSON.parse(this.evaluation);
    const isPercent = this.classList.contains('percent');

    const iconClass = evaluator.available ?
          (evaluator.qualified ? 'fa-check-circle' :
            'fa-times-circle') :
            'fa-minus-circle';

    return html`
    <div class="metrics-row">
      <span class="icon"><i class="fas ${iconClass}"></i></span>
      ${this.name} :
      <b>${evaluator.available ?
        (isPercent ?
          html`${Math.floor(evaluator.ratio * 100)}%`:
          html`${Utils.toFixedFloor(evaluator.ratio)}`):
        html`N/A`}</b>
    </div>
    `;
  }
}
