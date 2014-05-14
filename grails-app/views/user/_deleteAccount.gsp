<%--
Copyright (C) 2014 FIZ Karlsruhe
 
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
<div id="msDeleteAccount" class="modal hide fade" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-header">
    <span title="<g:message encodeAs="html" code="ddbnext.Close" />" data-dismiss="modal" class="fancybox-toolbar-close"></span>
    <h3>
      <g:message encodeAs="html" code="ddbnext.delete_confirmation" />
    </h3>
  </div>
  <div class="modal-body">
    <g:message encodeAs="html" code="ddbnext.User.Really_Delete_Account" />
  </div>
  <div class="modal-footer">
    <button class="submit" data-dismiss="modal" id="delete-account-confirm"><g:message encodeAs="html" code="ddbnext.Ok" /></button>
    <button class="submit" data-dismiss="modal"><g:message encodeAs="html" code="ddbnext.Cancel" /></button>
  </div>
</div>