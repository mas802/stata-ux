/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
function surroundSel( field, before, after ) {
  var t = $(field)[0];

  var selstart = t.selectionStart;
  var selend = t.selectionEnd;

  var content = $(field).val();
  
  var replace = content.substring(0,selstart) + before +
         content.substring(selstart,selend) + after +
         content.substring(selend);
  
  $(field).val( replace );
  
  // alert( selstart + " - " + selend  );
  
  t.selectionStart = selend + before.length;
  t.selectionEnd = selend + before.length;
  t.focus();
  
  // updateCmdtxt();
}


// f**king utility method
function urldecode(str) {
  return decodeURIComponent((str + '').replace(/\+/g, '%20'));
}