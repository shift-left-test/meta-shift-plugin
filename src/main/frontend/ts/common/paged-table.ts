import {html, LitElement} from 'lit';
import {customElement, query} from 'lit/decorators.js';
import Tabulator from 'tabulator-tables';

@customElement('paged-table')
/**
 * recipes table.
 */
export class PagedTable extends LitElement {
  @query('.paged-table') recipesTable

  protected tabulatorTable: Tabulator;
  protected columns;
  protected index; // 'id' field for the data.
  protected hasRowClick;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.hasRowClick = true;
    this.columns = [];
  }

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
    return html`<div class="paged-table"></div>`;
  }

  /**
   * first updated
   */
  firstUpdated() : void {
    this.tabulatorTable = new Tabulator(this.recipesTable, {
      selectable: this.hasRowClick,
      rowClick: this.hasRowClick ? this._handleRowClicked.bind(this) : null,
      pagination: 'local',
      paginationSize: 10,
      layout: 'fitColumns',
      columns: this.columns,
      tooltipsHeader: true,
      index: this.index,
    });
  }

  /**
   * set ajax func.
   * @param {unknown} requestDataFunc
   */
  setAjaxFunc(requestDataFunc: (callback) => void) : void {
    const that = this;

    requestDataFunc(function(model) {
      that.tabulatorTable.setData(model.responseJSON);
      that.onTableUpdated();
    });
  }

  /**
   * handle row click event.
   * @param {unknown} e
   * @param {unknown} row
   */
  _handleRowClicked(e, row) {
    // TODO
  }

  /**
   * callback table data updated
   */
  onTableUpdated() {
    this.tabulatorTable.setSort([{column: 'name', dir: 'asc'}]);
  }


  /**
   * line of code formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected localeNumberString(cell: any) : unknown {
    return `<div class="locale-number">
      ${cell.getValue().toLocaleString()}</div>`;
  }

  /**
   * density formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected floatNumberString(cell: any) : unknown {
    return `<div class="locale-number">
      ${cell.getValue().toFixed(2)}</div>`;
  }

  /**
   * qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifierCellformatter(cell) {
    let available = false;
    let ratio = 0;

    if (cell.getValue()) {
      available = cell.getValue().available;
      ratio = cell.getValue().ratio;
    }

    if (available) {
      return `
      <div class="progress-bar-legend">
          ${ratio.toFixed(2)}
      </div>
      `;
    } else {
      return `<div class="progress-bar-legend">N/A</div>`;
    }
  }

  /**
   * percent type qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifierPercentCellformatterPositive(cell) {
    let available = false;
    let ratio = 0;

    if (cell.getValue()) {
      available = cell.getValue().available;
      ratio = cell.getValue().ratio * 100;
    }

    if (available) {
      return `
      <div class="progress">
        <div class="progress-bar positive"
          role="progressbar" style="width: ${ratio}%">
        </div>
        <div class="progress-bar-legend">${Math.floor(ratio)}% </div>
      </div>
      `;
    } else {
      return `<div class="progress-bar-legend">N/A</div>`;
    }
  }

  /**
   * percent type qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifierPercentCellformatterNegative(cell) {
    let available = false;
    let ratio = 0;

    if (cell.getValue()) {
      available = cell.getValue().available;
      ratio = cell.getValue().ratio * 100;
    }

    if (available) {
      return `
      <div class="progress">
        <div class="progress-bar negative"
          role="progressbar" style="width: ${ratio}%">
        </div>
        <div class="progress-bar-legend">${Math.floor(ratio)}% </div>
      </div>
      `;
    } else {
      return `<div class="progress-bar-legend">N/A</div>`;
    }
  }

  /**
   * percent type qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifiedCellformatter(cell) {
    if (cell.getValue()) {
      return `<div class="qualified-check"><span class="qualified-pass">
      <i class="fas fa-check"></i>
      </span></div>`;
    } else {
      return `<div class="qualified-check"><span class="qualified-fail">
      <i class="fas fa-times"></i>
      </span></div>`;
    }
  }

  /**
   * sort function for qualifier.
   * @param {unknown} a
   * @param {unknown} b
   * @return {unknown}
   */
  protected qualifierSorter(a, b) {
    return a.ratio - b.ratio;
  }

  /**
   * tootip for qualifier cell.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifierTooltip(cell: any) {
    if (cell.getValue().available) {
      return cell.getValue().numerator.toLocaleString() +
        ' / ' +
        cell.getValue().denominator.toLocaleString();
    }

    return 'N/A';
  }
}
