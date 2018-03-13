$(document).ready(function() {
	$('#inputDate').datepicker({
    format: 'dd/mm/yyyy',
	  autoclose: true,
	});
  $('#inputDate').datepicker('update', new Date());
})
