<div id="favoritesModal" class="modal" tabindex="-1" role="dialog" >
<div class="modal-body">
    <form method="POST" id="sendFavorites">
    <fieldset>
    <legend><g:message code="ddbnext.send_favorites"/></legend>
    <label><g:message code="ddbnext.Email" /></label>
    <input type="text" placeholder="Contact Email" type="email" name="email" required>
          <div class="favorites-results">
            <g:favoritesEmailResultsRender results="${[results,userName]}"></g:favoritesEmailResultsRender>
          </div> 
      <button class="btn-padding" data-dismiss="modal" aria-hidden="true"><g:message code="ddbnext.Close" /></button>
      <button class="btn-padding" type="submit" id="btnSubmit"><g:message code="ddbnext.send_now" /></button>
      </fieldset>
      </form>
  </div>
</div>