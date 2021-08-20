import {customElement} from 'lit/decorators.js';
import {PagedTable} from '../common/paged-table';

@customElement('recipe-list')
/**
 * Recipe list element.
 */
export class RecipeList extends PagedTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'Recipes', field: 'name',
        tooltip: true,
        widthGrow: 1},
      {title: 'Lines of Code', field: 'linesOfCode',
        formatter: this.localeNumberString.bind(this)},
      {title: 'Premirror Cache', field: 'premirrorCache',
        formatter: this.qualifierPercentCellformatterPositive.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Shared State Cache', field: 'sharedStateCache',
        formatter: this.qualifierPercentCellformatterPositive.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Recipe Violations', field: 'recipeViolations',
        formatter: this.qualifierCellformatter.bind(this),
        accessorDownload:
          this.qualifierCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Comment', field: 'comments',
        formatter: this.qualifierPercentCellformatterPositive.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Code Violations', field: 'codeViolations',
        formatter: this.qualifierCellformatter.bind(this),
        accessorDownload:
          this.qualifierCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Complexity', field: 'complexity',
        formatter: this.qualifierPercentCellformatterNegative.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Duplications', field: 'duplications',
        formatter: this.qualifierPercentCellformatterNegative.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Unit Tests', field: 'unitTests',
        formatter: this.qualifierPercentCellformatterPositive.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Statement Coverage', field: 'statementCoverage',
        formatter: this.qualifierPercentCellformatterPositive.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Branch Coverage', field: 'branchCoverage',
        formatter: this.qualifierPercentCellformatterPositive.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
      {title: 'Mutation Tests', field: 'mutationTests',
        formatter: this.qualifierPercentCellformatterPositive.bind(this),
        accessorDownload:
          this.qualifierPercentCellAccessorDownload.bind(this),
        sorter: this.qualifierSorter.bind(this),
        tooltip: this.qualifierTooltip.bind(this),
        hozAlign: 'left', widthGrow: 1},
    ];
  }

  /**
   * recipe click event handler.
   * @param {unknown} e
   * @param {unknown} row
   */
  _handleRowClicked(e, row) {
    window.location.href = row.getData().name;
  }
}
