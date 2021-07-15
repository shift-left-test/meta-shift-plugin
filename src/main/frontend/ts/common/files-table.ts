import {html, LitElement} from 'lit';
import {customElement, property, query} from 'lit/decorators.js';
import Tabulator from 'tabulator-tables';

@customElement('files-table')
/**
 * files table.
 */
export class FilesTable extends LitElement {
  @property() page
  @property({type: String}) sort
  @property({type: String}) select
  @query('.files-table') filesTable


  private tabulatorTable: Tabulator;
  protected columns;
  protected defalutSortColumn;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [];
    this.page = 1;
    this.sort = '"[]"';
    this.defalutSortColumn = 'file';
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
    return html`<div class="files-table"></div>`;
  }

  /**
   * first updated
   */
  firstUpdated() : void {
    const hasRowClick = this.classList.contains('row-click');

    this.tabulatorTable = new Tabulator(this.filesTable, {
      selectable: hasRowClick,
      rowClick: hasRowClick ? this._handleFileClicked.bind(this) : null,
      pagination: 'local',
      paginationSize: 10,
      layout: 'fitColumns', // fit columns to width of table (optional)
      columns: this.columns,
      index: 'file',
    });
  }

  /**
   * handle file click event.
   * @param {unknown} e
   * @param {unknown} row
   */
  private _handleFileClicked(e, row) {
    const sorters: [] = this.tabulatorTable.getSorters();
    const sortinfo = sorters.map((o) => {
      return {dir: o['dir'], column: o['field']};
    });

    window.location.href = `.?file=${row.getData().file}` +
        `&scrollX=${window.scrollX}&scrollY=${window.scrollY}` +
        `&page=${this.tabulatorTable.getPage()}` +
        `&sort=${JSON.stringify(sortinfo)}`;
  }

  /**
   * set ajax func.
   * @param {unknown} requestFilesFunc
   */
  setAjaxFunc(requestFilesFunc: (callback) => void) : void {
    const that = this;

    requestFilesFunc(function(model) {
      that.tabulatorTable.setData(model.responseJSON);
      // to remove quotes parameter string, parse twice.
      let sortinfo = JSON.parse(JSON.parse(that.sort));
      if (sortinfo.length == 0) {
        sortinfo = [
          {column: that.defalutSortColumn, dir: 'asc'},
        ];
      }
      that.tabulatorTable.setSort(sortinfo);
      that.tabulatorTable.setPage(that.page);
      if (that.select) {
        that.tabulatorTable.selectRow(that.select);
      }
    });
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
}
