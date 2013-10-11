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

    <a href="${entity.thumbnail.link}" class="image no-external-link-icon">
      <!-- TODO: refactor to use figure element -->
      <!-- TODO: remove width and height, use CSS -->
      <!-- TODO srcset is not a valid attribute -->
      <img alt="${entity.title}"
      src="${entity.thumbnail.link}"
      width="220" height="271" class="thumbimage"
      srcset="${entity.thumbnail.link}
      1.5x,
      ${entity.thumbnail.link}
      2x">
    </a>
    <div class="thumbcaption">
      <i>${entity.title}</i>
      <br>
      ${entity.thumbnail.caption}
    </div>

    <%--   
    <a href="//commons.wikimedia.org/wiki/File:Goethe_(Stieler_1828).jpg" class="image">
      <!-- TODO: refactor to use figure element -->
      <!-- TODO: remove width and height, use CSS -->
      <!-- TODO srcset is not a valid attribute -->
      <img alt=""
      src="//upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Goethe_%28Stieler_1828%29.jpg/220px-Goethe_%28Stieler_1828%29.jpg"
      width="220" height="271" class="thumbimage"
      srcset="//upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Goethe_%28Stieler_1828%29.jpg/330px-Goethe_%28Stieler_1828%29.jpg
      1.5x,
      //upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Goethe_%28Stieler_1828%29.jpg/440px-Goethe_%28Stieler_1828%29.jpg
      2x">
    </a>
    <div class="thumbcaption">
      <i>Johann Wolfgang von Goethe</i>,<br>
      Ölgemälde von Joseph Karl Stieler, 1828
    </div>
    --%>
  </div>
</div>
