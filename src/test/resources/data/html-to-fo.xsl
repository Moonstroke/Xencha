<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="a4" page-height="29.7cm" page-width="21cm">
					<fo:region-body margin="25mm"/>
					<fo:region-before extent="2cm" margin-top="5mm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:apply-templates/>
		</fo:root>
	</xsl:template>

	<xsl:template match="html">
		<fo:page-sequence master-reference="a4">
			<fo:static-content flow-name="xsl-region-before">
				<xsl:apply-templates select="head/title"/>
			</fo:static-content>
			<xsl:apply-templates select="body"/>
		</fo:page-sequence>
	</xsl:template>

	<xsl:template match="title">
		<fo:block text-align="center">
			<xsl:value-of select="text()"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="body">
		<fo:flow flow-name="xsl-region-body">
			<xsl:apply-templates/>
		</fo:flow>
	</xsl:template>

	<xsl:template match="h1">
		<fo:block font-weight="bold" text-decoration="underline" font-size="24pt">
			<xsl:value-of select="text()"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="p">
		<fo:block>
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>

	<xsl:template match="em|i">
		<fo:inline font-style="italic">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="strong|b">
		<fo:inline font-weight="bold">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="samp|code">
		<fo:inline font-family="monospace">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="s">
		<fo:inline text-decoration="line-through">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="del">
		<fo:inline text-decoration="line-through" color="#FF0000" background-color="#FFBFBF">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<xsl:template match="ins">
		<fo:inline text-decoration="underline" color="#00FF00" background-color="#BFFFBF">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>
</xsl:stylesheet>
