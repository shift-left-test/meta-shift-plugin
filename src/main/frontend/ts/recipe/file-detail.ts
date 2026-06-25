/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('file-detail')
/**
 * file detail.
 */
export class FileDetail extends LitElement {
  protected dataList;

  protected currentLine: number;

  @property() filePath;
  @property({type: Number}) scrollX;
  @property({type: Number}) scrollY;
  @property({type: Array}) protected currentDataList = [];

  /**
   * create render root.
   * @return {unknown}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * render.
   * @return {unknown}
   */
  render() : unknown {
    if (this.filePath) {
      return html`<div id="data-list-panel">
          ${this.renderDataList()}
        </div>`;
    } else {
      return html`Click on a file to view its details.`;
    }
  }

  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    return html``;
  }

  /**
   * set ajax func.
   * @param {unknown} requestFileDetailFunc
   * @param {unknown} filePath
   */
  setAjaxFunc(requestFileDetailFunc = undefined) : void {
    if (this.filePath) {
      requestFileDetailFunc(this.filePath, function(model) {
        this.setSourceFile(model.responseJSON);
        window.scroll(this.scrollX, this.scrollY);
      }.bind(this));
    }
  }

  /**
   * set source file
   * @param {unknown} response
   */
  setSourceFile(response: unknown)
    : void {
    this.dataList = response['dataList'];
    this.updateDataList(undefined);
  }

  /**
   * update data list.
   * @param {number} newLine
   */
  protected updateDataList(newLine: number) : void {
    if (newLine != undefined && newLine === this.currentLine) {
      return;
    }

    this.currentLine = newLine;
    const datas = [];

    if (this.currentLine !== undefined) {
      for (let i = 0; i < this.dataList.length; i++) {
        const data = this.dataList[i];
        if (data.line == newLine) {
          datas.push(data);
        }
      }
    }
    this.currentDataList = datas;
  }
}
