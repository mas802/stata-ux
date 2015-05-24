var topPos = -1;
var bottomPos = -1;
var smallDist = 10;
var focus = "";
var resup = false;
var selectpos = 0;

$(document).ready(
  function() {

    /*
     * add the scroll handler
     * 
     */
    $("#resdiv")
        .scroll(
            function() {

              var resdiv = $("#resdiv");
              console.log((resdiv.height() + resdiv.scrollTop()) + " - "
                  + resdiv[0].scrollHeight);

              if ($("#resdiv").scrollTop() <= 0) {
                console.log("up");
                // load more on top
                if (!resup) {
                  resup = true;
                  $.ajax(
                      {
                        url : "/results?from=" + (topPos - 10) + "&to="
                            + topPos
                      }).then(function(data) {
                    if (data.length > 0) {
                      up(data);
                    }
                    resup = false;
                  });
                }
              } else if ($("#resdiv").scrollTop() + $("#resdiv").height() > $("#resdiv")[0].scrollHeight
                  - smallDist) {
                // load more on bottom
                console.log("down");

                if (!resup) {
                  resup = true;
                  $.ajax({
                    url : "/results?from=" + (bottomPos + 1)
                  }).then(function(data) {
                    if (data.length > 0) {
                      down(data);
                    }
                    resup = false;
                  });
                }
                // $("#resdiv").animate({ scrollTop: $( "#resdiv"
                // )[0].scrollHeight - ( $( "#resdiv" ).height() +
                // smallDist+1) }, "fast");
              }
            });

    /*
     * add the suggestion update
     */
    $('#cmdtxt').on('input', function() {

      var a = document.getElementById('cmdtxt');
      $('#cmdpos').val(getSelectionStart(a));
      delay(function() {
        updateSide();
      }, 300);
      
    });

    /*
     * add the blur function
     */
    $('#cmdtxt').on('blur', function() {

      var a = document.getElementById('cmdtxt');
      $('#cmdpos').val(getSelectionStart(a));

    });

    /*
     * add the blur function
     */
    $('#cmdtxt').on('click keyup keydown mouseup', function() {

      var a = document.getElementById('cmdtxt');
      $('#cmdpos').val(getSelectionStart(a));

    });

    /*
     * add the pre focus function
     */
    $('#cmdpre').on('focus', function() {

      selectpos++;
      $("#sidebar").children()[selectpos].click();
      $('#cmdtxt').focus();

    });

    /*
     * add the post focus function
     */
    $('#cmdpost').on('focus', function() {
      
      selectpos = 0;
      $("#sidebar").children()[selectpos].click();      
      $('#cmdtxt').focus();
      
    });

    /*
     * registering the input handler
     */
    $('#inptform').submit(function() {
      
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

      return false;
    });

    /*
     * shedule updated for the results (0.5 sec)
     */
    var intervalId = setInterval(function() {

      var resdiv = $("#resdiv");
      // console.log("update " + (resdiv.height()+resdiv.scrollTop()) + "
      // - "+ resdiv[0].scrollHeight );

      if ((resdiv.height() + resdiv.scrollTop()) > resdiv[0].scrollHeight
          - (3 * smallDist)
          && !resup) {
        console.log("update me " + (bottomPos + 1));
        // load more on bottom
        // alert( bottomPos );
        resup = true;
        $.ajax({
          url : "/results?from=" + (bottomPos + 1)
        }).then(
            function(data) {
              if (data.length > 0) {
                down(data);
                $("#resdiv").animate(
                    {
                      scrollTop : $("#resdiv")[0].scrollHeight
                          - ($("#resdiv").height() + smallDist + 1)
                    }, "fast");
              }
              resup = false;
            });
      }
    }, 500);

    /*
     * load everything
     */
    $.ajax({
      url : "/results"
    }).then(
        function(data) {
          if (data.length > 0) {
            down(data);
            $("#resdiv").animate(
                {
                  scrollTop : $("#resdiv")[0].scrollHeight
                      - ($("#resdiv").height() + smallDist + 1)
                }, "fast");
          }
        });

    updateSide();

  });


/*
 * add more results on top
 */
function up(data) {
  
  $.each(data.reverse(), function(key, value) {
    $("#results").prepend(value.content);
    topPos = value.line;
    bottomPos = Math.max(value.line, bottomPos);
  });
  $("#resdiv").animate({
    scrollTop : smallDist + 1
  }, "fast");
  
}


/*
 * add more results on the bottom
 */
function down(data) {
  
  // console.log("updated");
  $.each(data, function(key, value) {
    $("#results").append(value.content);
    topPos = Math.min(value.line, topPos);
    bottomPos = value.line;
  });
  // $("#resdiv").animate({ scrollTop: $( "#resdiv" )[0].scrollHeight - ( $(
  // "#resdiv" ).height() + smallDist + 1) }, "fast");
  
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
  
  switch (what) {
  case "sidebarclick":
    $("#cmdtxt").val(urldecode(data));
    if ( focuspos > 0 ) {
      $("#cmdtxt").focus();
      $("#cmdtxt")[0].selectionStart = focuspos;
      $("#cmdtxt")[0].selectionEnd = focuspos;
      var a = document.getElementById('cmdtxt');
      $('#cmdpos').val(getSelectionStart(a));
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
 * utility function to delay updating by 300 ms
 */
var delay = (function() {
  
  var timer = 0;
  return function(callback, ms) {
    clearTimeout(timer);
    timer = setTimeout(callback, ms);
  };
  
})();

/*
 * utility function to get the current cursor position
 */
function getSelectionStart(o) {
  
  if (o.createTextRange) {
    var r = document.selection.createRange().duplicate()
    r.moveEnd('character', o.value.length)
    if (r.text == '') {
      return o.value.length;
    }
    return o.value.lastIndexOf(r.text);
  } else {
    return o.selectionStart;
  }
    
}

// f**king utility method
function urldecode(str) {
  return decodeURIComponent((str + '').replace(/\+/g, '%20'));
}
