function renderMutationTestTable(tableDivId, buildAction) {
  function ajaxRequestFunc(url, config, params) {
    var self = this;

    return new Promise(function (resolve, reject) {
      buildAction.getMutationTestTableModel(params.page, params.size, function (model) {
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
        { title:"MutatedClass", field:"mutatedClass", widthGrow:1},
        { title:"MutatedMethod", field:"mutatedMethod", widthGrow:1},
        { title:"Line", field:"line", widthGrow:1},
        { title:"Mutator", field:"mutator", widthGrow:1},
        { title:"KillingTest", field:"killingTest", widthGrow:1},
      ]
    });
}
