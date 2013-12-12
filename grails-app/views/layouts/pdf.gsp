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
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="${ddb.getCurrentLocale()}">
  <head>

    <title><g:layoutTitle default="Deutsche Digitale Bibliothek" /></title>

    <meta name="description" content="Deutsche Digitale Bibliothek" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default" />

    <!-- TODO: uncomment the next two lines, once we find the icons -->
    <!-- link rel="apple-touch-icon" href="/apple-touch-icon.png"/ -->
    <!-- link rel="apple-touch-startup-image" href="320x460-ipad1004x768.png"/ -->

    <r:require module="ddbnext" />
    <r:layoutResources />
    <g:layoutHead />

  </head>
  <body>
    <g:render template="/pdfHeader" />
    <div id="main-container" class="container" role="main">
      <g:layoutBody/>
    </div>
    <g:render template="/pdfFooter" />
    <r:layoutResources />
  </body>
</html>
