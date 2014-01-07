<%--
Copyright (C) 2013 FIZ Karlsruhe
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<div class="thumb">
  <div class="thumbinner">
    <a href="${entity.person.depiction.thumbnail}" class="image no-external-link-icon">
      <!-- TODO: refactor to use figure element -->
      <!-- TODO: remove width and height, use CSS -->
      <!-- TODO srcset is not a valid attribute -->
      <img alt="${entity.title}" src="${entity.person.depiction.thumbnail}" width="220" height="271" class="thumbimage">
    </a>
    <div class="thumbcaption">
      <i>${entity.person.preferredName}</i>
      <br>
      ${entity.person.depiction.decription}
    </div>
  </div>
</div>
