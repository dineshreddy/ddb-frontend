<table border="0" cellpadding="0" cellspacing="0" width="100%" class="slide-viewer item-detail">
  <tr>
    <td id="binary-viewer"><rendering:inlineJpeg bytes="${binariesListViewerContent}" /></td>
  </tr>
  <tr>
    <td class="slice-viewer-tabsection">
      <!--  This section should contain info on the first viewed Item -->
      <div class="binary-title">
        <span>${binaryList.first().full.title}</span>
      </div>
      <div class="binary-author">
        <span>${binaryList.first().full.author}</span>
      </div>
      <div class="binary-rights">
        <span>${binaryList.first().full.rights}</span>
      </div>
    </td>
  </tr>
</table>