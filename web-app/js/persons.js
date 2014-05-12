/*
 * Copyright (C) 2014 FIZ Karlsruhe
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
$(function() {
  if (jsPageName === "persons") {
    var randomSeed = $('#varToJs').attr('data-random-seed');
    var pgTitle = $('#varToJs').attr('data-pgTitle');
    console.log(randomSeed)
    History.pushState({sort:randomSeed}, pgTitle, "?sort="+randomSeed);
    var socialMediaManager = new SocialMediaManager();
    socialMediaManager.integrateSocialMedia();
    $('.persons-list img').resizecrop({
      width:145,
      height:200,
      vertical:"top"
    });  
  }
});