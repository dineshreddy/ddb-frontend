<%@page defaultCodec="none" %>
<table border="0" cellpadding="0" cellspacing="0" width="100%" class="slide-viewer item-detail">
  <tr>
    <td class="binary-viewer"><rendering:inlineJpeg bytes="${binariesListViewerContent}" alt=""/></td>
  </tr>
  <tr>
    <td>
      <!--  This section should contain info on the first viewed Item -->
      <div class="binary-title">
        <span>
          <g:if test="${!binaryList.first().preview.title.isEmpty()}">${binaryList.first().preview.title}</g:if>
          <g:else>${itemTitle}</g:else>
        </span>
      </div>
      <div class="binary-author">
        <span>${binaryList.first().preview.author}</span>
      </div>
      <div class="binary-rights">
        <span> ${binaryList.first().preview.rights}</span>
      </div>
    </td>
  </tr>
</table>