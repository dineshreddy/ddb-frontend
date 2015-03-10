<%@page defaultCodec="none" %>
<table border="0" cellpadding="0" cellspacing="0" width="100%" class="slide-viewer item-detail">
  <tr>
    <td class="binary-viewer">
      <g:if test="${binariesListViewerContent}">
        <rendering:inlineJpeg bytes="${binariesListViewerContent}" alt=""/>
      </g:if>
    </td>
  </tr>
  <tr>
    <td>
      <!--  This section should contain info on the first viewed Item -->
      <div class="binary-title">
        <span>
          <g:if test="${!binaryList.first().preview.title.isEmpty()}">${ddbcommon.wellFormedDocFromString(text:binaryList.first().preview.title)}</g:if>
        </span>
      </div>
      <div class="binary-author">
        <span>${ddbcommon.wellFormedDocFromString(text:binaryList.first().preview.author)}</span>
      </div>
      <div class="binary-rights">
        <span> ${ddbcommon.wellFormedDocFromString(text:binaryList.first().preview.rights)}</span>
      </div>
    </td>
  </tr>
</table>