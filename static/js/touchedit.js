var topPos = -1;
var bottomPos = -1;
var smallDist = 10;
var focus = "";
var resup = false;
var selectpos = 0;

$(document).ready(function() {

  /*
   * add the suggestion update
   */
  $('#cmdcontent').on('input', function() {

    var a = document.getElementById('cmdcontent');
    $('#cmdpos').val(getSelectionStart(a));
    updateCmdtxt();

  });

  /*
   * add the blur function
   */
  $('#cmdcontent').on('blur', function() {

    /*
    var a = document.getElementById('cmdcontent');
    $('#cmdpos').val(0);

    $('#cmdtxt').val("")
    updateSide();
    */
    updateCmdtxt();
    updateSide();
  });

  /*
   * add the changes function
   */
  $('#cmdcontent').on('click keyup keydown mouseup', function() {

    // var a = document.getElementById('cmdcontent');
    // $('#cmdpos').val(getSelectionStart(a));

    var a = document.getElementById('cmdcontent');
    $('#cmdpos').val(getSelectionStart(a));
    delay(function() {
      updateCmdtxt();
      updateSide();
    }, 100);
    
  });

  /*
   * add the pre focus function
   */
  $('#cmdpre').on('focus', function() {

    selectpos++;
    $("#sidebar").children()[selectpos].click();
    $('#cmdcontent').focus();

  });

  /*
   * add the post focus function
   */
  $('#cmdpost').on('focus', function() {

    selectpos = 0;
    $("#sidebar").children()[selectpos].click();
    $('#cmdcontent').focus();

  });

  /*
   * registering the input handler
   */
  $('#inptform').submit(function() {

    $('#indicator').html("busy")
    var frm = document.getElementById("inptform");
    $.ajax({
      url : "/xxx",
      data : $("#inptform").serialize(),
      success : function(data) {
        // nothing
        $('#indicator').html("OK (" + data.content + ")");
        $('#cmdtxt').val("");
        updateSide();
      }

    });

    return false;
  });

  updateSide();

});

/*
 * set the commands
 */
function updateCmdtxt() {

  console.log("update cmdtxt");

  var t = $("#cmdcontent")[0];

  var selstart = t.selectionStart;
  var selend = t.selectionEnd;

  var sellinestart = 0;
  var sellineend = 0;

  var arr = t.value.split("\n");
  for (i = 0; i < arr.length; i++) {
    sellineend += arr[i].length + 1;
    if (sellineend < selstart) {
      // selection has not started yet
      sellinestart += arr[i].length + 1;
    } else if (sellineend > selend) {
      sellineend--;
      // selection ends at end of line
      break;
    }
  }

  /*
   * console.log( selstart + " - " + selend ); console.log( sellinestart + " - " +
   * sellineend ); console.log( i + " - " + arr.length + " - " + t.value.length );
   */
  $('#cmdpos').val(selstart - sellinestart);
  $('#cmdstart').val(sellinestart);
  $('#cmdend').val(sellineend);

  $("#cmdtxt").val(t.value.substring(sellinestart, sellineend));

}

/*
 * set the commands
 */
function updateSide() {

  console.log("update side");
  $.ajax({
    url : "/suggest",
    data : $("#inptform").serialize()
  }).then(function(data) {
    if (data.length > 0) {
      selectpos = 0;
      $("#sidebar").html("");
      $.each(data, function(key, value) {
        $("#sidebar").append(value.content);
      });
    }
  });

}

/*
 * callback function for sideupdates
 */
function handle(what, data, focuspos, from, to) {

  if (!from) {
    from = 0;
  }

  var content = $("#cmdcontent").val();
  var newcontent = urldecode(data);

  if (!to || to < 0) {
    to = content.length;
  }

  var rcontent = content.substring(0, from) + newcontent
      + content.substring(to, content.length);

  console.log(focuspos + " - " + from + " - " + to);

  switch (what) {
  case "sidebarclick":
    $("#cmdcontent").val(rcontent);
    if (focuspos > 0) {
      $("#cmdcontent")[0].selectionStart = (focuspos + from);
      $("#cmdcontent")[0].selectionEnd = (focuspos + from);
      $("#cmdcontent").focus();
      
      updateCmdtxt();
      updateSide();
    }
    break;
  case "sidebardblclick":
    $("#cmdtxt").val(urldecode(data));
    $("#inptform").submit();
    // $("#cmdtxt").focus();
    break;
  default:
    alert(urldecode(data));
  }

}

/*
 * load file
 */
function loadFile() {

  console.log("load file");
  $('#indicator').html("busy load file")

  $.ajax({
    url : "/loaddofile",
    data : {
      "path" : "" + $("#cmdpath").val() + ""
    }
  }).then(function(data) {
    $("#cmdfile").val(data.name);
    $("#cmdcontent").val(data.content);
    $("#cmdtimestamp").val(data.timestamp);
    $('#indicator').html("ok")
  });

}

/*
 * save file
 */
function saveFile(run) {

  console.log("save file");
  $('#indicator').html("busy save file")

  if (run) {
    $.post("/saveandrundofile", $("#editform").serialize(), function(data) {
      $('#indicator').html(data.content);
    });
  } else {
    $.post("/savedofile", $("#editform").serialize(), function(data) {
      $('#indicator').html(data.content);
    });
  }

}

/*
 * run the current selection
 */
function run() {

  $('#indicator').html("busy")
  var frm = document.getElementById("inptform");
  $.ajax({
    url : "/run",
    data : $("#inptform").serialize(),
    success : function(data) {
      // nothing
      $('#indicator').html("OK (" + data.content + ")");
      $('#cmdtxt').val("");
      updateSide();
    }

  });

}

