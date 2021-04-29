function renderRecipeViolationTable(tableDivId, buildAction) {
  function ajaxRequestFunc(url, config, params) {
    var self = this;

    return new Promise(function (resolve, reject) {
      buildAction.getRecipeViolationTableModel(params.page, params.size, function (model) {
        console.log(model.responseJSON);
        resolve(model.responseJSON);
      });
    });
  }

  var table = new Tabulator(tableDivId,
    {
      rowClick: function(e, row) {
        console.log(row.getData().name);
        window.location.href = row.getData().name;
      },
      pagination: "remote",
      paginationSize: 10,
      ajaxRequestFunc: ajaxRequestFunc,
      ajaxURL: "meta-shift", // just trigging ajaxRequestFunc
      layout:"fitColumns", //fit columns to width of table (optional)
      columns:[ //Define Table Columns
        { title:"File", field:"file", widthGrow:1},
        { title:"Line", field:"line", widthGrow:1},
        { title:"Rule", field:"rule", widthGrow:1},
        { title:"Description", field:"description", widthGrow:1},
        { title:"Severity", field:"severity", widthGrow:1},
      ]
    });
}
