var topPos = -1;
var bottomPos = -1;
var smallDist = 10;
var focus = "";
var resup = false;
var selectpos = 0;
var editor;

var Range = ace.require('ace/range').Range

$(document)
    .ready(
        function() {

          /*
           * add the changes function
           */
          $('#acecontent').on('click keyup keydown mouseup', function() {

            // var a = document.getElementById('cmdcontent');
            // $('#cmdpos').val(getSelectionStart(a));

            var a = document.getElementById('acecontent');
            $('#cmdpos').val(getSelectionStart(a));
            delay(function() {
              updateCmdtxt();
              updateSide();
            }, 300);

          });

          /*
           * add the pre focus function
           */
          $('#cmdpre').on('focus', function() {

            selectpos++;
            $("#sidebar").children()[selectpos].click();
            editor.focus();

          });

          /*
           * add the post focus function
           */
          $('#cmdpost').on('focus', function() {

            selectpos = 0;
            $("#sidebar").children()[selectpos].click();
            editor.focus();

          });

          editor = ace.edit("acecontent");
          editor.setTheme("ace/theme/eclipse");
          editor.getSession().setMode("ace/mode/stata");
          editor.getSession().setUseWrapMode(true);

          editor.getSession().selection.on('change', function(e) {
            delay(function() {
              updateCmdtxt();
              updateSide();
            }, 300);          
          });

          editor.getSession().selection.on('changeSelection', function(e) {
            delay(function() {
              updateCmdtxt();
              updateSide();
            }, 300);          
          });

          editor.getSession().selection.on('changeCursor', function(e) {
            delay(function() {
              updateCmdtxt();
              updateSide();
            }, 300);          
          });
          
          updateSide();

          var heightUpdateFunction = function() {

            // http://stackoverflow.com/questions/11584061/
            var newHeight = editor.getSession().getScreenLength()
                * editor.renderer.lineHeight
                + editor.renderer.scrollBar.getWidth() + 20;

            $('#acecontent').height(newHeight.toString() + "px");
            $('#acewrapper').height(newHeight.toString() + "px");

            // This call is required for the editor to fix all of
            // its inner structure for adapting to a change in size
            editor.resize();
          };

          // Set initial size to match initial content
          heightUpdateFunction();

          // Whenever a change happens inside the ACE editor, update
          // the size again
          editor.getSession().on('change', heightUpdateFunction);

        });

/*
 * set the commands
 */
function updateCmdtxt() {

  console.log("update cmdtxt");

  var sel = editor.getSelectionRange();

  $('#cmdpos').val(sel.start.column);
  $('#cmdstart').val(sel.start.row);
  $('#cmdend').val(sel.end.row);

  $("#cmdtxt").val(editor.session.getLines(sel.start.row,sel.end.row));

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

  console.log( "handle" );
  
  switch (what) {
  case "sidebarclick":
    var repl = urldecode(data) + "\n";
    var range = new Range(from,0,to+1,0);

    console.log( "handle " + range.start.row );
    console.log( "handle " + range.start.column );
    console.log( "handle " + range.end.row );
    console.log( "handle " + range.end.column );
    console.log( "handle " + repl );

    editor.session.replace( range, repl);
    
    if (focuspos > 0) {
      editor.clearSelection();
      editor.moveCursorTo(from, focuspos);
      editor.focus();
      
      delay(function() {
        updateCmdtxt();
        updateSide();
      }, 100);

    }
    
    break;
  case "sidebardblclick":
    // nothing
    break;
  default:
    alert(urldecode(data));
  }

}

/*
 * load file
 *
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
    editor.setValue(data.content);
    $("#cmdtimestamp").val(data.timestamp);
    $('#indicator').html("ok");
    editor.clearSelection();
  });

}
*/

/*
 * save file
 */
function saveFile(run) {

  console.log("save file");
  $('#indicator').html("busy save file")

  $('#cmdcontent').val( editor.getValue() );
  
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


/**
 * surround the current selection with before and after
 * 
 * cursor will end up at the end of the current selection
 * allowing to enter empty replaces
 * 
 * @param field the input/textares selector
 * @param before text to insert before
 * @param after text to insert after
 */
function surroundSelAce( field, before, after ) {

  var sel = editor.getSelectionRange();
  var txt = editor.session.getTextRange( sel );
  editor.session.replace( sel, before + txt + after );
  editor.clearSelection();
  editor.moveCursorTo( sel.start.row, sel.start.column+before.length+txt.length);
  editor.focus();

}
