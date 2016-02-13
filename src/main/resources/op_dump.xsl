<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" indent="yes" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" />
  <xsl:template match="/documentation">
  <html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  <title>Open Peripheral API Documentation</title>
  <style type="text/css">
body {
            background-color: white;
            color: black;
}

.major {
            margin-top: 4px;
            margin-left: 4px;
            margin-right: 4px;
            margin-bottom: 20px;
            border-width: 2px;
            border-style: solid;
            border-top-color: silver;
            border-left-color: silver;
            border-right-color: gray;
            border-bottom-color: gray;
            padding: 3px;
            background-color: #C7E5ED;
}

.method {
            margin: 2px;
            border-width: 1px;
            border-style: solid;
            border-color: gray;
            padding: 2px;
            background-color: #A2DFEF;
}

.arguments {
            padding-left: 3em;
}
  </style>
  </head>
  <body>
  <h1>Your OpenPeripherals API Documentation</h1>
  <p><strong>This documentation is specific to your mods.</strong> It is not complete - it will grow every time you attach new peripheral or call method with previously unseen type.</p>
  <p>If it seems empty, try using peripheral.wrap() on a few things and running some code (any code) on the terminal glasses. This will generate more peripherals for you to use.</p>
  <p><small>Generated in OpenPeripheralCore <xsl:value-of select="@generatedIn" /> by <xsl:value-of select="@generatedBy" /> on <xsl:value-of select="@generatedOn" /></small></p>

  <!-- Table of contents -->
  <h2>Table of Contents:</h2>
  <h3>External adapters:</h3>
  <ul>
    <xsl:for-each select="adapter">
      <xsl:sort select="source/text()"/>
      <li><a href = "#adapt.{@class}"><xsl:value-of select="source/text()" /></a></li>
    </xsl:for-each>
  </ul>

  <h3><a href="#architectures">Architectures</a></h3>
  <xsl:for-each select="architecture[@enabled='true']" >
    <xsl:variable name="arch" select="id/text()"/>
    <h3><a href="#arch.{$arch}">Generated types for <xsl:value-of select="$arch" /></a></h3>
      <h4><a href="#periph.{$arch}" >Peripherals:</a></h4>
      <ul>
      <xsl:for-each select="/documentation/classMethods[@type='peripheral'][@architecture=$arch]">
        <xsl:sort select="name/text()"/>
        <li><a href="#periph.{$arch}.{@class}"><xsl:value-of select="name/text()" /></a></li>
      </xsl:for-each>
      </ul>
      <h4><a href="#lua.{$arch}">Objects:</a></h4>
      <ul>
      <xsl:for-each select="/documentation/classMethods[@type='object'][@architecture=$arch]">
        <xsl:sort select="name/text()"/>
        <li><a href="#lua.{$arch}.{@class}"><xsl:value-of select="name/text()" /></a></li>
      </xsl:for-each>
      </ul>
  </xsl:for-each>

  <!-- Documentation body -->
  <h2>Documentation:</h2>

  <!-- Adapters -->
  <h3>Adapters:</h3>
  <xsl:for-each select="adapter">
    <div class="major" id="adapt.{@class}">
    <h1><xsl:value-of select="source/text()" /></h1>
    <p>An Adapter</p>
    <p>Target class: <code><xsl:value-of select="target/text()" /></code></p>
    <p>Source class: <code><xsl:value-of select="@class" /></code></p>
    <p>Defined in: <code><xsl:value-of select="@source" /></code></p>
    <p>Location: <xsl:value-of select="@location" /></p>
    <xsl:for-each select="method">
      <xsl:sort select="@name"/>
      <div class="method">
      <xsl:for-each select="names/name">
        <h2><code>
          <xsl:value-of select="text()"/><xsl:value-of select="../../signature/text()" />
          <xsl:if test="../../returns"> : <xsl:value-of select="../../returns/text()" /></xsl:if>
        </code></h2>
      </xsl:for-each>
      <xsl:if test="@asynchronous = 'false'"><p><strong>Synchronized</strong></p></xsl:if>
      <xsl:if test="@returnSignal"><p><strong>Return signal: </strong><code><xsl:value-of select="@returnSignal" /></code></p></xsl:if>
      <xsl:if test="description"><p><xsl:value-of select="description/text()" /></p></xsl:if>
      <xsl:if test="source"><p>Source: <xsl:value-of select="source/text()" /></p></xsl:if>
      <xsl:if test="arguments/arg">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="arguments/arg">
          <p>
            <code><xsl:value-of select="name/text()" /></code>
            (<xsl:if test="@optional='true'">optional </xsl:if>
             <xsl:if test="@nullable='true'">nullable </xsl:if>
             <xsl:if test="@variadic='true'">variadic </xsl:if>
             <xsl:value-of select="type/text()"/>)
             <xsl:if test="description">: <xsl:value-of select="description/text()" /></xsl:if>
          </p>
        </xsl:for-each>
        </div>
      </xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each>

  <!-- Architectures -->
  <h3 id="architectures">Architectures:</h3>
  <table border="1">
    <xsl:for-each select="architecture">
    <tr>
      <td><a href="#arch.{id/text()}"><xsl:value-of select="id/text()" /></a></td>
      <td>
      <xsl:choose>
        <xsl:when test="@enabled = 'true'">Enabled</xsl:when>
        <xsl:otherwise>Disabled</xsl:otherwise>
      </xsl:choose>
      </td>
    </tr>
    </xsl:for-each>
  </table>

  <xsl:for-each select="architecture[@enabled='true']">
  <xsl:variable name="arch" select="id/text()"/>

  <!-- architecture classes-->
  <h3 id="arch.{$arch}">Generated types for <xsl:value-of select="$arch" /></h3>

  <h4 id="periph.{$arch}">Peripherals:</h4>
  <xsl:for-each select="/documentation/classMethods[@type='peripheral'][@architecture=$arch]">
    <div class="major" id="periph.{$arch}.{@class}">
    <h1><xsl:value-of select="name/text()" /></h1>
    <p>A peripheral of type <code><xsl:value-of select="name/text()" /></code></p>
    <p>TileEntity id: <code><xsl:value-of select="teName/text()" /></code></p>
    <p>Architecture: <xsl:value-of select="$arch" /></p>
    <p>Generated for class <code><xsl:value-of select="@class" /></code></p>
    <xsl:if test="docText">
      <p>Included documentation: </p>
      <pre><xsl:value-of select="docText" /></pre>
    </xsl:if>
    <xsl:for-each select="method">
      <xsl:sort select="@name"/>
      <div class="method">
      <h2><code>
        <xsl:value-of select="@name" /><xsl:value-of select="signature/text()" />
        <xsl:if test="returns"> : <xsl:value-of select="returns/text()" /></xsl:if>
      </code></h2>
      <xsl:if test="@asynchronous = 'false'"><p><strong>Synchronized</strong></p></xsl:if>
      <xsl:if test="@returnSignal"><p><strong>Return signal: </strong><code><xsl:value-of select="@returnSignal" /></code></p></xsl:if>
      <xsl:if test="description"><p><xsl:value-of select="description/text()" /></p></xsl:if>
      <xsl:if test="source"><p>Source: <xsl:value-of select="source/text()" /></p></xsl:if>
       <xsl:if test="arguments/arg">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="arguments/arg">
          <p>
            <code><xsl:value-of select="name/text()" /></code>
            (<xsl:if test="@optional='true'">optional </xsl:if>
             <xsl:if test="@nullable='true'">nullable </xsl:if>
             <xsl:if test="@variadic='true'">variadic </xsl:if>
             <xsl:value-of select="type/text()"/>)
             <xsl:if test="description">: <xsl:value-of select="description/text()" /></xsl:if>
          </p>
        </xsl:for-each>
        </div>
      </xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each> <!-- peripherals -->

  <h4 id="lua.{$arch}">Scripting Objects:</h4>
  <xsl:for-each select="/documentation/classMethods[@type='object'][@architecture=$arch]">
    <div class="major" id="lua.{$arch}.{@class}">
    <h1><xsl:value-of select="name/text()" /></h1>
    <p>A Script Object of type <code><xsl:value-of select="name/text()" /></code></p>
    <p>Architecture: <xsl:value-of select="$arch" /></p>
    <p>Generated for class <code><xsl:value-of select="@class" /></code></p>
    <xsl:for-each select="method">
      <xsl:sort select="@name"/>
      <div class="method">
      <h2><code>
        <xsl:value-of select="@name" /><xsl:value-of select="signature/text()" />
        <xsl:if test="returns"> : <xsl:value-of select="returns/text()" /></xsl:if>
      </code></h2>
      <xsl:if test="@asynchronous = 'false'"><p><strong>Synchronized</strong></p></xsl:if>
      <xsl:if test="@returnSignal"><p><strong>Return signal: </strong><code><xsl:value-of select="@returnSignal" /></code></p></xsl:if>
      <xsl:if test="description"><p><xsl:value-of select="description/text()" /></p></xsl:if>
      <xsl:if test="source"><p>Source: <xsl:value-of select="source/text()" /></p></xsl:if>
      <xsl:if test="arguments/arg">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="arguments/arg">
          <p>
            <code><xsl:value-of select="name/text()" /></code>
            (<xsl:if test="@optional='true'">optional </xsl:if>
             <xsl:if test="@nullable='true'">nullable </xsl:if>
             <xsl:if test="@variadic='true'">variadic </xsl:if>
             <xsl:value-of select="type/text()"/>)
             <xsl:if test="description">: <xsl:value-of select="description/text()" /></xsl:if>
          </p>
        </xsl:for-each>
        </div>
      </xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each> <!-- objects -->
  </xsl:for-each> <!-- architecture -->
  </body>
  </html>
  </xsl:template>
</xsl:stylesheet>
