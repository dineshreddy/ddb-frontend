/*
 * Copyright (C) 2013 FIZ Karlsruhe
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
$(document).ready(function(){
  
  /** Fade in overlay when social icon is clicked **/
  $(".social-entry").click(function() {
    $(".social-overlay-container").fadeIn(200);
  });

  /** Fade out overlay when overlay div is leaved **/
  $(".social-overlay-container").mouseleave(function() {
    window.setTimeout(function(){
      $(".social-overlay-container").fadeOut(200);
    }, 200);
  });
  
  $(".social-accept").click(function(){
    $(".social-overlay-container").fadeOut(100);
    $(".social-locked").css("display", "none");
    $(".social-open").css("display", "block");
  });

});