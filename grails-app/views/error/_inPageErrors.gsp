<div class="errors-container fullwidth-container span12">
  <div>
    <ul class="unstyled">
    <g:each in="${ errors }">
      <li>
        <span><g:message code="${it}" /></span>
      </li>
    </g:each>
    </ul>
  </div>
</div>
