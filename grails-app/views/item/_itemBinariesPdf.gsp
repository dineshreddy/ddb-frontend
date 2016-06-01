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
      <g:if test="${binaryList.first().preview.title}">
        <div class="binary-title">
          <span>
            ${ddbcommon.wellFormedDocFromString(text: binaryList.first().preview.title)}
          </span>
        </div>
      </g:if>
      <g:if test="${binaryList.first().preview.author}">
        <div class="binary-author">
          <span>${ddbcommon.wellFormedDocFromString(text: binaryList.first().preview.author)}</span>
        </div>
      </g:if>
      <g:if test="${binaryList.first().preview.rights}">
        <div class="binary-rights">
          <span> ${ddbcommon.wellFormedDocFromString(text: binaryList.first().preview.rights)}</span>
        </div>
      </g:if>
    </td>
  </tr>
</table>