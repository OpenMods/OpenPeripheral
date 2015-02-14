<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" indent="yes" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" />

  <xsl:key name="architectures" match="/documentation/classMethods" use="@architecture" />
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
            background-color: #faf0e6;
}

.method {
            margin: 2px;
            border-width: 1px;
            border-style: solid;
            border-color: gray;
            padding: 2px;
}

.arguments {
            padding-left: 3em;
}
  </style>
  </head>
  <body>
  <h1>Your OpenPeripherals API Documentation</h1>
  <p>This documentation is specific to your mods.  If it seems empty, try using peripheral.wrap() on a few things and running some code (any code) on the terminal glasses.  This will generate more peripherals for you to use.</p>

  <!-- Table of contents -->
  <h2>Table of Contents:</h2>

  <h3>Adapters:</h3>
  <ul>
    <li>External:
      <ul>
      <xsl:for-each select="adapter[@location='external']">
        <xsl:sort select="source/text()"/>
        <li><a href = "#adapt.{@class}"><xsl:value-of select="source/text()" /></a></li>
      </xsl:for-each>
      </ul>
    </li>
    <li>Inline:
      <ul>
      <xsl:for-each select="adapter[@location='inline']">
        <xsl:sort select="source/text()"/>
        <li><a href="#adapt.{@class}"><xsl:value-of select="source/text()" /></a></li>
      </xsl:for-each>
      </ul>
    </li>
  </ul>

  <!-- Note: following line selects only first elements in key group. That way we can get unique architectures-->
  <xsl:for-each select="classMethods[generate-id()=generate-id(key('architectures', @architecture)[1])]" >
    <h3>Generated types for: <xsl:value-of select="@architecture" /></h3>
      <h4>Peripherals:</h4>
      <ul>
      <xsl:for-each select="key('architectures', @architecture)[@type='peripheral']">
        <xsl:sort select="name/text()"/>
        <li><a href="#periph.{@architecture}.{@class}"><xsl:value-of select="name/text()" /></a></li>
      </xsl:for-each>
      </ul>
      <h4>Objects:</h4>
      <ul>
      <xsl:for-each select="key('architectures', @architecture)[@type='objectr']">
        <xsl:sort select="simpleName/text()"/>
        <li><a href="#lua.{@architecture}.{@class}"><xsl:value-of select="simpleName/text()" /></a></li>
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
    <xsl:if test="target/text()"><p>Target: <code><xsl:value-of select="target/text()" /></code></p></xsl:if>
    <p>An Adapter</p>
    <p>Location: <xsl:value-of select="@location" /></p>
    <p>Type: <xsl:value-of select="@type" /></p>
    <xsl:for-each select="method">
      <xsl:sort select="@name"/>
      <div class="method">
      <xsl:for-each select="names/name">
        <h2><code><xsl:value-of select="text()"/><xsl:value-of select="../../signature/text()" /></code></h2>
      </xsl:for-each>
      <xsl:if test="@asynchronous = 'false'"><p><strong>Synchronized</strong></p></xsl:if>
      <xsl:if test="extra/description"><p><xsl:value-of select="extra/description/text()" /></p></xsl:if>
      <xsl:if test="extra/source"><p>Source: <xsl:value-of select="extra/source/text()" /></p></xsl:if>
      <xsl:if test="extra/args/e">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="extra/args/e">
        <p><code><xsl:value-of select="name" /></code>: <xsl:if test="optional">(Optional: <xsl:value-of select="optional/text()" />)</xsl:if> (<xsl:value-of select="type/text()"/>) <xsl:value-of select="description/text()" /></p>
        </xsl:for-each>
        </div>
      </xsl:if>
      <xsl:if test="extra/returnTypes/e"><p>Returns:<xsl:for-each select="extra/returnTypes/e"><xsl:text> (</xsl:text><xsl:value-of select="text()" />)</xsl:for-each></p></xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each>

  <!-- classes -->
  <h3>Peripherals:</h3>
  <xsl:for-each select="classMethods[@type='peripheral']">
    <xsl:sort select="@architecture"/>
    <div class="major" id="periph.{@architecture}.{@class}">
    <h1><xsl:value-of select="simpleName/text()" /><xsl:text> - </xsl:text><xsl:value-of select="name/text()" /></h1>
    <p>A peripheral</p>
    <p>Architecture: <xsl:value-of select="@architecture" /></p>
    <xsl:for-each select="method">
      <xsl:sort select="@name"/>
      <div class="method">
      <h2><code><xsl:value-of select="@name" /><xsl:value-of select="signature/text()" /></code></h2>
      <xsl:if test="@asynchronous = 'false'"><p><strong>Synchronized</strong></p></xsl:if>
      <xsl:if test="extra/description"><p><xsl:value-of select="extra/description/text()" /></p></xsl:if>
      <xsl:if test="extra/source"><p>Source: <xsl:value-of select="extra/source/text()" /></p></xsl:if>
      <xsl:if test="extra/args/e">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="extra/args/e">
        <p><code><xsl:value-of select="name" /></code>: <xsl:if test="optional">(Optional: <xsl:value-of select="optional/text()" />)</xsl:if> (<xsl:value-of select="type/text()"/>) <xsl:value-of select="description/text()" /></p>
        </xsl:for-each>
        </div>
      </xsl:if>
      <xsl:if test="extra/returnTypes/e"><p>Returns:<xsl:for-each select="extra/returnTypes/e"><xsl:text> (</xsl:text><xsl:value-of select="text()" />)</xsl:for-each></p></xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each>

  <h3>Scripting Objects:</h3>
  <xsl:for-each select="classMethods[@type='object']">
    <xsl:sort select="@architecture"/>
    <div class="major" id="lua.{@architecture}.{@class}">
    <h1><xsl:value-of select="simpleName/text()" /></h1>
    <p>A Script Object</p>
    <p>Architecture: <xsl:value-of select="@architecture" /></p>
    <xsl:for-each select="method">
      <xsl:sort select="@name"/>
      <div class="method">
      <h2><code><xsl:value-of select="@name" /><xsl:value-of select="signature/text()" /></code></h2>
      <xsl:if test="@asynchronous = 'false'"><p><strong>Synchronized</strong></p></xsl:if>
      <xsl:if test="extra/description"><p><xsl:value-of select="extra/description/text()" /></p></xsl:if>
      <xsl:if test="extra/source"><p>Source: <xsl:value-of select="extra/source/text()" /></p></xsl:if>
      <xsl:if test="extra/args/e">
        <p>Arguments:</p>
        <div class="arguments">
        <xsl:for-each select="extra/args/e">
        <p><code><xsl:value-of select="name" /></code>: <xsl:if test="optional">(Optional: <xsl:value-of select="optional/text()" />)</xsl:if> (<xsl:value-of select="type/text()"/>) <xsl:value-of select="description/text()" /></p>
        </xsl:for-each>
        </div>
      </xsl:if>
      <xsl:if test="extra/returnTypes/e"><p>Returns:<xsl:for-each select="extra/returnTypes/e"><xsl:text> (</xsl:text><xsl:value-of select="text()" />)</xsl:for-each></p></xsl:if>
      </div>
    </xsl:for-each>
    </div>
  </xsl:for-each>

  </body>
  </html>
  </xsl:template>
</xsl:stylesheet>
